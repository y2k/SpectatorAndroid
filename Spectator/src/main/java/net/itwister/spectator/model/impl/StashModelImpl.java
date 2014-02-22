package net.itwister.spectator.model.impl;

import javax.inject.Inject;

import net.itwister.spectator.Constants;
import net.itwister.spectator.R;
import net.itwister.spectator.model.AnalyticsModel;
import net.itwister.spectator.model.StashModel;
import net.itwister.spectator.model.database.ObserverManager;
import net.itwister.spectator.model.helpers.SpectatorTask;
import net.itwister.spectator.model.web.SpectatorWebClient;
import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import bindui.Task;

public class StashModelImpl extends SpectatorModel implements StashModel {

	@Inject
	private OrmLiteSqliteOpenHelper helper;

	@Inject
	private ObserverManager observerManager;

	@Inject
	private AnalyticsModel analitycs;

	@Inject
	private SpectatorWebClient web;

	@Override
	public void toggleAsync(final int snapshotId) {
        final boolean[] add = new boolean[1];
		new SpectatorTask<Void>() {

			@Override
			public void onExecute() throws Exception {
				add[0] = toggleDatabase(snapshotId);
				try {
					if (add[0]) {
                        web.api().stashAdd(snapshotId);
					} else {
                        web.api().stashDelete(snapshotId);
					}
				} catch (Exception e) {

					if (add[0]) analitycs.eventStashAdd(false);
					else analitycs.eventStashRemove(false);

					toggleDatabase(snapshotId);
					throw e;
				}

				if (add[0]) analitycs.eventStashAdd(true);
				else analitycs.eventStashRemove(true);
			}
		}.withCallback(new Task.TaskCallback<Void>() {

            @Override
            public void onFail(Exception exception) {
                int msg = add[0] ? R.string.error_can_t_add_snapshot_to_stash : R.string.error_can_t_remove_snapshot_from_stash;
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        }).async();
	}

	private boolean toggleDatabase(int snapshotId) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("snapshot_id", snapshotId);

		boolean add = false;
		try {
			db.insertWithOnConflict("stash", null, cv, SQLiteDatabase.CONFLICT_ABORT);
			add = true;
		} catch (SQLiteConstraintException e) {
			db.execSQL("DELETE FROM stash WHERE snapshot_id = ?", new Object[] { snapshotId });
		}

		observerManager.sendNotification(ObserverManager.SNAPSHOT_LIST);
		return add;
	}
}