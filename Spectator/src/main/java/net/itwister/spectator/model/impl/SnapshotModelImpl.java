package net.itwister.spectator.model.impl;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import net.itwister.spectator.BuildConfig;
import net.itwister.spectator.Constants;
import net.itwister.spectator.data.ListSnapshot;
import net.itwister.spectator.data.Screenshot;
import net.itwister.spectator.data.Snapshot;
import net.itwister.spectator.data.WebContent;
import net.itwister.spectator.data.generic.SnapshotWrapper;
import net.itwister.spectator.data.protobuf.SnapshotsResponseProtos.ProtoSnapshotsResponse;
import net.itwister.spectator.data.protobuf.SnapshotsResponseProtos.ProtoSnapshotsResponse.ProtoSnapshot;
import net.itwister.spectator.model.SnapshotModel;
import net.itwister.spectator.model.SyncModel;
import net.itwister.spectator.model.SyncModel.SyncObject;
import net.itwister.spectator.model.database.ObserverManager;
import net.itwister.spectator.model.helpers.SpectatorTask;
import net.itwister.spectator.model.web.SpectatorWebClient;
import net.itwister.tools.inner.Ln;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import bindui.extra.Task;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

public class SnapshotModelImpl extends SpectatorModel implements SnapshotModel {

	@Inject	private SpectatorWebClient web;
	@Inject	private OrmLiteSqliteOpenHelper helper;
	@Inject	private ObserverManager observer;

	@Override
	public Cursor get(int listId, String query) throws SQLException {
		return doGet(listId, query);
	}

	@Override
	public int getCount(SyncObject syncObject) throws SQLException {
        return doGetCount(syncObject);
	}

    @Override
    public Task<Integer> getCountTask(final SyncObject syncObject) {
        return new Task<Integer>() {

            @Override
            protected Integer onExecuteWithResult() throws Exception {
                return doGetCount(syncObject);
            }
        };
    }

    @Override
	public Snapshot getSnapshotByPosition(int subscriptionId, String query, int orderId) throws SQLException {
		QueryBuilder<ListSnapshot, ?> s = helper.getDao(ListSnapshot.class).queryBuilder();
		s.selectColumns(ListSnapshot.SNAPSHOT_ID);

		Where<ListSnapshot, ?> w = s.where()
				.eq(ListSnapshot.LIST_ID, subscriptionId).and()
				.eq(ListSnapshot.TYPE, 0).and();

		if (query == null) w.isNull(ListSnapshot.QUERY);
		else w.eq(ListSnapshot.QUERY, clearQuery(query));

		QueryBuilder<Snapshot, ?> q = helper.getDao(Snapshot.class).queryBuilder();
		q.orderBy(Snapshot.CREATED, false);
		q.limit(1L);
		q.offset((long) orderId);

		Where<Snapshot, ?> qw = q.where().in(Snapshot.ID, s);
		if (subscriptionId == STASH_SUBSCRIPTION_ID) {
			qw.and().raw("_id IN (SELECT snapshot_id FROM stash)");
		}

		return q.queryForFirst();
	}

	@Override
	public Task<SnapshotWrapper> getSnapshotTask(final SyncObject syncObject) {
		return new SpectatorTask<SnapshotWrapper>() {

			@Override
			public SnapshotWrapper onExecuteWithResult() throws Exception {
				return doGetSnapshot(syncObject.getSnapshotId());
			}
		};
	}

	@Override
	public Task<Cursor> getTask(final int listId, final String query) {
		return new SpectatorTask<Cursor>() {

			@Override
			public Cursor onExecuteWithResult() throws Exception {
				return doGet(listId, query);
			}
		};
	}

	@Override
	public void sync(boolean reset, int subId, String query) throws Exception {
		doSync(reset, subId, query, subId, 100);
	}

	@Override
	public void sync(boolean reset, int subId, String query, int databaseSubId, int count) throws Exception {
		doSync(reset, subId, query, databaseSubId, count);
	}

	@Override
	public Task<Void> syncTask(final SyncObject syncObject) {
		return new SpectatorTask<Void>() {

			@Override
			public void onExecute() throws Exception {
				doSync(syncObject.getSnapshotId());
			}
		};
	}

    @Override
    public Task<WebContent> getContent(final int snapshotId, final int type) {
        return new Task<WebContent>(){

            @Override
            protected WebContent onExecuteWithResult() throws Exception {
                WebContent c = new WebContent();
                c.data = type == 0 ? web.api().content(snapshotId) : web.api().contentDiff(snapshotId);
                Dao<Snapshot, Integer> dao = helper.getDao(Snapshot.class);
                Snapshot s = dao.queryForId(snapshotId);
                c.url = s.source;
                return c;
            }
        };
    }

