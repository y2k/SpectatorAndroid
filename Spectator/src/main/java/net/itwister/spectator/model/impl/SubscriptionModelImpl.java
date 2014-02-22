package net.itwister.spectator.model.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.itwister.spectator.BuildConfig;
import net.itwister.spectator.data.Subscription;
import net.itwister.spectator.data.protobuf.SnapshotsResponseProtos.ProtoSubscriptionResponse;
import net.itwister.spectator.data.protobuf.SnapshotsResponseProtos.ProtoSubscriptionResponse.ProtoSubscription;
import net.itwister.spectator.model.SubscriptionModel;
import net.itwister.spectator.model.database.ObserverManager;
import net.itwister.spectator.model.database.SpectatorOpenHelper;
import net.itwister.spectator.model.helpers.OrmLiteHelper;
import net.itwister.spectator.model.helpers.SpectatorTask;
import net.itwister.spectator.model.web.SpectatorWebClient;
import android.database.Cursor;
import android.database.DatabaseUtils;
import bindui.Task;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

@Singleton
public class SubscriptionModelImpl extends SpectatorModel implements SubscriptionModel {

	@Inject
	private SpectatorOpenHelper helper;

	@Inject
	private SpectatorWebClient web;

	@Inject
	private ObserverManager observer;

	@Override
	public void clearUnreadCountAsync(final int subscriptionId) {
		new SpectatorTask<Void>() {

			@Override
			public void onExecute() throws Exception {
				helper.getWritableDatabase().execSQL("UPDATE subscriptions SET count = 0 WHERE _id = ?", new Object[] { subscriptionId });
				observer.notifySubscriptions();
			}
		}.async();
	}

	@Override
	public Task<Void> create(final String title, final String source, final boolean isRss) {
		return new Task<Void>() {

			@Override
			protected void onExecute() throws Exception {
				web.api().createSubscription(source, false, title, isRss);
			}
		};
	}

    @Override
    public Task<Void> editTask(final int subscriptionId, final String title) {
        return new Task<Void>() {

            @Override
            protected void onExecute() throws Exception {
                doEdit(subscriptionId, title);
            }
        };
    }

    @Override
	public void edit(int subscriptionId, String title) throws Exception {
        doEdit(subscriptionId, title);
	}

    @Override
	public int getNumberOfNewSnapshots() {
		return (int) DatabaseUtils.longForQuery(helper.getWritableDatabase(), "SELECT SUM(count) FROM subscriptions", null);
	}

	@Override
	public int getNumberOfUpdatedSubscriptions() {
		return (int) DatabaseUtils.longForQuery(helper.getWritableDatabase(), "SELECT COUNT(*) FROM subscriptions WHERE count > 0", null);
	}

	@Override
	public Subscription getSubscriptionById(int subscriptionId) {
		try {
			List<Subscription> subs = helper.getDao(Subscription.class).queryForEq(Subscription.ID, subscriptionId);
			return subs.isEmpty() ? null : subs.get(0);
		} catch (Exception e) {
			if (BuildConfig.DEBUG) e.printStackTrace();
			return null;
		}
	}

	@Override
	public Cursor getSubscriptionsFromDatabase() throws SQLException {
        return doGetSubscriptionsFromDatabase();
	}

    @Override
    public Task<Cursor> getSubscriptionsFromDatabaseTask() {
        return new Task<Cursor>() {

            @Override
            protected Cursor onExecuteWithResult() throws Exception {
                return doGetSubscriptionsFromDatabase();
            }
        };
    }

    @Override
	public List<Subscription> getUpdatedSubscriptions() throws SQLException {
		return helper.getDao(Subscription.class).queryBuilder().orderBy("count", false).where().gt("count", 0).query();
	}

	@Override
	public void syncSubscriptions() throws Exception {
		final ProtoSubscriptionResponse data = web.api().subscriptionList();
		final Dao<Subscription, Integer> dao = helper.getDao(Subscription.class);

		dao.callBatchTasks(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				dao.deleteBuilder().delete();

				for (ProtoSubscription i : data.getSubscriptionsList()) {
					Subscription s = new Subscription();
					s.id = i.getId();
					s.title = i.getTitle();
					s.source = i.getSource();
					s.group = i.getGroup();
					s.thumbnail = i.getThumbnail();
					s.count = i.getUnreadCount();
					dao.create(s);
				}

				return null;
			}
		});
	}

    @Override
    public Task<Void> delete(final int subscriptionId) {
        return new Task<Void>() {

            @Override
            protected void onExecute() throws Exception {
                doDeleteSubscription(subscriptionId);
            }
        };
    }

    private void doEdit(int subscriptionId, String title) {
        Map<String, String> json = new HashMap<String, String>();
        json.put("Title", title);
        web.api().editSubscription(subscriptionId, title);
    }

    private void doDeleteSubscription(int subscriptionId) throws SQLException {
        web.api().deleteSubscription(subscriptionId);

        Dao<Subscription, Integer> dao = helper.getDao(Subscription.class);
        dao.deleteById(subscriptionId);

        observer.notifySubscriptions();
    }

    private Cursor doGetSubscriptionsFromDatabase() throws SQLException {
        QueryBuilder<Subscription, ?> q = helper.getDao(Subscription.class).queryBuilder()
                .orderByRaw("[" + Subscription.GROUP + "], upper(" + Subscription.TITLE + ") ");
        return OrmLiteHelper.queryToCursor(q, helper);
    }
}