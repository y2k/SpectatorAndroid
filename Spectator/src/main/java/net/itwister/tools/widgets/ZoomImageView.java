package net.itwister.tools.widgets;

import net.itwister.tools.widgets.drawable.ZoomImageDrawable;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Контрол для показа картинок (с анимацией), с возможностью зума и перемещения.<br />
 * Version: 1.0.0.1
 */
public class ZoomImageView extends View {

	private static final String STATE_PARENT = "state_parent";
	private static final String STATE_BLOCKED = "state_blocked";

	private ScaleGestureDetector scaleDetector;
	private GestureDetector translateDetector;

	private Matrix imageMatrix;
	private ZoomImageDrawable imageDrawable;

	private Paint paint;

	private OnClickListener clickListener;

	private boolean touchBlocked;

	private Rect lastLayout;

	private Boolean pendingNormailze;

	private OverScrollerCompat scroller;
	private final Runnable scrollerRunnable = new Runnable() {

		@Override
		public void run() {
			if (scroller.computeScrollOffset()) {
				float[] m = new float[16];
				imageMatrix.getValues(m);
				m[Matrix.MTRANS_X] = scroller.getCurrX();
				m[Matrix.MTRANS_Y] = scroller.getCurrY();
				imageMatrix.setValues(m);

				invalidate();
				post(this);
			}
		}
	};

	private ProgressBar progressStub;
	private View stubView;

	// ==============================================================
	// Конструкторы
	// ==============================================================

	public ZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		scroller = new OverScrollerCompat(context);

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);

		imageMatrix = new Matrix();

		progressStub = new ProgressBar(context);

		scaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {

			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				if (touchBlocked) return false;

				Matrix m = new Matrix(getImageMatrix());
				float s = detector.getScaleFactor();
				m.postScale(s, s, detector.getFocusX(), detector.getFocusY());
				setImageMatrix(m);
				return true;
			}
		});

		translateDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				if (clickListener != null) {
					clickListener.onClick(ZoomImageView.this);
					return true;
				}
				return false;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				if (touchBlocked) return false;
				if (imageDrawable == null) return false;

				float[] m = new float[16];
				imageMatrix.getValues(m);

				float s = m[Matrix.MSCALE_X];
				int startX = (int) m[Matrix.MTRANS_X];
				int startY = (int) m[Matrix.MTRANS_Y];

				float minX = getMeasuredWidth() - imageDrawable.getIntrinsicWidth() * s;
				float maxX = 0;

				float minY = getMeasuredHeight() - imageDrawable.getIntrinsicHeight() * s;
				float maxY = 0;

				if (minX >= 0) minX = maxX = minX / 2;
				if (minY >= 0) minY = maxY = minY / 2;

				scroller.fling(startX, startY, (int) velocityX, (int) velocityY, (int) minX, (int) maxX, (int) minY, (int) maxY, 50, 50);
				post(scrollerRunnable);

				return true;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				if (touchBlocked) return false;

				Matrix m = new Matrix(getImageMatrix());
				m.postTranslate(-distanceX, -distanceY);
				setImageMatrix(m);
				return true;
			}
		});
	}

	// ==============================================================
	// Публичные методы
	// ==============================================================

	public void enableDrawableAnimation(boolean enable) {
		if (imageDrawable != null) imageDrawable.setAnimationEnable(enable);
	}

	public ZoomImageDrawable getImage() {
		return imageDrawable;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		scaleDetector.onTouchEvent(event);
		if (event.getPointerCount() == 1) translateDetector.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_UP) normalizePositionAndScale(false);
		return true;
	}

	public void setIgnoreUserTouch(boolean block) {
		touchBlocked = block;
	}

	public void setImage(ZoomImageDrawable image) {
		if (imageDrawable != image) {
			if (imageDrawable != null) imageDrawable.close();
			imageDrawable = image;

			if (imageDrawable != null) {
				imageDrawable.setAnimationEnable(true);
				imageDrawable.setInvalidateListener(new Runnable() {

					@Override
					public void run() {
						postInvalidate();
					}
				});
			}

			setZoomToFullscreen();
		}
	}

	public void setNotReadyView(View stubView) {
		this.stubView = stubView;
		invalidateNotReadyStub();
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		clickListener = l;
	}

	/** Устанавливает зум такой что-бы вся картинка поместилась на экране. */
	public void setZoomToFullscreen() {
		Matrix m = new Matrix(getImageMatrix());
		m.postScale(0.01f, 0.01f, 0, 0);
		setImageMatrix(m);
		normalizePositionAndScale(true);
	}

	// ==============================================================
	// Защищенные методы
	// ==============================================================

	@Override
	protected void onDraw(Canvas canvas) {
		invalidateNotReadyStub();

		if (imageDrawable != null && imageDrawable.isReady()) {
			canvas.save();
			canvas.concat(imageMatrix);
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG));
			imageDrawable.draw(canvas);
			canvas.restore();
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		Rect r = new Rect(left, top, right, bottom);
		if (changed && !r.equals(lastLayout)) {
			lastLayout = r;
		}

		if (pendingNormailze != null) normalizePositionAndScale(pendingNormailze);
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Bundle pstate = (Bundle) state;
		touchBlocked = pstate.getBoolean(STATE_BLOCKED);
		super.onRestoreInstanceState(pstate.getParcelable(STATE_PARENT));
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle state = new Bundle();
		state.putParcelable(STATE_PARENT, super.onSaveInstanceState());
		state.putBoolean(STATE_BLOCKED, touchBlocked);
		return state;
	}

	// ==============================================================
	// Скрытые методы
	// ==============================================================

	private Matrix getImageMatrix() { // XXX Убрать метод
		return imageMatrix;
	}

	private void invalidateNotReadyStub() {
		if (stubView != null) {
			if (imageDrawable == null || !imageDrawable.isReady()) {
				if (stubView.getVisibility() != View.VISIBLE) stubView.setVisibility(View.VISIBLE);
			} else {
				if (stubView.getVisibility() != View.GONE) stubView.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * Востанавливает минимальный зум.
	 * 
	 * @param forceNormalize
	 *            Если нельзя сейчас исправить зум сразу, устанавливается флаг
	 *            для нормализации при следующем layout'e.
	 */
	private void normalizePositionAndScale(boolean forceNormalize) {
		if (imageDrawable == null) return;

		if (getMeasuredWidth() == 0) pendingNormailze = forceNormalize;
		else {
			pendingNormailze = null;

			float s = Math.min(
					(float) getMeasuredWidth() / imageDrawable.getIntrinsicWidth(),
					(float) getMeasuredHeight() / imageDrawable.getIntrinsicHeight());

			float[] m = new float[16];
			imageMatrix.getValues(m);

			if (forceNormalize || m[Matrix.MSCALE_X] < s) {
				m[Matrix.MTRANS_X] = (getMeasuredWidth() - s * imageDrawable.getIntrinsicWidth()) / 2;
				m[Matrix.MTRANS_Y] = (getMeasuredHeight() - s * imageDrawable.getIntrinsicHeight()) / 2;
				m[Matrix.MSCALE_X] = m[Matrix.MSCALE_Y] = s;
				imageMatrix.setValues(m);
				invalidate();
			}
		}
	}

	private void setImageMatrix(Matrix imageMatrix) {
		this.imageMatrix = imageMatrix;
		invalidate();
	}
}