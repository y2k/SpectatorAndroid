package net.itwister.spectator.view.home.menu;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import net.itwister.spectator.Constants;
import net.itwister.spectator.R;
import net.itwister.spectator.data.Subscription;
import net.itwister.spectator.model.ImageModel;
import net.itwister.spectator.view.common.PinnedHeaderListView;
import net.itwister.spectator.view.common.PinnedHeaderListView.PinnedHeaderAdapter;
import net.itwister.spectator.view.helpers.UiHelper;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import bindui.InjectService;
import bindui.extra.ImageDownloader;

public class SubscriptionsAdapter extends CursorAdapter implements PinnedHeaderAdapter {

    @Inject private ImageModel model;
	private final int iconSize;
	private PinnedHeaderListView listView;

//	private final Map<ImageView, String> viewsPendingImage = new HashMap<ImageView, String>();
//	private final BroadcastReceiver cacheReceiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			for (ImageView iv : viewsPendingImage.keySet().toArray(new ImageView[0])) {
//				Bitmap img = model.getImageFromMemoryCacheOrDownload(viewsPendingImage.get(iv));
//				if (img != null) {
//					viewsPendingImage.remove(iv);
//					iv.setImageBitmap(img);
//
//					AnimatorSet anim = new AnimatorSet();
//					anim.play(ObjectAnimator.ofFloat(iv, "alpha", 0, 1));
//					anim.start();
//				}
//			}
//		}
//	};

	public SubscriptionsAdapter(Context context) { // NO_UCD (unused code)
		super(context, null, 0);
        InjectService.injectSimple(this);
		iconSize = UiHelper.px(context, Constants.ICON_SIZE);
	}

	@Override
	public void bindView(View view, final Context context, Cursor cursor) {
		final Holder h = (Holder) view.getTag();
		h.title.setText(cursor.getString(cursor.getColumnIndex(Subscription.TITLE)));

		int count = cursor.getInt(cursor.getColumnIndex("count"));
		h.count.setText("" + count);
		h.count.setVisibility(count > 0 ? View.VISIBLE : View.GONE);

//		viewsPendingImage.remove(h.icon);
        model.deleteTokenFromQueue(h.icon);
        h.icon.setImageDrawable(null);
		int thumb = cursor.getInt(cursor.getColumnIndex(Subscription.THUMBNAIL));
//		Bitmap img = null;
		if (thumb > 0) {
			String url = String.format(Constants.Url.URL_HOST + "/Image/Thumb/%s?size=%s&type=webp", thumb, iconSize);
//			img = model.getImageFromMemoryCacheOrDownload(url);
//			if (img == null) viewsPendingImage.put(h.icon, url);
            model.addImageTaskToQueue(h.icon, url, new ImageDownloader.ImageDownloaderCallback() {

                @Override
                public void complete(Bitmap bitmap) {
                    h.icon.setImageBitmap(bitmap);
                }
            });
		}
		//h.icon.setImageBitmap(img);

		String prev = null, current = cursor.getString(cursor.getColumnIndex(Subscription.GROUP));
		if (cursor.moveToPrevious()) {
			prev = cursor.getString(cursor.getColumnIndex(Subscription.GROUP));
			cursor.moveToNext(); // Возвращаемся на нужную позицию
		}

		h.group.setVisibility(TextUtils.equals(current, prev) ? View.GONE : View.VISIBLE);
		h.groupTitle.setText(current);
	}

	@Override
	public void configurePinnedHeaders(PinnedHeaderListView listView) {
		this.listView = listView;
		if (getCount() < 1) return;

		if (listView.getPositionAt(0) < listView.getHeaderViewsCount()) {
			listView.setHeaderInvisible(0, false);
		} else {
			Cursor cursor = (Cursor) getItem(listView.getPositionAt(0) - listView.getHeaderViewsCount());
			String prev = null, current = cursor.getString(cursor.getColumnIndex(Subscription.GROUP));

			cursor = (Cursor) getItem(listView.getPositionAt(listView.getPinnedHeaderHeight(0)) - listView.getHeaderViewsCount());
			prev = cursor.getString(cursor.getColumnIndex(Subscription.GROUP));

			if (TextUtils.equals(current, prev)) listView.setHeaderPinnedAtTop(0, 0, true);
			else listView.setHeaderPinnedAtTop(0, listView.getChildAt(1).getTop() - listView.getPinnedHeaderHeight(0), true);
		}
	}

	@Override
	public int getPinnedHeaderCount() {
		return getCount() > 0 ? 1 : 0;
	}

	@Override
	public View getPinnedHeaderView(int viewIndex, View convertView, ViewGroup parent) {
		if (convertView == null) convertView = newView(mContext, null, null);

		if (listView != null && listView.getPositionAt(0) >= listView.getHeaderViewsCount()) {
			Holder h = (Holder) convertView.getTag();
			Cursor cursor = (Cursor) getItem(listView.getPositionAt(0) - listView.getHeaderViewsCount());

			if (cursor != null) {
				String current = cursor.getString(cursor.getColumnIndex(Subscription.GROUP));
				h.groupTitle.setText(current);
				h.content.setVisibility(View.GONE);
			}
		}

		return convertView;
	}

	@Override
	public int getScrollPositionForHeader(int viewIndex) {
		return -1;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup group) {
		View v = View.inflate(context, R.layout.item_subscription2, null);
		Holder h = new Holder();
		h.groupTitle = (TextView) v.findViewById(R.id.groupTitle);
		h.count = (TextView) v.findViewById(R.id.count);
		h.title = (TextView) v.findViewById(R.id.title);
		h.group = v.findViewById(R.id.group);
		h.content = v.findViewById(R.id.content);
		h.icon = (ImageView) v.findViewById(R.id.icon);
		v.setTag(h);
		return v;
	}

//	public void start() {
//		LocalBroadcastManager.getInstance(mContext).registerReceiver(cacheReceiver, ImageDownloadManager.FILTER);
//		cacheReceiver.onReceive(null, null);
//	}
//
//	public void stop() {
//		LocalBroadcastManager.getInstance(mContext).unregisterReceiver(cacheReceiver);
//	}

	static class Holder {

		ImageView icon;
		View content;
		TextView count;
		TextView groupTitle;
		TextView title;
		View group;
	}
}