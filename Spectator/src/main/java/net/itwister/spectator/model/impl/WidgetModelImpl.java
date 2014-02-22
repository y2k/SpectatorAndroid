package net.itwister.spectator.model.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.itwister.spectator.App;
import net.itwister.spectator.model.ImageModel;
import net.itwister.spectator.model.SnapshotModel;
import net.itwister.spectator.model.SyncModel.SyncObject;
import net.itwister.spectator.model.SyncModel.SyncTarget;
import net.itwister.spectator.model.WidgetModel;
import net.itwister.spectator.model.helpers.SpectatorTask;
import net.itwister.spectator.widget.SubscriptionWidget;
import net.itwister.tools.io.IoHelper;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.ParcelFileDescriptor;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import bindui.Task;

@Singleton
public class WidgetModelImpl implements WidgetModel {

	/** Максимальное количество снимков в одмно виджете. */
	private static final int ITEMS_FOR_WIDGET = 20;
	private static final String PREFS_NAME = "net.itwister.spectator.SubscriptionWidget";
	private static final String PREF_PREFIX_KEY = "appwidget_";

	private static final int WIDGET_SUB_BASE = 100000;

	private final Context context = App.getInstance();

	@Inject
	private OrmLiteSqliteOpenHelper helper;

	@Inject
	private SnapshotModel snaps;

	@Inject
	private ImageModel imgModel;

	@Override
	public Task<Void> deleteAllWidgetTask() {
		return new SpectatorTask<Void>() {

			@Override
			public void onExecute() throws Exception {
				AppWidgetManager wm = AppWidgetManager.getInstance(App.getInstance());
				int[] ids = wm.getAppWidgetIds(new ComponentName(App.getInstance(), SubscriptionWidget.class));
				doDeleteWidgetAndCleanup(ids);
				for (int i : ids) {
					SubscriptionWidget.invalidate(getContext(), i);
				}
			}
		};
	}

	@Override
	public Task<Void> deleteWidgetAndCleanup(final int[] widgetIds) {
		return deleteWidgetAndCleanupTask(widgetIds).async();
	}

	@Override
	public Task<Void> deleteWidgetAndCleanupTask(final int[] widgetIds) {
		return new SpectatorTask<Void>() {

			@Override
			public void onExecute() throws Exception {
				doDeleteWidgetAndCleanup(widgetIds);
			}
		};
	}

	@Override
	public int getCountOfSnapshots(int widgetId) throws Exception {
		SyncObject so = new SyncObject();
		so.subId = getDatabaseSubscriptionId(widgetId);
		so.target = SyncTarget.Snapshots;
		return snaps.getCount(so);
	}

	@Override
	public int getDatabaseSubscriptionId(int widgetId) {
		return getSubscriptionIdForWidget(widgetId) + WIDGET_SUB_BASE;
	}

	@Override
	public Cursor getSnapshots(int widgetId) throws Exception {
		return snaps.get(getDatabaseSubscriptionId(widgetId), null);
	}

	@Override
	public int getSubscriptionIdForWidget(int widgetId) {
		return context.getSharedPreferences(PREFS_NAME, 0).getInt(PREF_PREFIX_KEY + widgetId, 0);
	}

	@Override
	public Bitmap getThumbnailForShapshot(int snapshotId) {
		SQLiteDatabase db = helper.getReadableDatabase();
		ParcelFileDescriptor fd = null;
		try {
			Cursor c = db.rawQuery("SELECT image FROM thumbnails WHERE snapshot_id = ?", new String[] { "" + snapshotId });
			try {
				if (c.moveToFirst()) {
					byte[] data = c.getBlob(0);
					return BitmapFactory.decodeByteArray(data, 0, data.length);
				} else return null;
			} finally {
				c.close();
			}
		} catch (Exception e) {
			return null;
		} finally {
			IoHelper.close(fd);
		}
	}

	@Override
	public void setSubsciptionForWidget(int widgetId, int subscriptionId) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
		prefs.putInt(PREF_PREFIX_KEY + widgetId, subscriptionId).commit();
	}

	@Override
	public void sync(int widgetId) throws Exception {
		int subId = getSubscriptionIdForWidget(widgetId);
		if (subId == 0) return;
		snaps.sync(true, subId, null, subId + WIDGET_SUB_BASE, ITEMS_FOR_WIDGET);

		Cursor c = snaps.get(subId + WIDGET_SUB_BASE, null);
		try {
			c.moveToPosition(-1);
			while (c.moveToNext()) {
				syncThumbnailForSnapshot(c.getInt(c.getColumnIndex("_id")), c.getInt(c.getColumnIndex("thumbnail")));
			}
		} finally {
			c.close();
		}
	}

	private void doDeleteWidgetAndCleanup(final int[] widgetIds) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();

		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		try {
			for (int id : widgetIds) {
				db.execSQL("DELETE FROM subscriptions_snapshots WHERE subscriptionId = ?", new Object[] { getSubscriptionIdForWidget(id) + WIDGET_SUB_BASE });
				prefs.remove(PREF_PREFIX_KEY + id);
			}
			db.execSQL("DELETE FROM thumbnails WHERE snapshot_id NOT IN (SELECT snapshotId FROM subscriptions_snapshots WHERE subscriptionId >= ?)", new Object[] { WIDGET_SUB_BASE });

			prefs.commit();
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * Загрузить в базу миниатюру снимка если она уже не загружена.
	 * @param snapshotId ID снимка.
	 * @param imageId ID картинки.
	 */
	private void syncThumbnailForSnapshot(int snapshotId, int imageId) throws Exception {
		if (imageId == 0) return; // Если ссылка не невалидную картинку, то выходим.

		SQLiteDatabase db = helper.getWritableDatabase();
		long c = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM thumbnails WHERE snapshot_id = ?", new String[] { "" + snapshotId });
		if (c > 0) return; // Если картика уже скачана, то выходи.

		String url = imgModel.getThumbnailUrl(imageId, 200);
		byte[] image = EntityUtils.toByteArray(new DefaultHttpClient().execute(new HttpGet(url)).getEntity());

		ContentValues cv = new ContentValues();
		cv.put("snapshot_id", snapshotId);
		cv.put("image", image);
		db.insertWithOnConflict("thumbnails", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
	}
}