    // ==============================================================
	// Скрытые методы
	// ==============================================================

	//	private String createUrlForFeedRequest(int subId, int toId, String query, int count, int version) {
	//		String sver = version <= 1 ? "" : "" + version;
	//		Builder b = null;
	//		if (subId == STASH_SUBSCRIPTION_ID) {
	//			b = Uri.parse(String.format(Constants.Url.URL_HOST + "/api/stash" + sver)).buildUpon();
	//		} else {
	//			b = Uri.parse(Constants.Url.URL_HOST + "/api/snapshot" + sver).buildUpon();
	//			if (subId > 0) b.appendQueryParameter("subId", "" + subId);
	//			if (!TextUtils.isEmpty(query)) b.appendQueryParameter("query", query);
	//		}
	//		if (toId > 0) b.appendQueryParameter("toId", "" + toId);
	//		if (count > 0) b.appendQueryParameter("count", "" + count);
	//		return b.toString();
	//	}

	private Cursor doGet(int listId, String query) {
		if (BuildConfig.DEBUG) Ln.d("get(listId = %s)", listId);

		List<String> args = new ArrayList<String>();
		args.add("" + listId);

		String sql = "SELECT s.*, ifnull(h.snapshot_id, 0) AS stash FROM snapshots s LEFT JOIN stash h ON h.snapshot_id = s._id WHERE s._id IN (SELECT snapshotId FROM subscriptions_snapshots WHERE subscriptionId = ?1 AND type = 0 {STASH} AND {QUERY}) ORDER BY s.created DESC";
		if (query == null) sql = sql.replace("{QUERY}", "query IS NULL");
		else {
			sql = sql.replace("{QUERY}", "query = ?2");
			args.add(clearQuery(query));
		}

		sql = sql.replace("{STASH}", listId == STASH_SUBSCRIPTION_ID ? "AND stash != 0" : "");

		Cursor c = helper.getReadableDatabase().rawQuery(sql, args.toArray(new String[0]));
		return observer.registerSnapshotList(c);
	}

	private SnapshotWrapper doGetSnapshot(int snapshotId) throws SQLException {
		SnapshotWrapper w = new SnapshotWrapper();
		w.snapshot = helper.getDao(Snapshot.class).queryForEq(Snapshot.ID, snapshotId).get(0);

		w.screenshots = new ArrayList<Screenshot>();
		Cursor c = helper.getWritableDatabase().rawQuery("SELECT image_url FROM screenshots WHERE snapshot_id = ?", new String[] { "" + snapshotId });
		try {
			c.moveToPosition(-1);
			while (c.moveToNext()) {
				w.screenshots.add(new Screenshot(c.getString(0)));
			}
		} finally {
			c.close();
		}

		try {
			w.groupTitle = DatabaseUtils.stringForQuery(helper.getReadableDatabase(),
					"SELECT title FROM subscriptions WHERE _id IN (SELECT subscription_id FROM snapshots WHERE _id = ?)",
					new String[] { "" + snapshotId });
		} catch (SQLiteDoneException e) {
			if (BuildConfig.DEBUG) e.printStackTrace();
		}

		return w;
	}

	private void doSync(boolean reset, int subId, String query, int databaseSubId, int count) throws SQLException, Exception {
		doSyncViaProtobuf(reset, subId, query, databaseSubId, count);
	}

	//	private void doSync(int snapshotId) throws InterruptedException {
	//		JsonObject json = (JsonObject) rest.getForObject(Constants.Url.SNAPSHOT, JsonElement.class, snapshotId);
	//		final JsonArray screens = json.get("j").getAsJsonArray();
	//		if (screens == null || screens.size() < 1) return;
	//
	//		throwWhenInterrupted();
	//
	//		SQLiteDatabase db = helper.getWritableDatabase();
	//		db.beginTransaction();
	//		try {
	//			db.execSQL("DELETE FROM screenshots WHERE snapshot_id = ?", new Object[] { snapshotId });
	//			for (JsonElement s : screens) {
	//				db.execSQL("INSERT INTO screenshots (snapshot_id, image_url) VALUES (?, ?)", new Object[] { snapshotId, s.getAsString() });
	//			}
	//
	//			throwWhenInterrupted();
	//
	//			db.setTransactionSuccessful();
	//		} finally {
	//			db.endTransaction();
	//		}
	//		observer.notifySnapshot();
	//	}

