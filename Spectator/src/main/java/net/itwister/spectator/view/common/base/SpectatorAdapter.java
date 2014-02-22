//package net.itwister.spectator.view.common.base;
//
//import net.itwister.spectator.view.helpers.UiTimer;
//import net.itwister.tools.image.ImageDownloadManager;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.support.v4.widget.CursorAdapter;
//import android.util.DisplayMetrics;
//import android.widget.ImageView;
//
//public abstract class SpectatorAdapter extends CursorAdapter {
//
//	private final UiTimer timer = new UiTimer(500);
//	private final DisplayMetrics metrics;
//
//	public SpectatorAdapter(Context context) {
//		super(context, null, 0);
//		metrics = context.getResources().getDisplayMetrics();
//	}
//
//	protected int dip2px(float dip) {
//		return (int) (dip * metrics.density);
//	}
//
//	protected void setImageViewUrl(ImageView iv, String url) {
//		Bitmap bmp = ImageDownloadManager.getInstance().getImageFromMemoryCacheOrDownload(url);
//		iv.setImageBitmap(bmp);
//		if (bmp == null) timer.scheduleNotifyDataSetChanged(this);
//	}
//}