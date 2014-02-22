package net.itwister.spectator.view.common;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SpectatorViewPager extends ViewPager {

	private boolean blockTouch;

	public SpectatorViewPager(Context context) {
		super(context);
	}

	public SpectatorViewPager(Context context, AttributeSet attrs) { // NO_UCD (unused code)
		super(context, attrs);
	}

	public boolean isBlockTouch() {
		return blockTouch;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		return blockTouch ? false : super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return blockTouch ? false : super.onTouchEvent(event);
	}

	public void setBlockTouch(boolean blockTouch) {
		this.blockTouch = blockTouch;
	}
}