	private void doSync(int snapshotId) throws InterruptedException {
		ProtoSnapshot p = web.api().snapshot(snapshotId);
		if (p.getImagesCount() < 1) return;

		//		JsonObject json = (JsonObject) rest.getForObject(Constants.Url.SNAPSHOT, JsonElement.class, snapshotId);
		//		final JsonArray screens = json.get("j").getAsJsonArray();
		//		if (screens == null || screens.size() < 1) return;

		throwWhenInterrupted();

		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.execSQL("DELETE FROM screenshots WHERE snapshot_id = ?", new Object[] { snapshotId });
			//			for (JsonElement s : screens) {
			//				db.execSQL("INSERT INTO screenshots (snapshot_id, image_url) VALUES (?, ?)", new Object[] { snapshotId, s.getAsString() });
			//			}
			for (String s : p.getImagesList()) {
				db.execSQL("INSERT INTO screenshots (snapshot_id, image_url) VALUES (?, ?)", new Object[] { snapshotId, s });
			}

			throwWhenInterrupted();

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		observer.notifySnapshot();
	}

	private void doSyncViaProtobuf(final boolean reset, int subId, String query, final int databaseSubId, int count) throws SQLException, Exception {
		Snapshot snap = reset ? null : getBottomSnapshotForList(databaseSubId, query);
		final int toId = snap == null ? 0 : snap.id;
		final String fquery = clearQuery(query);

		final ProtoSnapshotsResponse data = subId == STASH_SUBSCRIPTION_ID
				? web.api().stashList(toId == 0 ? null : toId, count)
				: web.api().snapshotList(subId == 0 ? null : subId, fquery, toId == 0 ? null : toId, count);

		if (subId == STASH_SUBSCRIPTION_ID) {
			if (toId == 0) {
				//				BSONObject stash = (BSONObject) data.get("StashIds");
				//				saveStashToDatabase(stash);

				{ // saveStashToDatabase
					helper.getDao(Snapshot.class).callBatchTasks(new Callable<Void>() {

						@Override
						public Void call() throws Exception {
							SQLiteDatabase db = helper.getWritableDatabase();
							db.execSQL("DELETE FROM stash");

							for (String i : data.getStashIdsList()) {
								db.execSQL("INSERT INTO stash(snapshot_id) VALUES(?)", new Object[] { i });
							}
							return null;
						}
					});
				}
			}

			//			data = (BSONObject) data.get("Snapshots");
		}

		{ // saveSnapshotToDatabase(reset, databaseSubId, fquery, data);
			helper.getDao(Snapshot.class).callBatchTasks(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					if (reset) {
						DeleteBuilder<ListSnapshot, ?> d = helper.getDao(ListSnapshot.class).deleteBuilder();

						Where<ListSnapshot, ?> w = d.where().eq(ListSnapshot.LIST_ID, databaseSubId).and().eq(ListSnapshot.TYPE, 0).and();
						if (fquery == null) w.isNull(ListSnapshot.QUERY);
						else w.isNotNull(ListSnapshot.QUERY);

						d.delete();
					}

					for (ProtoSnapshot k : data.getSnapshotsList()) {
						//						BSONObject o = (BSONObject) snapshots.get(k);

						Snapshot s = new Snapshot();
						s.id = k.getId();
						s.source = k.getSource();
						s.thumbnail = k.getThumbnail();
						s.title = k.getTitle();
						s.updated = new Date(k.getUpdated());
						s.hasContent = k.getHasContent();
						s.subscriptionName = k.getSubscriptionName();
						s.subscriptionIcon = k.getSubscriptionIcon();
						s.hasScreenshots = k.getHasScreenshots();
						s.hasRevisions = k.getHasRevisions();
						s.subscriptionId = k.getSubscriptionId();

						helper.getDao(Snapshot.class).createOrUpdate(s);
						helper.getDao(ListSnapshot.class).create(new ListSnapshot(databaseSubId, 0, s.id, fquery));
					}
					return null;
				}
			});

