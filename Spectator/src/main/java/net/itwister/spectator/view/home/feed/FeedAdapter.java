//package net.itwister.spectator.view.home.feed;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.inject.Inject;
//
//import net.itwister.spectator.Constants;
//import net.itwister.spectator.R;
//import net.itwister.spectator.data.Snapshot;
//import net.itwister.spectator.model.ImageModel;
//import net.itwister.spectator.model.StashModel;
//import net.itwister.spectator.view.common.P2;
//import net.itwister.spectator.view.helpers.UiHelper;
//import net.itwister.tools.image.ImageDownloadManager;
//
//import org.ocpsoft.prettytime.PrettyTime;
//
//import android.animation.AnimatorSet;
//import android.animation.ObjectAnimator;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.drawable.AnimationDrawable;
//import android.support.v4.content.LocalBroadcastManager;
//import android.support.v4.widget.CursorAdapter;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.ViewGroup.LayoutParams;
//import android.widget.ImageView;
//import android.widget.ImageView.ScaleType;
//import android.widget.TextView;
//
//public class FeedAdapter extends CursorAdapter {
//
//	private final int iconSize;
//
//	@Inject
//	private ImageModel imageModel;
//
//	@Inject
//	private StashModel stash;
//
//	private final Map<ImageView, String> viewsPendingImage = new HashMap<ImageView, String>();
//	private final BroadcastReceiver cacheReceiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			for (ImageView iv : viewsPendingImage.keySet().toArray(new ImageView[0])) {
//				Bitmap img = model.getImageFromMemoryCacheOrDownload(viewsPendingImage.get(iv));
//				if (img != null) {
//					viewsPendingImage.remove(iv);
//					iv.setScaleType(ScaleType.CENTER_CROP);
//					iv.setImageBitmap(img);
//
//					AnimatorSet anim = new AnimatorSet();
//					anim.play(ObjectAnimator.ofFloat(iv, "alpha", 0, 1));
//					anim.start();
//				}
//			}
//		}
//	};
//
//	private P2<Integer, String> subscriptionClickListener;
//
//	public FeedAdapter(Context context) { // NO_UCD (unused code)
//		super(context, null, 0);
//		iconSize = UiHelper.px(context, Constants.ICON_SIZE);
//	}
//
//	@Override
//	public void bindView(View view, Context context, Cursor c) {
//		ViewHolder h = (ViewHolder) view.getTag();
//
//		h.text.setText(c.getString(c.getColumnIndex(Snapshot.TITLE)));
//		h.date.setText(new PrettyTime().format(new Date(c.getLong(c.getColumnIndex(Snapshot.CREATED)))));
//
//		{
//			viewsPendingImage.remove(h.image);
//			h.image.setScaleType(ScaleType.CENTER_CROP);
//
//			int thumb = c.getInt(c.getColumnIndex(Snapshot.THUMBNAIL));
//			if (thumb > 0) {
//				String url = imageModel.getSquareThumbnailUrl(thumb, 300);
//				Bitmap img = model.getImageFromMemoryCacheOrDownload(url);
//				h.image.setImageBitmap(img);
//				if (img == null) {
//
//					AnimationDrawable d = (AnimationDrawable) context.getResources().getDrawable(R.drawable.ellipsis_drawable);
//					h.image.setImageDrawable(d);
//					h.image.setScaleType(ScaleType.CENTER);
//					d.start();
//
//					viewsPendingImage.put(h.image, url);
//				}
//			} else {
//				h.image.setImageBitmap(null);
//			}
//		}
//
//		{
//			viewsPendingImage.remove(h.subIcon);
//			int subIcon = c.getInt(c.getColumnIndex(Snapshot.SUBSCRIPTION_ICON));
//			if (subIcon > 0) {
//				String url = imageModel.getSquareThumbnailUrl(subIcon, iconSize);
//				Bitmap img = model.getImageFromMemoryCacheOrDownload(url);
//				h.subIcon.setImageBitmap(img);
//				if (img == null) viewsPendingImage.put(h.subIcon, url);
//			} else {
//				h.subIcon.setImageResource(R.drawable.ic_html_subscription_stub);
//			}
//		}
//
//		if (subscriptionClickListener != null) {
//			final int id = c.getInt(c.getColumnIndex(Snapshot.SUBSCRIPTION_ID));
//			final String title = c.getString(c.getColumnIndex(Snapshot.SUBSCRIPTION_NAME));
//			h.subscriptionButton.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					subscriptionClickListener.call(id, title);
//				}
//			});
//		}
//
//		boolean isStash = c.getInt(c.getColumnIndex(Snapshot.EXTRA_STASH)) != 0;
//		h.stash.setImageResource(isStash ? R.drawable.ic_rating_important : R.drawable.ic_rating_not_important);
//
//		final int id = c.getInt(c.getColumnIndex(Snapshot.ID));
//		h.stash.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				stash.toggleAsync(id);
//			}
//		});
//	}
//
//	@Override
//	public View newView(Context context, Cursor data, ViewGroup parent) {
//		View view = View.inflate(context, R.layout.item_feed, null);
//		ViewHolder h = new ViewHolder();
//		h.text = (TextView) view.findViewById(R.id.title);
//		h.date = (TextView) view.findViewById(R.id.date);
//		h.image = (ImageView) view.findViewById(R.id.image);
//		h.subIcon = (ImageView) view.findViewById(R.id.subscriptionIcon);
//		h.subscriptionButton = view.findViewById(R.id.subscriptionButton);
//		h.stash = (ImageView) view.findViewById(R.id.stash);
//		view.setTag(h);
//
//		{
//			float d = context.getResources().getDisplayMetrics().density;
//			int col = Math.max(1, (int) (context.getResources().getDisplayMetrics().widthPixels / 240f / d));
//			int w = context.getResources().getDisplayMetrics().widthPixels / col;
//
//			LayoutParams lp = h.image.getLayoutParams();
//			lp.height = (int) (w * 0.7f);
//			h.image.setLayoutParams(lp);
//		}
//
//		return view;
//	}
//
//	public void pause() {
//		LocalBroadcastManager.getInstance(mContext).unregisterReceiver(cacheReceiver);
//	}
//
//	public void resume() {
//		LocalBroadcastManager.getInstance(mContext).registerReceiver(cacheReceiver, ImageDownloadManager.FILTER);
//	}
//
//	public void setOnSubscriptionClickListner(P2<Integer, String> subscriptionClickListener) {
//		this.subscriptionClickListener = subscriptionClickListener;
//	}
//
//	private static class ViewHolder {
//
//		TextView text;
//		TextView date;
//		ImageView image;
//		ImageView subIcon;
//		View subscriptionButton;
//		ImageView stash;
//	}
//}