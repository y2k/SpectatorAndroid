package net.itwister.tools.widgets;

import net.itwister.tools.inner.Ln;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.FloatMath;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

/** This class encapsulates scrolling with the ability to overshoot the bounds of
 * a scrolling operation. This class is a drop-in replacement for {@link android.widget.Scroller} in most cases. */
@SuppressWarnings("unused")
class OverScrollerCompat { // NO_UCD

	private int mMode;

	private final SplineOverScroller mScrollerX;
	private final SplineOverScroller mScrollerY;

	private final Interpolator mInterpolator;

	private final boolean mFlywheel;

	private static final int DEFAULT_DURATION = 250;
	private static final int SCROLL_MODE = 0;
	private static final int FLING_MODE = 1;

	/** Creates an OverScroller with a viscous fluid scroll interpolator and
	 * flywheel.
	 * 
	 * @param context */
	public OverScrollerCompat(Context context) {
		this(context, null);
	}

	/** Creates an OverScroller with flywheel enabled.
	 * 
	 * @param context
	 *        The context of this application.
	 * @param interpolator
	 *        The scroll interpolator. If null, a default (viscous)
	 *        interpolator will be used. */
	public OverScrollerCompat(Context context, Interpolator interpolator) {
		this(context, interpolator, true);
	}

	/** Creates an OverScroller.
	 * 
	 * @param context
	 *        The context of this application.
	 * @param interpolator
	 *        The scroll interpolator. If null, a default (viscous)
	 *        interpolator will be used.
	 * @param flywheel
	 *        If true, successive fling motions will keep on increasing
	 *        scroll speed.
	 * @hide */
	public OverScrollerCompat(Context context, Interpolator interpolator, boolean flywheel) {
		mInterpolator = interpolator;
		mFlywheel = flywheel;
		mScrollerX = new SplineOverScroller();
		mScrollerY = new SplineOverScroller();

		SplineOverScroller.initFromContext(context);
	}

	/** Creates an OverScroller with flywheel enabled.
	 * 
	 * @param context
	 *        The context of this application.
	 * @param interpolator
	 *        The scroll interpolator. If null, a default (viscous)
	 *        interpolator will be used.
	 * @param bounceCoefficientX
	 *        A value between 0 and 1 that will determine the proportion of
	 *        the velocity which is preserved in the bounce when the
	 *        horizontal edge is reached. A null value means no bounce. This
	 *        behavior is no longer supported and this coefficient has no
	 *        effect.
	 * @param bounceCoefficientY
	 *        Same as bounceCoefficientX but for the vertical direction.
	 *        This behavior is no longer supported and this coefficient has
	 *        no effect. !deprecated Use {!link #OverScroller(Context,
	 *        Interpolator, boolean)} instead. */
	public OverScrollerCompat(Context context, Interpolator interpolator,
			float bounceCoefficientX, float bounceCoefficientY) {
		this(context, interpolator, true);
	}

	/** Creates an OverScroller.
	 * 
	 * @param context
	 *        The context of this application.
	 * @param interpolator
	 *        The scroll interpolator. If null, a default (viscous)
	 *        interpolator will be used.
	 * @param bounceCoefficientX
	 *        A value between 0 and 1 that will determine the proportion of
	 *        the velocity which is preserved in the bounce when the
	 *        horizontal edge is reached. A null value means no bounce. This
	 *        behavior is no longer supported and this coefficient has no
	 *        effect.
	 * @param bounceCoefficientY
	 *        Same as bounceCoefficientX but for the vertical direction.
	 *        This behavior is no longer supported and this coefficient has
	 *        no effect.
	 * @param flywheel
	 *        If true, successive fling motions will keep on increasing
	 *        scroll speed. !deprecated Use {!link OverScroller(Context,
	 *        Interpolator, boolean)} instead. */
	public OverScrollerCompat(Context context, Interpolator interpolator,
			float bounceCoefficientX, float bounceCoefficientY, boolean flywheel) {
		this(context, interpolator, flywheel);
	}

	/** Stops the animation. Contrary to {@link #forceFinished(boolean)},
	 * aborting the animating causes the scroller to move to the final x and y
	 * positions.
	 * 
	 * @see #forceFinished(boolean) */
	public void abortAnimation() {
		mScrollerX.finish();
		mScrollerY.finish();
	}

	/** Call this when you want to know the new location. If it returns true, the
	 * animation is not yet finished. */
	public boolean computeScrollOffset() {
		if (isFinished()) {
			return false;
		}

		switch (mMode) {
			case SCROLL_MODE:
				long time = AnimationUtils.currentAnimationTimeMillis();
				// Any scroller can be used for time, since they were started
				// together in scroll mode. We use X here.
				final long elapsedTime = time - mScrollerX.mStartTime;

				final int duration = mScrollerX.mDuration;
				if (elapsedTime < duration) {
					float q = (float) (elapsedTime) / duration;

					if (mInterpolator == null) {
						q = Scroller.viscousFluid(q);
					} else {
						q = mInterpolator.getInterpolation(q);
					}

					mScrollerX.updateScroll(q);
					mScrollerY.updateScroll(q);
				} else {
					abortAnimation();
				}
				break;

			case FLING_MODE:
				if (!mScrollerX.mFinished) {
					if (!mScrollerX.update()) {
						if (!mScrollerX.continueWhenFinished()) {
							mScrollerX.finish();
						}
					}
				}

				if (!mScrollerY.mFinished) {
					if (!mScrollerY.update()) {
						if (!mScrollerY.continueWhenFinished()) {
							mScrollerY.finish();
						}
					}
				}

				break;
		}

		return true;
	}