			observer.notifySnapshotList();

		}
	}

	//	@Deprecated
	//	private void doSyncViaBson(boolean reset, int subId, String query, int databaseSubId, int count) throws SQLException, Exception {
	//		Snapshot snap = reset ? null : getBottomSnapshotForList(databaseSubId, query);
	//		final int toId = snap == null ? 0 : snap.id;
	//		final String fquery = clearQuery(query);
	//
	//		BSONObject data = rest.getForObject(createUrlForFeedRequest(subId, toId, fquery, count, 1), BSONObject.class);
	//
	//		if (subId == STASH_SUBSCRIPTION_ID) {
	//			if (toId == 0) {
	//				BSONObject stash = (BSONObject) data.get("StashIds");
	//				saveStashToDatabase(stash);
	//			}
	//			data = (BSONObject) data.get("Snapshots");
	//		}
	//
	//		saveSnapshotToDatabase(reset, databaseSubId, fquery, data);
	//	}

	private Snapshot getBottomSnapshotForList(final int subId, String query) throws SQLException {
		Snapshot snap;
		QueryBuilder<ListSnapshot, ?> s = helper.getDao(ListSnapshot.class).queryBuilder();
		s.selectColumns(ListSnapshot.SNAPSHOT_ID);

		Where<ListSnapshot, ?> w = s.where().eq(ListSnapshot.LIST_ID, subId).and().eq(ListSnapshot.TYPE, 0).and();
		if (query == null) w.isNull(ListSnapshot.QUERY);
		else w.eq(ListSnapshot.QUERY, clearQuery(query));

		QueryBuilder<Snapshot, ?> q = helper.getDao(Snapshot.class).queryBuilder();
		q.where().in(Snapshot.ID, s);
		q.orderBy(Snapshot.CREATED, true);

		snap = q.queryForFirst();
		return snap;
	}

	private void throwWhenInterrupted() throws InterruptedException {
		if (Thread.interrupted()) throw new InterruptedException();
	}

    private int doGetCount(SyncObject syncObject) throws SQLException {
        QueryBuilder<ListSnapshot, ?> s = helper.getDao(ListSnapshot.class).queryBuilder();
        s.selectColumns(ListSnapshot.SNAPSHOT_ID);

        Where<ListSnapshot, ?> w = s.where()
                .eq(ListSnapshot.LIST_ID, syncObject.subId).and()
                .eq(ListSnapshot.TYPE, 0).and();

        if (syncObject.query == null) w.isNull(ListSnapshot.QUERY);
        else w.eq(ListSnapshot.QUERY, clearQuery(syncObject.query));

        s.setCountOf(true);
        return (int) s.countOf();
    }

    //	private void saveSnapshotToDatabase(final boolean reset, final int subId, final String fquery, final BSONObject snapshots) throws Exception {
	//		helper.getDao(Snapshot.class).callBatchTasks(new Callable<Void>() {
	//
	//			@Override
	//			public Void call() throws Exception {
	//				if (reset) {
	//					DeleteBuilder<ListSnapshot, ?> d = helper.getDao(ListSnapshot.class).deleteBuilder();
	//
	//					Where<ListSnapshot, ?> w = d.where().eq(ListSnapshot.LIST_ID, subId).and().eq(ListSnapshot.TYPE, 0).and();
	//					if (fquery == null) w.isNull(ListSnapshot.QUERY);
	//					else w.isNotNull(ListSnapshot.QUERY);
	//
	//					d.delete();
	//				}
	//
	//				for (String k : snapshots.keySet()) {
	//					BSONObject o = (BSONObject) snapshots.get(k);
	//
	//					Snapshot s = new Snapshot();
	//					s.id = (Integer) o.get("a");
	//					s.source = (String) o.get("b");
	//					s.thumbnail = (Integer) (o.get("d") == null ? 0 : o.get("d"));
	//					s.title = (String) o.get("c");
	//					s.updated = new Date((Long) o.get("e"));
	//					s.hasContent = (Boolean) o.get("f");
	//					s.subscriptionName = (String) o.get("h");
	//					s.subscriptionIcon = (Integer) (o.get("i") == null ? 0 : o.get("i"));
	//					s.hasScreenshots = (Boolean) o.get("k");
	//					s.hasRevisions = (Boolean) o.get("l");
	//					s.subscriptionId = (Integer) o.get("g");
	//
	//					helper.getDao(Snapshot.class).createOrUpdate(s);
	//					helper.getDao(ListSnapshot.class).create(new ListSnapshot(subId, 0, s.id, fquery));
	//				}
	//				return null;
	//			}
	//		});
	//
	//		observer.notifySnapshotList();
	//	}

	//	private void saveStashToDatabase(final BSONObject stash) throws Exception {
	//		helper.getDao(Snapshot.class).callBatchTasks(new Callable<Void>() {
	//
	//			@Override
	//			public Void call() throws Exception {
	//				SQLiteDatabase db = helper.getWritableDatabase();
	//				db.execSQL("DELETE FROM stash");
	//
	//				for (String i : stash.keySet()) {
	//					db.execSQL("INSERT INTO stash(snapshot_id) VALUES(?)", new Object[] { stash.get(i) });
	//				}
	//				return null;
	//			}
	//		});
	//	}

	private static String clearQuery(String query) {
		return query == null ? null : query.trim().toLowerCase();
	}
}