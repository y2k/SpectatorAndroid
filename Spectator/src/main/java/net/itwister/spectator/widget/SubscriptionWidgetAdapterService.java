package net.itwister.spectator.widget;

import javax.inject.Inject;

import net.itwister.spectator.App;
import net.itwister.spectator.BuildConfig;
import net.itwister.spectator.R;
import net.itwister.spectator.model.WidgetModel;
import net.itwister.spectator.view.viewsnapshot.SnapshotViewActivity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import bindui.InjectService;

public class SubscriptionWidgetAdapterService extends RemoteViewsService {

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new RemoteViewsFactoryImpl(intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0));
	}

	private static class RemoteViewsFactoryImpl implements RemoteViewsFactory {

		@Inject
		private WidgetModel model;

		private Cursor snapshots;

		private final int widgetId;
		private final int localSubscriptionId;

		public RemoteViewsFactoryImpl(int widgetId) {
			this.widgetId = widgetId;
			InjectService.injectSimple(this);
			localSubscriptionId = model.getDatabaseSubscriptionId(widgetId);
		}

		@Override
		public int getCount() {
			return snapshots == null ? 0 : snapshots.getCount();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public RemoteViews getLoadingView() {
			return null;
		}

		@Override
		public RemoteViews getViewAt(int position) {
			RemoteViews view = new RemoteViews(App.getInstance().getPackageName(), R.layout.item_widget_subscriptions);

			snapshots.moveToPosition(position);
			int id = snapshots.getInt(snapshots.getColumnIndex("_id"));

			Bitmap img = model.getThumbnailForShapshot(id);
			view.setImageViewBitmap(R.id.image, img);

			String title = snapshots.getString(snapshots.getColumnIndex("title"));
			view.setTextViewText(R.id.text, title);

			Intent i = SnapshotViewActivity
					//					.newIntentFill(App.getInstance(), localSubscriptionId, position)
					.newIntentFill(id)
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			view.setOnClickFillInIntent(R.id.root, i);
			return view;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public void onCreate() {}

		@Override
		public void onDataSetChanged() {
			try {
				snapshots = model.getSnapshots(widgetId);
			} catch (Exception e) {
				if (BuildConfig.DEBUG) e.printStackTrace();
			}
		}

		@Override
		public void onDestroy() {
			if (snapshots != null) snapshots.close();
		}
	}
}