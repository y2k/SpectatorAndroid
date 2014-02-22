package net.itwister.spectator.widget;

import net.itwister.spectator.App;
import net.itwister.spectator.BuildConfig;
import net.itwister.spectator.R;
import net.itwister.spectator.model.WidgetModel;
import net.itwister.spectator.view.viewsnapshot.SnapshotViewActivity;

import bindui.InjectService;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import javax.inject.Inject;

public class SubscriptionWidget extends AppWidgetProvider {

	private static final String ACTION_DELETE = "action_delete";
	private static final String EXTRA_ID = "extra_widget_id";
	private static final String EXTRA_IDS = "extra_widget_ids";

	public SubscriptionWidget() {
        InjectService.injectSimple(this);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		context.startService(new Intent(context, SyncWidgetService.class).setAction(ACTION_DELETE).putExtra(EXTRA_IDS, appWidgetIds));
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int widgetId : appWidgetIds) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.subscription_widget);
			views.setInt(R.id.animator, "setDisplayedChild", 0);
			appWidgetManager.updateAppWidget(widgetId, views);
			context.startService(new Intent(context, SyncWidgetService.class).putExtra(EXTRA_ID, widgetId));
		}
	}

	public static void invalidate(Context context, int widgetId) {
		new SubscriptionWidget().onUpdate(context, AppWidgetManager.getInstance(context), new int[] { widgetId });
	}

	/** Сервис для фоновой синхронизации данных для виджета. */
	public static class SyncWidgetService extends IntentService {

		@Inject
		private WidgetModel model;

		private AppWidgetManager wm;

		public SyncWidgetService() {
			super("Widget_SyncService");
            InjectService.injectSimple(this);
		}

		@Override
		public void onCreate() {
			super.onCreate();
			wm = AppWidgetManager.getInstance(this);
		}

		@Override
		protected void onHandleIntent(Intent intent) {
			try {
				if (ACTION_DELETE.equals(intent.getAction())) {
					model.deleteWidgetAndCleanup(intent.getIntArrayExtra(EXTRA_IDS)).sync();
				} else {
					actionRefresh(intent.getIntExtra(EXTRA_ID, 0));
				}
			} catch (Exception e) {
				if (BuildConfig.DEBUG) e.printStackTrace();
			}
		}

		private void actionRefresh(int widgetId) throws Exception {
			try {
				preSyncUpdateWidget(widgetId);
				model.sync(widgetId);
				successSyncUpdateWidget(widgetId);
			} catch (Exception e) {
				if (BuildConfig.DEBUG) e.printStackTrace();
				errorSyncUpdateWidget(widgetId);
			}
		}

		/** Создать интент на обновление виджета. */
		private PendingIntent createRefreshIntent(int widgetId) {
			Intent i = new Intent(this, SyncWidgetService.class).putExtra(EXTRA_ID, widgetId).setData(Uri.parse("refresh://" + widgetId));
			return PendingIntent.getService(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		}

		/** Обновить виджет в случае ошибки синхронизации. */
		private void errorSyncUpdateWidget(int widgetId) throws Exception {
			RemoteViews views = new RemoteViews(getPackageName(), R.layout.subscription_widget);

			if (model.getCountOfSnapshots(widgetId) > 0) {
				views.setInt(R.id.animator, "setDisplayedChild", 1);
				setAdapter(widgetId, views);
			} else {
				views.setInt(R.id.animator, "setDisplayedChild", 2);
				views.setTextViewText(R.id.refresh, getString(R.string.widget_error_the_error_occurred_refresh));
				views.setOnClickPendingIntent(R.id.refresh, createRefreshIntent(widgetId));
			}

			wm.updateAppWidget(widgetId, views);
		}

		/** Обновить виджет перед загрузкой снимков. */
		private void preSyncUpdateWidget(int widgetId) throws Exception {
			RemoteViews views = new RemoteViews(getPackageName(), R.layout.subscription_widget);

			if (model.getCountOfSnapshots(widgetId) > 0) {
				views.setInt(R.id.animator, "setDisplayedChild", 1);
				setAdapter(widgetId, views);
			} else {
				views.setInt(R.id.animator, "setDisplayedChild", 0);
			}

			wm.updateAppWidget(widgetId, views);
		}

		private void setAdapter(int widgetId, RemoteViews views) {
			Intent i = new Intent(this, SubscriptionWidgetAdapterService.class)
					.setData(Uri.parse("snapshots://" + widgetId))
					.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
			views.setRemoteAdapter(R.id.list, i);

			i = new Intent(this, SnapshotViewActivity.class);
			PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
			views.setPendingIntentTemplate(R.id.list, pi);
		}

		/** Обновить виджет в случае успешного обновления. */
		private void successSyncUpdateWidget(int widgetId) throws Exception {
			RemoteViews views = new RemoteViews(getPackageName(), R.layout.subscription_widget);

			if (model.getCountOfSnapshots(widgetId) > 0) {
				views.setInt(R.id.animator, "setDisplayedChild", 1);
				setAdapter(widgetId, views);
			} else {
				views.setInt(R.id.animator, "setDisplayedChild", 2);
				views.setTextViewText(R.id.refresh, "Subscription is empty. Refresh.");
				views.setOnClickPendingIntent(R.id.refresh, createRefreshIntent(widgetId));
			}

			wm.updateAppWidget(widgetId, views);
		}
	}
}