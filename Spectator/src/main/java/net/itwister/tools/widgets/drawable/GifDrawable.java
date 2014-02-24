package net.itwister.tools.widgets.drawable;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;
import bindui.extra.Task;
import bindui.extra.Task.TaskCallback;

class GifDrawable extends ZoomImageDrawable {

	private DiskGifDecoder decoder;

	private final Handler mainHandler = new Handler(Looper.getMainLooper());

	private Bitmap currentFrame;

	private Task<Void> worker;
	private Task<Void> initializeTask;

	private final int width;

	private final int height;

	public GifDrawable(final File imagePath, int width, int height) {
		this.width = width;
		this.height = height;

		initializeTask = new Task<Void>() {

			@Override
			public void onExecute() throws Exception {
				decoder = new DiskGifDecoder();
				if (Thread.interrupted()) throw new InterruptedException();
				decoder.read(imagePath);
			}
		}.withCallback(new TaskCallback<Void>() {

			@Override
			public void onFinish() {
				initializeTask = null;
			}

			@Override
			public void onSuccess(Void data) {
				worker = new WorkerTask();
				worker.async();
			}
		});
		initializeTask.async();
	}

	@Override
	public void close() {
		super.close();

		if (initializeTask != null) {
			initializeTask.cancel(true);
			initializeTask = null;
		}

		if (worker != null) {
			worker.cancel(true);
			worker = null;
		}

		if (decoder != null) decoder.close();
		if (currentFrame != null) {
			currentFrame.recycle();
			currentFrame = null;
		}
	}

	@Override
	public int getIntrinsicHeight() {
		return height;
	}

	@Override
	public int getIntrinsicWidth() {
		return width;
	}

	@Override
	public boolean isReady() {
		return initializeTask == null && currentFrame != null;
	}

	// ==============================================================
	// Скрытые методы
	// ==============================================================

	//	private void startAnimation() {
	//		if (worker == null) {
	//			worker = new WorkerTask();
	//			worker.execute();
	//		}
	//	}
	//
	//	private void stopAnimation() {
	//		if (worker != null) {
	//			worker.cancel(true);
	//			worker = null;
	//		}
	//	}

	@Override
	public void onDraw(Canvas canvas, Paint paint) {
		if (initializeTask != null) return;
		if (currentFrame != null && !currentFrame.isRecycled()) {
			canvas.drawBitmap(currentFrame, 0, 0, paint);
		}
	}

	private class WorkerTask extends Task<Void> {

		private static final int MIN_DELAY = 50;

		private Bitmap frame1;
		private Bitmap frame2;
		private int frame;

		@Override
		public void onExecute() throws Exception {
			Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);

			while (true) {
				if (Thread.interrupted()) throw new InterruptedException();
				if (!animationEnabled) {
					Thread.sleep(500);
					continue;
				}

				long time = SystemClock.uptimeMillis();
				long duration = 0;

				int count = decoder.getFrameCount();
				if (count > 0) {
					frame %= count;
					decoder.setImage(frame1);
					frame1 = decoder.getFrame(frame);
					duration = Math.max(MIN_DELAY, decoder.getDelay(frame));
					frame++;

					synchronized (GifDrawable.this) {
						mainHandler.removeCallbacksAndMessages(null);
						mainHandler.post(new Runnable() {

							@Override
							public void run() {
								currentFrame = frame1;
								frame1 = frame2;
								frame2 = currentFrame;

								synchronized (GifDrawable.this) {
									GifDrawable.this.notifyAll();
								}

								postInvalidate();
							}
						});

						GifDrawable.this.wait();
					}
				}

				time = SystemClock.uptimeMillis() - time;
				Thread.sleep(Math.max(0, duration - time));
			}
		}
	}
}