	/** Extend the scroll animation. This allows a running animation to scroll
	 * further and longer, when used with {@link #setFinalX(int)} or {@link #setFinalY(int)}.
	 * 
	 * @param extend
	 *        Additional time to scroll in milliseconds.
	 * @see #setFinalX(int)
	 * @see #setFinalY(int)
	 * @hide Pending removal once nothing depends on it
	 * @deprecated OverScrollers don't necessarily have a fixed duration.
	 *             Instead of setting a new final position and extending the
	 *             duration of an existing scroll, use startScroll to begin a
	 *             new animation. */
	@Deprecated
	public void extendDuration(int extend) {
		mScrollerX.extendDuration(extend);
		mScrollerY.extendDuration(extend);
	}

	public void fling(int startX, int startY, int velocityX, int velocityY,
			int minX, int maxX, int minY, int maxY) {
		fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, 0, 0);
	}

	/** Start scrolling based on a fling gesture. The distance traveled will
	 * depend on the initial velocity of the fling.
	 * 
	 * @param startX
	 *        Starting point of the scroll (X)
	 * @param startY
	 *        Starting point of the scroll (Y)
	 * @param velocityX
	 *        Initial velocity of the fling (X) measured in pixels per
	 *        second.
	 * @param velocityY
	 *        Initial velocity of the fling (Y) measured in pixels per
	 *        second
	 * @param minX
	 *        Minimum X value. The scroller will not scroll past this point
	 *        unless overX > 0. If overfling is allowed, it will use minX as
	 *        a springback boundary.
	 * @param maxX
	 *        Maximum X value. The scroller will not scroll past this point
	 *        unless overX > 0. If overfling is allowed, it will use maxX as
	 *        a springback boundary.
	 * @param minY
	 *        Minimum Y value. The scroller will not scroll past this point
	 *        unless overY > 0. If overfling is allowed, it will use minY as
	 *        a springback boundary.
	 * @param maxY
	 *        Maximum Y value. The scroller will not scroll past this point
	 *        unless overY > 0. If overfling is allowed, it will use maxY as
	 *        a springback boundary.
	 * @param overX
	 *        Overfling range. If > 0, horizontal overfling in either
	 *        direction will be possible.
	 * @param overY
	 *        Overfling range. If > 0, vertical overfling in either
	 *        direction will be possible. */
	public void fling(int startX, int startY, int velocityX, int velocityY,
			int minX, int maxX, int minY, int maxY, int overX, int overY) {
		// Continue a scroll or fling in progress
		if (mFlywheel && !isFinished()) {
			float oldVelocityX = mScrollerX.mCurrVelocity;
			float oldVelocityY = mScrollerY.mCurrVelocity;
			if (Math.signum(velocityX) == Math.signum(oldVelocityX) &&
					Math.signum(velocityY) == Math.signum(oldVelocityY)) {
				velocityX += oldVelocityX;
				velocityY += oldVelocityY;
			}
		}

		mMode = FLING_MODE;
		mScrollerX.fling(startX, velocityX, minX, maxX, overX);
		mScrollerY.fling(startY, velocityY, minY, maxY, overY);
	}

	/** Force the finished field to a particular value. Contrary to {@link #abortAnimation()}, forcing the animation to finished does NOT
	 * cause the scroller to move to the final x and y position.
	 * 
	 * @param finished
	 *        The new finished value. */
	public final void forceFinished(boolean finished) {
		mScrollerX.mFinished = mScrollerY.mFinished = finished;
	}

	/** Returns the absolute value of the current velocity.
	 * 
	 * @return The original velocity less the deceleration, norm of the X and Y
	 *         velocity vector. */
	public float getCurrVelocity() {
		float squaredNorm = mScrollerX.mCurrVelocity * mScrollerX.mCurrVelocity;
		squaredNorm += mScrollerY.mCurrVelocity * mScrollerY.mCurrVelocity;
		return FloatMath.sqrt(squaredNorm);
	}

	/** Returns the current X offset in the scroll.
	 * 
	 * @return The new X offset as an absolute distance from the origin. */
	public final int getCurrX() {
		return mScrollerX.mCurrentPosition;
	}

	/** Returns the current Y offset in the scroll.
	 * 
	 * @return The new Y offset as an absolute distance from the origin. */
	public final int getCurrY() {
		return mScrollerY.mCurrentPosition;
	}

	/** Returns how long the scroll event will take, in milliseconds.
	 * 
	 * @return The duration of the scroll in milliseconds.
	 * @hide Pending removal once nothing depends on it
	 * @deprecated OverScrollers don't necessarily have a fixed duration. This
	 *             function will lie to the best of its ability. */
	@Deprecated
	public final int getDuration() {
		return Math.max(mScrollerX.mDuration, mScrollerY.mDuration);
	}

	/** Returns where the scroll will end. Valid only for "fling" scrolls.
	 * 
	 * @return The final X offset as an absolute distance from the origin. */
	public final int getFinalX() {
		return mScrollerX.mFinal;
	}

	/** Returns where the scroll will end. Valid only for "fling" scrolls.
	 * 
	 * @return The final Y offset as an absolute distance from the origin. */
	public final int getFinalY() {
		return mScrollerY.mFinal;
	}

	/** Returns the start X offset in the scroll.
	 * 
	 * @return The start X offset as an absolute distance from the origin. */
	public final int getStartX() {
		return mScrollerX.mStart;
	}

	/** Returns the start Y offset in the scroll.
	 * 
	 * @return The start Y offset as an absolute distance from the origin. */
	public final int getStartY() {
		return mScrollerY.mStart;
	}

	/** Returns whether the scroller has finished scrolling.
	 * 
	 * @return True if the scroller has finished scrolling, false otherwise. */
	public final boolean isFinished() {
		return mScrollerX.mFinished && mScrollerY.mFinished;
	}

	/** Returns whether the current Scroller is currently returning to a valid
	 * position. Valid bounds were provided by the {@link #fling(int, int, int, int, int, int, int, int, int, int)} method.
	 * One should check this value before calling {@link #startScroll(int, int, int, int)} as the interpolation currently
	 * in progress to restore a valid position will then be stopped. The caller
	 * has to take into account the fact that the started scroll will start from
	 * an overscrolled position.
	 * 
	 * @return true when the current position is overscrolled and in the process
	 *         of interpolating back to a valid value. */
	public boolean isOverScrolled() {
		return ((!mScrollerX.mFinished && mScrollerX.mState != SplineOverScroller.SPLINE) || (!mScrollerY.mFinished && mScrollerY.mState != SplineOverScroller.SPLINE));
	}

	/** @hide */
	public boolean isScrollingInDirection(float xvel, float yvel) {
		final int dx = mScrollerX.mFinal - mScrollerX.mStart;
		final int dy = mScrollerY.mFinal - mScrollerY.mStart;
		return !isFinished() && Math.signum(xvel) == Math.signum(dx) &&
				Math.signum(yvel) == Math.signum(dy);
	}

	/** Notify the scroller that we've reached a horizontal boundary. Normally
	 * the information to handle this will already be known when the animation
	 * is started, such as in a call to one of the fling functions. However
	 * there are cases where this cannot be known in advance. This function will
	 * transition the current motion and animate from startX to finalX as
	 * appropriate.
	 * 
	 * @param startX
	 *        Starting/current X position
	 * @param finalX
	 *        Desired final X position
	 * @param overX
	 *        Magnitude of overscroll allowed. This should be the maximum
	 *        desired distance from finalX. Absolute value - must be
	 *        positive. */
	public void notifyHorizontalEdgeReached(int startX, int finalX, int overX) {
		mScrollerX.notifyEdgeReached(startX, finalX, overX);
	}

	/** Notify the scroller that we've reached a vertical boundary. Normally the
	 * information to handle this will already be known when the animation is
	 * started, such as in a call to one of the fling functions. However there
	 * are cases where this cannot be known in advance. This function will
	 * animate a parabolic motion from startY to finalY.
	 * 
	 * @param startY
	 *        Starting/current Y position
	 * @param finalY
	 *        Desired final Y position
	 * @param overY
	 *        Magnitude of overscroll allowed. This should be the maximum
	 *        desired distance from finalY. Absolute value - must be
	 *        positive. */
	public void notifyVerticalEdgeReached(int startY, int finalY, int overY) {
		mScrollerY.notifyEdgeReached(startY, finalY, overY);
	}

	/** Sets the final position (X) for this scroller.
	 * 
	 * @param newX
	 *        The new X offset as an absolute distance from the origin.
	 * @see #extendDuration(int)
	 * @see #setFinalY(int)
	 * @hide Pending removal once nothing depends on it
	 * @deprecated OverScroller's final position may change during an animation.
	 *             Instead of setting a new final position and extending the
	 *             duration of an existing scroll, use startScroll to begin a
	 *             new animation. */
	@Deprecated
	public void setFinalX(int newX) {
		mScrollerX.setFinalPosition(newX);
	}

	/** Sets the final position (Y) for this scroller.
	 * 
	 * @param newY
	 *        The new Y offset as an absolute distance from the origin.
	 * @see #extendDuration(int)
	 * @see #setFinalX(int)
	 * @hide Pending removal once nothing depends on it
	 * @deprecated OverScroller's final position may change during an animation.
	 *             Instead of setting a new final position and extending the
	 *             duration of an existing scroll, use startScroll to begin a
	 *             new animation. */
	@Deprecated
	public void setFinalY(int newY) {
		mScrollerY.setFinalPosition(newY);
	}

	/** The amount of friction applied to flings. The default value is {@link ViewConfiguration#getScrollFriction}.
	 * 
	 * @param friction
	 *        A scalar dimension-less value representing the coefficient of
	 *        friction. */
	public final void setFriction(float friction) {
		mScrollerX.setFriction(friction);
		mScrollerY.setFriction(friction);
	}

	/** Call this when you want to 'spring back' into a valid coordinate range.
	 * 
	 * @param startX
	 *        Starting X coordinate
	 * @param startY
	 *        Starting Y coordinate
	 * @param minX
	 *        Minimum valid X value
	 * @param maxX
	 *        Maximum valid X value
	 * @param minY
	 *        Minimum valid Y value
	 * @param maxY
	 *        Minimum valid Y value
	 * @return true if a springback was initiated, false if startX and startY
	 *         were already within the valid range. */
	public boolean springBack(int startX, int startY, int minX, int maxX, int minY, int maxY) {
		mMode = FLING_MODE;

		// Make sure both methods are called.
		final boolean spingbackX = mScrollerX.springback(startX, minX, maxX);
		final boolean spingbackY = mScrollerY.springback(startY, minY, maxY);
		return spingbackX || spingbackY;
	}

	/** Start scrolling by providing a starting point and the distance to travel.
	 * The scroll will use the default value of 250 milliseconds for the
	 * duration.
	 * 
	 * @param startX
	 *        Starting horizontal scroll offset in pixels. Positive numbers
	 *        will scroll the content to the left.
	 * @param startY
	 *        Starting vertical scroll offset in pixels. Positive numbers
	 *        will scroll the content up.
	 * @param dx
	 *        Horizontal distance to travel. Positive numbers will scroll
	 *        the content to the left.
	 * @param dy
	 *        Vertical distance to travel. Positive numbers will scroll the
	 *        content up. */
	public void startScroll(int startX, int startY, int dx, int dy) {
		startScroll(startX, startY, dx, dy, DEFAULT_DURATION);
	}

	/** Start scrolling by providing a starting point and the distance to travel.
	 * 
	 * @param startX
	 *        Starting horizontal scroll offset in pixels. Positive numbers
	 *        will scroll the content to the left.
	 * @param startY
	 *        Starting vertical scroll offset in pixels. Positive numbers
	 *        will scroll the content up.
	 * @param dx
	 *        Horizontal distance to travel. Positive numbers will scroll
	 *        the content to the left.
	 * @param dy
	 *        Vertical distance to travel. Positive numbers will scroll the
	 *        content up.
	 * @param duration
	 *        Duration of the scroll in milliseconds. */
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		mMode = SCROLL_MODE;
		mScrollerX.startScroll(startX, dx, duration);
		mScrollerY.startScroll(startY, dy, duration);
	}

	/** Returns the time elapsed since the beginning of the scrolling.
	 * 
	 * @return The elapsed time in milliseconds.
	 * @hide */
	public int timePassed() {
		final long time = AnimationUtils.currentAnimationTimeMillis();
		final long startTime = Math.min(mScrollerX.mStartTime, mScrollerY.mStartTime);
		return (int) (time - startTime);
	}

	private static class Scroller {
		private int mMode;

		private int mStartX;
		private int mStartY;
		private int mFinalX;
		private int mFinalY;

		private int mMinX;
		private int mMaxX;
		private int mMinY;
		private int mMaxY;

		private int mCurrX;
		private int mCurrY;
		private long mStartTime;
		private int mDuration;
		private float mDurationReciprocal;
		private float mDeltaX;
		private float mDeltaY;
		private boolean mFinished;
		private final Interpolator mInterpolator;
		private final boolean mFlywheel;

		private float mVelocity;

		private static final int DEFAULT_DURATION = 250;
		private static final int SCROLL_MODE = 0;
		private static final int FLING_MODE = 1;

		private static float DECELERATION_RATE = (float) (Math.log(0.75) / Math.log(0.9));
		private static float ALPHA = 800; // pixels / seconds
		private static float START_TENSION = 0.4f; // Tension at start: (0.4 * total T, 1.0 * Distance)
		private static float END_TENSION = 1.0f - START_TENSION;
		private static final int NB_SAMPLES = 100;
		private static final float[] SPLINE = new float[NB_SAMPLES + 1];

		private float mDeceleration;
		private final float mPpi;

		static {
			float x_min = 0.0f;
			for (int i = 0; i <= NB_SAMPLES; i++) {
				final float t = (float) i / NB_SAMPLES;
				float x_max = 1.0f;
				float x, tx, coef;
				while (true) {
					x = x_min + (x_max - x_min) / 2.0f;
					coef = 3.0f * x * (1.0f - x);
					tx = coef * ((1.0f - x) * START_TENSION + x * END_TENSION) + x * x * x;
					if (Math.abs(tx - t) < 1E-5) break;
					if (tx > t) x_max = x;
					else x_min = x;
				}
				final float d = coef + x * x * x;
				SPLINE[i] = d;
			}
			SPLINE[NB_SAMPLES] = 1.0f;

			// This controls the viscous fluid effect (how much of it)
			sViscousFluidScale = 8.0f;
			// must be set to 1.0 (used in viscousFluid())
			sViscousFluidNormalize = 1.0f;
			sViscousFluidNormalize = 1.0f / viscousFluid(1.0f);
		}

		private static float sViscousFluidScale;
		private static float sViscousFluidNormalize;

		/** Create a Scroller with the default duration and interpolator. */
		public Scroller(Context context) {
			this(context, null);
		}

		/** Create a Scroller with the specified interpolator. If the
		 * interpolator is null, the default (viscous) interpolator will be
		 * used. "Flywheel" behavior will be in effect for apps targeting
		 * Honeycomb or newer. */
		public Scroller(Context context, Interpolator interpolator) {
			this(context, interpolator,
					context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.HONEYCOMB);
		}

		/** Create a Scroller with the specified interpolator. If the
		 * interpolator is null, the default (viscous) interpolator will be
		 * used. Specify whether or not to support progressive "flywheel"
		 * behavior in flinging. */
		public Scroller(Context context, Interpolator interpolator, boolean flywheel) {
			mFinished = true;
			mInterpolator = interpolator;
			mPpi = context.getResources().getDisplayMetrics().density * 160.0f;
			mDeceleration = computeDeceleration(ViewConfiguration.getScrollFriction());
			mFlywheel = flywheel;
		}

		/** Stops the animation. Contrary to {@link #forceFinished(boolean)},
		 * aborting the animating cause the scroller to move to the final x and
		 * y position
		 * 
		 * @see #forceFinished(boolean) */
		public void abortAnimation() {
			mCurrX = mFinalX;
			mCurrY = mFinalY;
			mFinished = true;
		}

		/** Call this when you want to know the new location. If it returns true,
		 * the animation is not yet finished. loc will be altered to provide the
		 * new location. */
		public boolean computeScrollOffset() {
			if (mFinished) {
				return false;
			}

			int timePassed = (int) (AnimationUtils.currentAnimationTimeMillis() - mStartTime);

			if (timePassed < mDuration) {
				switch (mMode) {
					case SCROLL_MODE:
						float x = timePassed * mDurationReciprocal;

						if (mInterpolator == null) x = viscousFluid(x);
						else x = mInterpolator.getInterpolation(x);

						mCurrX = mStartX + Math.round(x * mDeltaX);
						mCurrY = mStartY + Math.round(x * mDeltaY);
						break;
					case FLING_MODE:
						final float t = (float) timePassed / mDuration;
						final int index = (int) (NB_SAMPLES * t);
						final float t_inf = (float) index / NB_SAMPLES;
						final float t_sup = (float) (index + 1) / NB_SAMPLES;
						final float d_inf = SPLINE[index];
						final float d_sup = SPLINE[index + 1];
						final float distanceCoef = d_inf + (t - t_inf) / (t_sup - t_inf) * (d_sup - d_inf);

						mCurrX = mStartX + Math.round(distanceCoef * (mFinalX - mStartX));
						// Pin to mMinX <= mCurrX <= mMaxX
						mCurrX = Math.min(mCurrX, mMaxX);
						mCurrX = Math.max(mCurrX, mMinX);

						mCurrY = mStartY + Math.round(distanceCoef * (mFinalY - mStartY));
						// Pin to mMinY <= mCurrY <= mMaxY
						mCurrY = Math.min(mCurrY, mMaxY);
						mCurrY = Math.max(mCurrY, mMinY);

						if (mCurrX == mFinalX && mCurrY == mFinalY) {
							mFinished = true;
						}

						break;
				}
			}
			else {
				mCurrX = mFinalX;
				mCurrY = mFinalY;
				mFinished = true;
			}
			return true;
		}

		/** Extend the scroll animation. This allows a running animation to
		 * scroll further and longer, when used with {@link #setFinalX(int)} or {@link #setFinalY(int)}.
		 * 
		 * @param extend
		 *        Additional time to scroll in milliseconds.
		 * @see #setFinalX(int)
		 * @see #setFinalY(int) */
		public void extendDuration(int extend) {
			int passed = timePassed();
			mDuration = passed + extend;
			mDurationReciprocal = 1.0f / mDuration;
			mFinished = false;
		}

		/** Start scrolling based on a fling gesture. The distance travelled will
		 * depend on the initial velocity of the fling.
		 * 
		 * @param startX
		 *        Starting point of the scroll (X)
		 * @param startY
		 *        Starting point of the scroll (Y)
		 * @param velocityX
		 *        Initial velocity of the fling (X) measured in pixels per
		 *        second.
		 * @param velocityY
		 *        Initial velocity of the fling (Y) measured in pixels per
		 *        second
		 * @param minX
		 *        Minimum X value. The scroller will not scroll past this
		 *        point.
		 * @param maxX
		 *        Maximum X value. The scroller will not scroll past this
		 *        point.
		 * @param minY
		 *        Minimum Y value. The scroller will not scroll past this
		 *        point.
		 * @param maxY
		 *        Maximum Y value. The scroller will not scroll past this
		 *        point. */
		public void fling(int startX, int startY, int velocityX, int velocityY,
				int minX, int maxX, int minY, int maxY) {
			// Continue a scroll or fling in progress
			if (mFlywheel && !mFinished) {
				float oldVel = getCurrVelocity();

				float dx = (mFinalX - mStartX);
				float dy = (mFinalY - mStartY);
				float hyp = FloatMath.sqrt(dx * dx + dy * dy);

				float ndx = dx / hyp;
				float ndy = dy / hyp;

				float oldVelocityX = ndx * oldVel;
				float oldVelocityY = ndy * oldVel;
				if (Math.signum(velocityX) == Math.signum(oldVelocityX) &&
						Math.signum(velocityY) == Math.signum(oldVelocityY)) {
					velocityX += oldVelocityX;
					velocityY += oldVelocityY;
				}
			}

			mMode = FLING_MODE;
			mFinished = false;

			float velocity = FloatMath.sqrt(velocityX * velocityX + velocityY * velocityY);

			mVelocity = velocity;
			final double l = Math.log(START_TENSION * velocity / ALPHA);
			mDuration = (int) (1000.0 * Math.exp(l / (DECELERATION_RATE - 1.0)));
			mStartTime = AnimationUtils.currentAnimationTimeMillis();
			mStartX = startX;
			mStartY = startY;

			float coeffX = velocity == 0 ? 1.0f : velocityX / velocity;
			float coeffY = velocity == 0 ? 1.0f : velocityY / velocity;

			int totalDistance =
					(int) (ALPHA * Math.exp(DECELERATION_RATE / (DECELERATION_RATE - 1.0) * l));

			mMinX = minX;
			mMaxX = maxX;
			mMinY = minY;
			mMaxY = maxY;

			mFinalX = startX + Math.round(totalDistance * coeffX);
			// Pin to mMinX <= mFinalX <= mMaxX
			mFinalX = Math.min(mFinalX, mMaxX);
			mFinalX = Math.max(mFinalX, mMinX);

			mFinalY = startY + Math.round(totalDistance * coeffY);
			// Pin to mMinY <= mFinalY <= mMaxY
			mFinalY = Math.min(mFinalY, mMaxY);
			mFinalY = Math.max(mFinalY, mMinY);
		}

		/** Force the finished field to a particular value.
		 * 
		 * @param finished
		 *        The new finished value. */
		public final void forceFinished(boolean finished) {
			mFinished = finished;
		}

		/** Returns the current velocity.
		 * 
		 * @return The original velocity less the deceleration. Result may be
		 *         negative. */
		public float getCurrVelocity() {
			return mVelocity - mDeceleration * timePassed() / 2000.0f;
		}

		/** Returns the current X offset in the scroll.
		 * 
		 * @return The new X offset as an absolute distance from the origin. */
		public final int getCurrX() {
			return mCurrX;
		}

		/** Returns the current Y offset in the scroll.
		 * 
		 * @return The new Y offset as an absolute distance from the origin. */
		public final int getCurrY() {
			return mCurrY;
		}

		/** Returns how long the scroll event will take, in milliseconds.
		 * 
		 * @return The duration of the scroll in milliseconds. */
		public final int getDuration() {
			return mDuration;
		}

		/** Returns where the scroll will end. Valid only for "fling" scrolls.
		 * 
		 * @return The final X offset as an absolute distance from the origin. */
		public final int getFinalX() {
			return mFinalX;
		}

		/** Returns where the scroll will end. Valid only for "fling" scrolls.
		 * 
		 * @return The final Y offset as an absolute distance from the origin. */
		public final int getFinalY() {
			return mFinalY;
		}

		/** Returns the start X offset in the scroll.
		 * 
		 * @return The start X offset as an absolute distance from the origin. */
		public final int getStartX() {
			return mStartX;
		}

		/** Returns the start Y offset in the scroll.
		 * 
		 * @return The start Y offset as an absolute distance from the origin. */
		public final int getStartY() {
			return mStartY;
		}

		/** Returns whether the scroller has finished scrolling.
		 * 
		 * @return True if the scroller has finished scrolling, false otherwise. */
		public final boolean isFinished() {
			return mFinished;
		}

		/** @hide */
		public boolean isScrollingInDirection(float xvel, float yvel) {
			return !mFinished && Math.signum(xvel) == Math.signum(mFinalX - mStartX) &&
					Math.signum(yvel) == Math.signum(mFinalY - mStartY);
		}

		/** Sets the final position (X) for this scroller.
		 * 
		 * @param newX
		 *        The new X offset as an absolute distance from the origin.
		 * @see #extendDuration(int)
		 * @see #setFinalY(int) */
		public void setFinalX(int newX) {
			mFinalX = newX;
			mDeltaX = mFinalX - mStartX;
			mFinished = false;
		}

		/** Sets the final position (Y) for this scroller.
		 * 
		 * @param newY
		 *        The new Y offset as an absolute distance from the origin.
		 * @see #extendDuration(int)
		 * @see #setFinalX(int) */
		public void setFinalY(int newY) {
			mFinalY = newY;
			mDeltaY = mFinalY - mStartY;
			mFinished = false;
		}

		/** The amount of friction applied to flings. The default value is {@link ViewConfiguration#getScrollFriction}.
		 * 
		 * @param friction
		 *        A scalar dimension-less value representing the coefficient
		 *        of friction. */
		public final void setFriction(float friction) {
			mDeceleration = computeDeceleration(friction);
		}

		/** Start scrolling by providing a starting point and the distance to
		 * travel. The scroll will use the default value of 250 milliseconds for
		 * the duration.
		 * 
		 * @param startX
		 *        Starting horizontal scroll offset in pixels. Positive
		 *        numbers will scroll the content to the left.
		 * @param startY
		 *        Starting vertical scroll offset in pixels. Positive
		 *        numbers will scroll the content up.
		 * @param dx
		 *        Horizontal distance to travel. Positive numbers will
		 *        scroll the content to the left.
		 * @param dy
		 *        Vertical distance to travel. Positive numbers will scroll
		 *        the content up. */
		public void startScroll(int startX, int startY, int dx, int dy) {
			startScroll(startX, startY, dx, dy, DEFAULT_DURATION);
		}

		/** Start scrolling by providing a starting point and the distance to
		 * travel.
		 * 
		 * @param startX
		 *        Starting horizontal scroll offset in pixels. Positive
		 *        numbers will scroll the content to the left.
		 * @param startY
		 *        Starting vertical scroll offset in pixels. Positive
		 *        numbers will scroll the content up.
		 * @param dx
		 *        Horizontal distance to travel. Positive numbers will
		 *        scroll the content to the left.
		 * @param dy
		 *        Vertical distance to travel. Positive numbers will scroll
		 *        the content up.
		 * @param duration
		 *        Duration of the scroll in milliseconds. */
		public void startScroll(int startX, int startY, int dx, int dy, int duration) {
			mMode = SCROLL_MODE;
			mFinished = false;
			mDuration = duration;
			mStartTime = AnimationUtils.currentAnimationTimeMillis();
			mStartX = startX;
			mStartY = startY;
			mFinalX = startX + dx;
			mFinalY = startY + dy;
			mDeltaX = dx;
			mDeltaY = dy;
			mDurationReciprocal = 1.0f / mDuration;
		}

		/** Returns the time elapsed since the beginning of the scrolling.
		 * 
		 * @return The elapsed time in milliseconds. */
		public int timePassed() {
			return (int) (AnimationUtils.currentAnimationTimeMillis() - mStartTime);
		}

		private float computeDeceleration(float friction) {
			return SensorManager.GRAVITY_EARTH // g (m/s^2)
					* 39.37f // inch/meter
					* mPpi // pixels per inch
					* friction;
		}

		static float viscousFluid(float x)
		{
			x *= sViscousFluidScale;
			if (x < 1.0f) {
				x -= (1.0f - (float) Math.exp(-x));
			} else {
				float start = 0.36787944117f; // 1/e == exp(-1)
				x = 1.0f - (float) Math.exp(1.0f - x);
				x = start + x * (1.0f - start);
			}
			x *= sViscousFluidNormalize;
			return x;
		}
	}

	static class SplineOverScroller { // NO_UCD
		// Initial position
		private int mStart;

		// Current position
		private int mCurrentPosition;

		// Final position
		private int mFinal;

		// Initial velocity
		private int mVelocity;

		// Current velocity
		private float mCurrVelocity;

		// Constant current deceleration
		private float mDeceleration;

		// Animation starting time, in system milliseconds
		private long mStartTime;

		// Animation duration, in milliseconds
		private int mDuration;

		// Duration to complete spline component of animation
		private int mSplineDuration;

		// Distance to travel along spline animation
		private int mSplineDistance;

		// Whether the animation is currently in progress
		private boolean mFinished;

		// The allowed overshot distance before boundary is reached.
		private int mOver;

		// Fling friction
		private float mFlingFriction = ViewConfiguration.getScrollFriction();

		// Current state of the animation.
		private int mState = SPLINE;

		// Constant gravity value, used in the deceleration phase.
		private static final float GRAVITY = 2000.0f;

		// A device specific coefficient adjusted to physical values.
		private static float PHYSICAL_COEF;

		private static float DECELERATION_RATE = (float) (Math.log(0.78) / Math.log(0.9));
		private static final float INFLEXION = 0.35f; // Tension lines cross at (INFLEXION, 1)
		private static final float START_TENSION = 0.5f;
		private static final float END_TENSION = 1.0f;
		private static final float P1 = START_TENSION * INFLEXION;
		private static final float P2 = 1.0f - END_TENSION * (1.0f - INFLEXION);

		private static final int NB_SAMPLES = 100;
		private static final float[] SPLINE_POSITION = new float[NB_SAMPLES + 1];
		private static final float[] SPLINE_TIME = new float[NB_SAMPLES + 1];

		private static final int SPLINE = 0;
		private static final int CUBIC = 1;
		private static final int BALLISTIC = 2;

		static {
			float x_min = 0.0f;
			float y_min = 0.0f;
			for (int i = 0; i < NB_SAMPLES; i++) {
				final float alpha = (float) i / NB_SAMPLES;

				float x_max = 1.0f;
				float x, tx, coef;
				while (true) {
					x = x_min + (x_max - x_min) / 2.0f;
					coef = 3.0f * x * (1.0f - x);
					tx = coef * ((1.0f - x) * P1 + x * P2) + x * x * x;
					if (Math.abs(tx - alpha) < 1E-5) break;
					if (tx > alpha) x_max = x;
					else x_min = x;
				}
				SPLINE_POSITION[i] = coef * ((1.0f - x) * START_TENSION + x) + x * x * x;

				float y_max = 1.0f;
				float y, dy;
				while (true) {
					y = y_min + (y_max - y_min) / 2.0f;
					coef = 3.0f * y * (1.0f - y);
					dy = coef * ((1.0f - y) * START_TENSION + y) + y * y * y;
					if (Math.abs(dy - alpha) < 1E-5) break;
					if (dy > alpha) y_max = y;
					else y_min = y;
				}
				SPLINE_TIME[i] = coef * ((1.0f - y) * P1 + y * P2) + y * y * y;
			}
			SPLINE_POSITION[NB_SAMPLES] = SPLINE_TIME[NB_SAMPLES] = 1.0f;
		}

		SplineOverScroller() {
			mFinished = true;
		}

		/*
		 * Modifies mDuration to the duration it takes to get from start to
		 * newFinal using the spline interpolation. The previous duration was
		 * needed to get to oldFinal.
		 */
		private void adjustDuration(int start, int oldFinal, int newFinal) {
			final int oldDistance = oldFinal - start;
			final int newDistance = newFinal - start;
			final float x = Math.abs((float) newDistance / oldDistance);
			final int index = (int) (NB_SAMPLES * x);
			if (index < NB_SAMPLES) {
				final float x_inf = (float) index / NB_SAMPLES;
				final float x_sup = (float) (index + 1) / NB_SAMPLES;
				final float t_inf = SPLINE_TIME[index];
				final float t_sup = SPLINE_TIME[index + 1];
				final float timeCoef = t_inf + (x - x_inf) / (x_sup - x_inf) * (t_sup - t_inf);
				mDuration *= timeCoef;
			}
		}

		private void fitOnBounceCurve(int start, int end, int velocity) {
			// Simulate a bounce that started from edge
			final float durationToApex = -velocity / mDeceleration;
			final float distanceToApex = velocity * velocity / 2.0f / Math.abs(mDeceleration);
			final float distanceToEdge = Math.abs(end - start);
			final float totalDuration = (float) Math.sqrt(
					2.0 * (distanceToApex + distanceToEdge) / Math.abs(mDeceleration));
			mStartTime -= (int) (1000.0f * (totalDuration - durationToApex));
			mStart = end;
			mVelocity = (int) (-mDeceleration * totalDuration);
		}

		private double getSplineDeceleration(int velocity) {
			return Math.log(INFLEXION * Math.abs(velocity) / (mFlingFriction * PHYSICAL_COEF));
		}

		private double getSplineFlingDistance(int velocity) {
			final double l = getSplineDeceleration(velocity);
			final double decelMinusOne = DECELERATION_RATE - 1.0;
			return mFlingFriction * PHYSICAL_COEF * Math.exp(DECELERATION_RATE / decelMinusOne * l);
		}

		/* Returns the duration, expressed in milliseconds */
		private int getSplineFlingDuration(int velocity) {
			final double l = getSplineDeceleration(velocity);
			final double decelMinusOne = DECELERATION_RATE - 1.0;
			return (int) (1000.0 * Math.exp(l / decelMinusOne));
		}

		private void onEdgeReached() {
			// mStart, mVelocity and mStartTime were adjusted to their values when edge was reached.
			float distance = mVelocity * mVelocity / (2.0f * Math.abs(mDeceleration));
			final float sign = Math.signum(mVelocity);

			if (distance > mOver) {
				// Default deceleration is not sufficient to slow us down before boundary
				mDeceleration = -sign * mVelocity * mVelocity / (2.0f * mOver);
				distance = mOver;
			}

			mOver = (int) distance;
			mState = BALLISTIC;
			mFinal = mStart + (int) (mVelocity > 0 ? distance : -distance);
			mDuration = -(int) (1000.0f * mVelocity / mDeceleration);
		}

		private void startAfterEdge(int start, int min, int max, int velocity) {
			if (start > min && start < max) {
				Ln.e("startAfterEdge called from a valid position");
				mFinished = true;
				return;
			}
			final boolean positive = start > max;
			final int edge = positive ? max : min;
			final int overDistance = start - edge;
			boolean keepIncreasing = overDistance * velocity >= 0;
			if (keepIncreasing) {
				// Will result in a bounce or a to_boundary depending on velocity.
				startBounceAfterEdge(start, edge, velocity);
			} else {
				final double totalDistance = getSplineFlingDistance(velocity);
				if (totalDistance > Math.abs(overDistance)) {
					fling(start, velocity, positive ? min : start, positive ? start : max, mOver);
				} else {
					startSpringback(start, edge, velocity);
				}
			}
		}

		private void startBounceAfterEdge(int start, int end, int velocity) {
			mDeceleration = getDeceleration(velocity == 0 ? start - end : velocity);
			fitOnBounceCurve(start, end, velocity);
			onEdgeReached();
		}

		private void startSpringback(int start, int end, int velocity) {
			// mStartTime has been set
			mFinished = false;
			mState = CUBIC;
			mStart = start;
			mFinal = end;
			final int delta = start - end;
			mDeceleration = getDeceleration(delta);
			// TODO take velocity into account
			mVelocity = -delta; // only sign is used
			mOver = Math.abs(delta);
			mDuration = (int) (1000.0 * Math.sqrt(-2.0 * delta / mDeceleration));
		}

		boolean continueWhenFinished() {
			switch (mState) {
				case SPLINE:
					// Duration from start to null velocity
					if (mDuration < mSplineDuration) {
						// If the animation was clamped, we reached the edge
						mStart = mFinal;
						// TODO Better compute speed when edge was reached
						mVelocity = (int) mCurrVelocity;
						mDeceleration = getDeceleration(mVelocity);
						mStartTime += mDuration;
						onEdgeReached();
					} else {
						// Normal stop, no need to continue
						return false;
					}
					break;
				case BALLISTIC:
					mStartTime += mDuration;
					startSpringback(mFinal, mStart, 0);
					break;
				case CUBIC:
					return false;
			}

			update();
			return true;
		}

		void extendDuration(int extend) {
			final long time = AnimationUtils.currentAnimationTimeMillis();
			final int elapsedTime = (int) (time - mStartTime);
			mDuration = elapsedTime + extend;
			mFinished = false;
		}

		void finish() {
			mCurrentPosition = mFinal;
			// Not reset since WebView relies on this value for fast fling.
			// TODO: restore when WebView uses the fast fling implemented in this class.
			// mCurrVelocity = 0.0f;
			mFinished = true;
		}

		void fling(int start, int velocity, int min, int max, int over) {
			mOver = over;
			mFinished = false;
			mCurrVelocity = mVelocity = velocity;
			mDuration = mSplineDuration = 0;
			mStartTime = AnimationUtils.currentAnimationTimeMillis();
			mCurrentPosition = mStart = start;

			if (start > max || start < min) {
				startAfterEdge(start, min, max, velocity);
				return;
			}

			mState = SPLINE;
			double totalDistance = 0.0;

			if (velocity != 0) {
				mDuration = mSplineDuration = getSplineFlingDuration(velocity);
				totalDistance = getSplineFlingDistance(velocity);
			}

			mSplineDistance = (int) (totalDistance * Math.signum(velocity));
			mFinal = start + mSplineDistance;

			// Clamp to a valid final position
			if (mFinal < min) {
				adjustDuration(mStart, mFinal, min);
				mFinal = min;
			}

			if (mFinal > max) {
				adjustDuration(mStart, mFinal, max);
				mFinal = max;
			}
		}

		void notifyEdgeReached(int start, int end, int over) {
			// mState is used to detect successive notifications 
			if (mState == SPLINE) {
				mOver = over;
				mStartTime = AnimationUtils.currentAnimationTimeMillis();
				// We were in fling/scroll mode before: current velocity is such that distance to
				// edge is increasing. This ensures that startAfterEdge will not start a new fling.
				startAfterEdge(start, end, end, (int) mCurrVelocity);
			}
		}

		void setFinalPosition(int position) {
			mFinal = position;
			mFinished = false;
		}

		void setFriction(float friction) {
			mFlingFriction = friction;
		}

		boolean springback(int start, int min, int max) {
			mFinished = true;

			mStart = mFinal = start;
			mVelocity = 0;

			mStartTime = AnimationUtils.currentAnimationTimeMillis();
			mDuration = 0;

			if (start < min) {
				startSpringback(start, min, 0);
			} else if (start > max) {
				startSpringback(start, max, 0);
			}

			return !mFinished;
		}

		void startScroll(int start, int distance, int duration) {
			mFinished = false;

			mStart = start;
			mFinal = start + distance;

			mStartTime = AnimationUtils.currentAnimationTimeMillis();
			mDuration = duration;

			// Unused
			mDeceleration = 0.0f;
			mVelocity = 0;
		}

		/*
		 * Update the current position and velocity for current time. Returns
		 * true if update has been done and false if animation duration has been
		 * reached.
		 */
		boolean update() {
			final long time = AnimationUtils.currentAnimationTimeMillis();
			final long currentTime = time - mStartTime;

			if (currentTime > mDuration) {
				return false;
			}

			double distance = 0.0;
			switch (mState) {
				case SPLINE: {
					final float t = (float) currentTime / mSplineDuration;
					final int index = (int) (NB_SAMPLES * t);
					float distanceCoef = 1.f;
					float velocityCoef = 0.f;
					if (index < NB_SAMPLES) {
						final float t_inf = (float) index / NB_SAMPLES;
						final float t_sup = (float) (index + 1) / NB_SAMPLES;
						final float d_inf = SPLINE_POSITION[index];
						final float d_sup = SPLINE_POSITION[index + 1];
						velocityCoef = (d_sup - d_inf) / (t_sup - t_inf);
						distanceCoef = d_inf + (t - t_inf) * velocityCoef;
					}

					distance = distanceCoef * mSplineDistance;
					mCurrVelocity = velocityCoef * mSplineDistance / mSplineDuration * 1000.0f;
					break;
				}

				case BALLISTIC: {
					final float t = currentTime / 1000.0f;
					mCurrVelocity = mVelocity + mDeceleration * t;
					distance = mVelocity * t + mDeceleration * t * t / 2.0f;
					break;
				}

				case CUBIC: {
					final float t = (float) (currentTime) / mDuration;
					final float t2 = t * t;
					final float sign = Math.signum(mVelocity);
					distance = sign * mOver * (3.0f * t2 - 2.0f * t * t2);
					mCurrVelocity = sign * mOver * 6.0f * (-t + t2);
					break;
				}
			}

			mCurrentPosition = mStart + (int) Math.round(distance);

			return true;
		}

		void updateScroll(float q) {
			mCurrentPosition = mStart + Math.round(q * (mFinal - mStart));
		}

		/*
		 * Get a signed deceleration that will reduce the velocity.
		 */
		static private float getDeceleration(int velocity) {
			return velocity > 0 ? -GRAVITY : GRAVITY;
		}

		static void initFromContext(Context context) {
			final float ppi = context.getResources().getDisplayMetrics().density * 160.0f;
			PHYSICAL_COEF = SensorManager.GRAVITY_EARTH // g (m/s^2)
					* 39.37f // inch/meter
					* ppi
					* 0.84f; // look and feel tuning
		}
	}

}