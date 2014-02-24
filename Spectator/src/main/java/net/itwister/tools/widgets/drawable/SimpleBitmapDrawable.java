package net.itwister.tools.widgets.drawable;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import bindui.extra.Task;
import bindui.extra.Task.TaskCallback;

class SimpleBitmapDrawable extends ZoomImageDrawable {

	private int imageWidth;
	private int imageHeight;
	private Bitmap image;

	public SimpleBitmapDrawable(Bitmap source) {
		imageWidth = source.getWidth();
		imageHeight = source.getHeight();
		image = source;
	}

	public SimpleBitmapDrawable(final File file, int width, int height) {
		imageWidth = width;
		imageHeight = height;

		new Task<Bitmap>() {

			@Override
			public Bitmap onExecuteWithResult() throws Exception {
				return BitmapFactory.decodeFile(file.getAbsolutePath());
			}
		}.withCallback(new TaskCallback<Bitmap>() {

			@Override
			public void onSuccess(Bitmap t) {
				image = t;
				postInvalidate();
			}
		}).async();
	}

	@Override
	public void close() {
		super.close();
		if (image != null) image.recycle();
	}

	@Override
	public void draw(Canvas canvas) {
		if (image != null && !image.isRecycled()) canvas.drawBitmap(image, 0, 0, null);
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
		return image != null;
	}
}