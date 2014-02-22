package net.itwister.tools.widgets.drawable;

import java.io.File;

import net.itwister.spectator.BuildConfig;
import net.itwister.tools.inner.Ln;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

public abstract class ZoomImageDrawable extends Drawable {

	private Runnable listener;

	protected volatile boolean animationEnabled;

	ZoomImageDrawable() {}

	public void close() {
		listener = null;
	}

	@SuppressLint("WrongCall")
	@Override
	public void draw(Canvas canvas) {
		onDraw(canvas, null);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.OPAQUE;
	}

	public abstract boolean isReady();

	@Override
	public void setAlpha(int alpha) {}

	public void setAnimationEnable(boolean enable) {
		animationEnabled = enable;
	}

	@Override
	public void setColorFilter(ColorFilter cf) {}

	public void setInvalidateListener(Runnable listener) {
		this.listener = listener;
	}

	protected void onDraw(Canvas canvas, Paint paint) {}

	protected void postInvalidate() {
		if (listener != null) listener.run();
	}

	public static ZoomImageDrawable fromBitmap(Bitmap source) {
		return new SimpleBitmapDrawable(source);
	}

	public static ZoomImageDrawable fromFile(File path, int width, int height) {
		Options op = new Options();
		op.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path.getAbsolutePath(), op);
		int sample = RegionBitmapDrawable.computeSample(op.outWidth, op.outHeight, width, height);

		if (BuildConfig.DEBUG) Ln.d("fromFile(path = %s), mime = %s, comp. sample = %s", path, op.outMimeType, sample);

		if (op.outMimeType != null && op.outMimeType.contains("gif")) return new GifDrawable(path, op.outWidth, op.outHeight);
		return sample > 1
				? new RegionBitmapDrawable(op.outWidth, op.outHeight, path, new Point(width, height))
				: new SimpleBitmapDrawable(path, op.outWidth, op.outHeight);
	}
}