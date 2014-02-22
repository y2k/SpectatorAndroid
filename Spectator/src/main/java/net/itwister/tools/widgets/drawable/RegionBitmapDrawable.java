package net.itwister.tools.widgets.drawable;

import java.io.File;

import net.itwister.spectator.BuildConfig;
import net.itwister.tools.inner.Ln;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.os.SystemClock;
import bindui.Task;
import bindui.Task.TaskCallback;

@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
class RegionBitmapDrawable extends ZoomImageDrawable {

	private BitmapRegionDecoder decoder;

	private Bitmap thumbnail;

	private Bitmap image;
	private Rect visualRect;
	private Rect realRect;

	private HandlerThread handlerThread;
	private Handler handler;

	private final Object locker = new Object();

	private int imageWidth;
	private int imageHeight;

	private final int screenWidth;
	private final int screenHeight;

	private Task<Void> initializeTask;

	public RegionBitmapDrawable(int width, int height, final File file, Point viewSize) {
		screenWidth = viewSize.x;
		screenHeight = viewSize.y;
		imageWidth = width;
		imageHeight = height;

		initializeTask = new Task<Void>() {

			@Override
			public void onExecute() throws Exception {
				decoder = BitmapRegionDecoder.newInstance(file.getAbsolutePath(), false);

				if (Thread.interrupted()) throw new InterruptedException();

				imageWidth = decoder.getWidth();
				imageHeight = decoder.getHeight();

				Options o = new Options();
				o.inSampleSize = 1 + computeSample(imageWidth, imageHeight, screenWidth, screenHeight);
				o.inSampleSize = o.inSampleSize == 0 ? 0 : (int) Math.pow(2, 32 - Integer.numberOfLeadingZeros(o.inSampleSize - 1));
				thumbnail = decoder.decodeRegion(new Rect(0, 0, imageWidth, imageHeight), o);

				if (Thread.interrupted()) throw new InterruptedException();

				if (BuildConfig.DEBUG) Ln.d("thumb create, sample = %s, img[%s x %s] -> thumb[%s x %s]",
						o.inSampleSize, imageWidth, imageHeight, thumbnail.getWidth(), thumbnail.getHeight());
			}
		}.withCallback(new TaskCallback<Void>() {

			@Override
			public void onFail(Exception e) {
				if (BuildConfig.DEBUG) e.printStackTrace();
				close();
			}

			@Override
			public void onSuccess(Void data) {
				initializeTask = null;

				handlerThread = new HandlerThread("RegionBitmapThread", Process.THREAD_PRIORITY_BACKGROUND);
				handlerThread.start();
				RegionBitmapDrawable.this.handler = new Handler(handlerThread.getLooper());

				postInvalidate();
			}
		});
		initializeTask.async();
	}

	@Override
	public void close() {
		super.close();

		if (initializeTask != null) initializeTask.cancel(true);
		if (decoder != null) decoder.recycle();
		if (thumbnail != null) thumbnail.recycle();
		if (image != null) image.recycle();
	}

	@Override
	public int getIntrinsicHeight() {
		return imageHeight;
	}

	@Override
	public int getIntrinsicWidth() {
		return imageWidth;
	}

	@Override
	public boolean isReady() {
		return initializeTask == null;
	}

	@Override
	public void onDraw(Canvas canvas, Paint paint) {
		if (initializeTask != null) return;

		final Rect targetRect = canvas.getClipBounds();

		canvas.save();
		canvas.scale((float) imageWidth / thumbnail.getWidth(), (float) imageHeight / thumbnail.getHeight());

		Paint p = paint == null ? new Paint() : new Paint(paint);
		p.setFilterBitmap(true);
		canvas.drawBitmap(thumbnail, 0, 0, p);

		canvas.restore();

		// ==========================================================
		synchronized (locker) {
			if (visualRect != null) {
				canvas.save();

				canvas.translate(realRect.left, realRect.top);
				canvas.scale((float) realRect.width() / image.getWidth(), (float) realRect.height() / image.getHeight());
				canvas.drawBitmap(image, 0, 0, paint);

				canvas.restore();
			}
		}
		// ==========================================================

		if (!targetRect.equals(visualRect)) {
			handler.removeCallbacksAndMessages(this);
			handler.postAtTime(new Runnable() {

				@Override
				public void run() {
					try {
						Options o = new Options();
						o.inSampleSize = computeSample(targetRect.width(), targetRect.height(), screenWidth, screenHeight);

						Rect region = new Rect(targetRect);
						Bitmap img = decodeRegion(region, o);
						if (img == null) return;

						if (BuildConfig.DEBUG) Ln.d("Decode part, img[%s x %s], bound[%s x %s], real [%s x %s] w = %s, h = %s, samp = %s",
								imageWidth, imageHeight, targetRect.width(), targetRect.height(), region.width(), region.height(), img.getWidth(), img.getHeight(), o.inSampleSize);

						synchronized (locker) {
							if (image != null) image.recycle();
							image = img;
							visualRect = targetRect;
							realRect = region;
						}

						postInvalidate();
					} catch (Exception e) {
						if (BuildConfig.DEBUG) e.printStackTrace();
					}
				}
			}, this, SystemClock.uptimeMillis() + 200);
		}
	}

	private Bitmap decodeRegion(Rect rect, Options o) {
		try {
			if (rect.intersect(0, 0, decoder.getWidth(), decoder.getHeight())) {
				return decoder.decodeRegion(rect, o);
			} else {
				if (BuildConfig.DEBUG) Ln.w("can't find region [%s], in image [%s x %s]", rect, decoder.getWidth(), decoder.getHeight());
				return null;
			}
		} catch (Throwable e) {
			throw new RuntimeException("img [" + decoder.getWidth() + " " + decoder.getHeight() + "], rect [" + rect + "], sample = " + o.inSampleSize, e);
		}
	}

	public static int computeSample(int imgWidth, int imgHeight, int targetWidth, int targetHeight) {
		//		int sample = Math.max(imgWidth, imgHeight) / Math.max(targetWidth, targetHeight);
		int sample = (int) Math.round(Math.sqrt((float) imgWidth * imgHeight / targetWidth / targetHeight));
		Ln.d("compute sample = %s, [%s x %s] -> [%s x %s]", sample, imgWidth, imgHeight, targetWidth, targetHeight);
		return sample;
	}
}