package net.itwister.spectator.view.viewsnapshot.information;

import java.util.List;
import java.util.WeakHashMap;

import net.itwister.spectator.R;
import net.itwister.spectator.data.Screenshot;
import net.itwister.spectator.model.ImageModel;
import net.itwister.spectator.view.helpers.UiTimer;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import javax.inject.Inject;

import bindui.extra.ImageDownloader;

public class StaticImageGrid {

	private static final int COLUMN_COUNT = 2;

	final LinearLayout screenshots;
	final Context context;
	final float density;

//	WeakHashMap<ImageView, String> imageUrls = new WeakHashMap<ImageView, String>();
//	UiTimer timer = new UiTimer();

	OnItemClickListener listner;

    @Inject private ImageModel model;

	public StaticImageGrid(LinearLayout screenshots) {
		this.screenshots = screenshots;
		context = screenshots.getContext();
		density = screenshots.getResources().getDisplayMetrics().density;
	}

	public void changeData(List<Screenshot> data) {
		screenshots.removeAllViews();
//		imageUrls.clear();

		int divider = (int) context.getResources().getDisplayMetrics().density;

		for (int i = 0; i < Math.min(3, (1 + data.size()) / COLUMN_COUNT); i++) {
			LinearLayout group = new LinearLayout(context);
			if (i > 0) group.setPadding(0, divider, 0, 0);

			for (int n = 0; n < COLUMN_COUNT; n++) {
				final int pos = COLUMN_COUNT * i + n;
				View v = null;

				if (pos < data.size()) {
					v = View.inflate(context, R.layout.item_screenshot, null);

					final ImageView iv = (ImageView) v.findViewById(R.id.image);
//					imageUrls.put(iv, data.get(COLUMN_COUNT * i + n).image);
                    model.addImageTaskToQueue(iv, data.get(COLUMN_COUNT * i + n).image, new ImageDownloader.ImageDownloaderCallback() {

                        @Override
                        public void complete(Bitmap bitmap) {
                            iv.setImageBitmap(bitmap);
                        }
                    });

					final int count = data.size();
					v.findViewById(R.id.button).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (listner != null) listner.onItemClicked(pos, count);
						}
					});
				} else {
					v = new View(context);
				}

				//					group.addView(v, new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
				LinearLayout.LayoutParams m = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
				if (n < COLUMN_COUNT - 1) m.rightMargin = divider;
				group.addView(v, m);
			}
			screenshots.addView(group, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, screenshots.getWidth() / COLUMN_COUNT));
		}
//		checkImageViews();
	}

    @Deprecated
	public void onPause() {
//		timer.clear();
	}

    @Deprecated
	public void onResume() {
//		checkImageViews();
	}

	public void setOnItemClickListener(OnItemClickListener l) {
		listner = l;
	}

//	private void checkImageViews() {
//		for (ImageView iv : imageUrls.keySet()) {
//			if (iv.getDrawable() == null) {
//
//				String url = imageUrls.get(iv);
//				url = "http://remote-cache.api-i-twister.net/Cache/Get?format=jpeg&size=120&url=" + Uri.encode(url);
//
//				Bitmap bmp = ImageDownloadManager.getInstance().getImageFromMemoryCacheOrDownload(url);
//				if (bmp == null) {
//					timer.schedule(new Runnable() {
//
//						@Override
//						public void run() {
//							checkImageViews();
//						}
//					});
//				} else iv.setImageBitmap(bmp);
//			}
//		}
//	}

	public interface OnItemClickListener {

		void onItemClicked(int position, int count);
	}
}