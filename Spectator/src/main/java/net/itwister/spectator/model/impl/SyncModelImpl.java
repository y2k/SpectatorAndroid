package net.itwister.spectator.model.impl;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.itwister.spectator.App;
import net.itwister.spectator.R;
import net.itwister.spectator.data.Subscription;
import net.itwister.spectator.model.SnapshotModel;
import net.itwister.spectator.model.SubscriptionModel;
import net.itwister.spectator.model.SyncModel;
import net.itwister.spectator.model.helpers.SpectatorTask;
import net.itwister.spectator.model.settings.SettingsManager;
import net.itwister.spectator.view.common.base.SpectatorActivity.ForegroundModule;
import net.itwister.spectator.view.home.HomeActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.InboxStyle;
import android.support.v4.content.LocalBroadcastManager;
import bindui.extra.Task.TaskCallback;

@Singleton
public class SyncModelImpl implements SyncModel {

	private static final String EXTRA_DATA = "data";
	private static final String ACTION_SYNC = "action_sync";
	private static final Set<String> sLoadersInProgress = new HashSet<String>();
	private static final Set<SyncTarget> sLastSyncSuccess = new HashSet<SyncTarget>();

	private final Handler uiHandler = new Handler();

	@Inject
	private SnapshotModel snapModel;

	@Inject
	private SubscriptionModel subModel;

	@Inject
	private SettingsManager settings;

	@Override
	public SyncObject createSyncSnapshots(int subId, String query) {
		SyncObject s = new SyncObject();
		s.subId = subId;
		s.query = query;
		s.target = SyncTarget.Snapshots;
		return s;
	}

	@Override
	public SyncObject createSyncSubscriptions() {
		SyncObject s = new SyncObject();
		s.target = SyncTarget.Subscriptions;
		return s;
	}

	@Override
	public boolean isLastSyncSuccess(SyncObject sync) {
		return sLastSyncSuccess.contains(sync.target);
	}

	@Override
	public boolean isSyncInProgress(SyncObject sync) {
		return sLoadersInProgress.contains(createKey(sync.target, sync.subId, sync.query));
	}

	@Override
	public void registerObserverReceiver(BroadcastReceiver observer) {
		observer.onReceive(App.getInstance(), null);
		LocalBroadcastManager.getInstance(App.getInstance()).registerReceiver(observer, new IntentFilter(ACTION_SYNC));
	}

	@Override
	public void startSync(final SyncObject sync) {
		final String key = createKey(sync.target, sync.subId, sync.query);
		if (!sLoadersInProgress.contains(key)) {
			sLoadersInProgress.add(key);

			sLastSyncSuccess.add(sync.target);
			if (sync.target == SyncTarget.Subscriptions) {
				new SpectatorTask<Void>() {

					@Override
					public void onExecute() throws Exception {
						subModel.syncSubscriptions();
					}
				}.withCallback(new TaskCallback<Void>() {

					@Override
					public void onFail(Exception exception) {
						sLastSyncSuccess.remove(sync.target);
					}

					@Override
					public void onFinish() {
						updateAndSendBroadcast(key, ACTION_SYNC);
					}
				}).async();
			} else if (sync.target == SyncTarget.Snapshots) {
				new SpectatorTask<Void>() {

					@Override
					public void onExecute() throws Exception {
						snapModel.sync(sync.reset, sync.subId, sync.query);
					}
				}.withCallback(new TaskCallback<Void>() {

					@Override
					public void onFail(Exception exception) {
						sLastSyncSuccess.remove(sync.target);
					}

					@Override
					public void onFinish() {
						updateAndSendBroadcast(key, ACTION_SYNC);
					}
				}).async();
			}

			updateAndSendBroadcast(null, ACTION_SYNC);
		}
	}

	@Override
	public void syncSubscriptionsAndShowNotification() throws Exception {
		subModel.syncSubscriptions();

		if (!settings.isNotificationsEnabled()) return;

		int updSubs = subModel.getNumberOfUpdatedSubscriptions();
		int newItems = subModel.getNumberOfNewSnapshots();
		if (updSubs <= 0 || newItems <= 0) return;

		Context c = App.getInstance();
		InboxStyle style = new NotificationCompat.InboxStyle();
		style.setSummaryText(c.getString(R.string.notification_new_s_snapshots, newItems));
		for (Subscription s : subModel.getUpdatedSubscriptions()) {
			style.addLine(c.getString(R.string.notification_item_update_subscription, s.count, s.title));
		}

		Intent i = new Intent(App.getInstance(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pi = PendingIntent.getActivity(App.getInstance(), 0, i, 0);

		final Notification n = new NotificationCompat.Builder(App.getInstance())
				.setAutoCancel(true)
				.setSmallIcon(R.drawable.ic_notification)
				.setContentTitle(c.getString(R.string.notification_updates_s_subscriptions, updSubs))
				.setContentText(c.getString(R.string.notification_new_s_snapshots, newItems))
				.setNumber(updSubs)
				.setStyle(style)
				.setContentIntent(pi)
				.setLights(Color.WHITE, 500, 2000)
				.build();

		if (ForegroundModule.isForeground()) return;
		((NotificationManager) App.getInstance().getSystemService(Context.NOTIFICATION_SERVICE)).notify(1, n);
	}

	// ==============================================================
	// Скрытые методы
	// ==============================================================

	private void updateAndSendBroadcast(final String removeKey, final String action) {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				if (removeKey != null) sLoadersInProgress.remove(removeKey);
				LocalBroadcastManager.getInstance(App.getInstance()).sendBroadcastSync(new Intent(action));
			}
		};

		if (uiHandler.getLooper().getThread() == Thread.currentThread()) r.run();
		else uiHandler.post(r);
	}

	private static String createKey(SyncTarget target, int listId, String query) {
		return target + "|" + listId + "|" + query;
	}
}