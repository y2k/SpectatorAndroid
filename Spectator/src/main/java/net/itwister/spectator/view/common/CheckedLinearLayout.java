package net.itwister.spectator.view.common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

public class CheckedLinearLayout extends LinearLayout implements Checkable {

	private CheckedTextView checkbox;

	public CheckedLinearLayout(Context context) {
		super(context);
	}

	public CheckedLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckedLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isChecked() {
		return checkbox != null ? checkbox.isChecked() : false;
	}

	@Override
	public void setChecked(boolean checked) {
		if (checkbox != null) {
			checkbox.setChecked(checked);
		}
	}

	@Override
	public void toggle() {
		if (checkbox != null) {
			checkbox.toggle();
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i) instanceof CheckedTextView) {
				checkbox = (CheckedTextView) getChildAt(i);
				break;
			}
		}
	}
}