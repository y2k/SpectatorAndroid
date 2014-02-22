package net.itwister.spectator.view.helpers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.webkit.URLUtil;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class UiHelper {

	private static final int ANIM_DURATION = 500;

	public static boolean checkEmptyValide(int errorStringId, TextView... editTexts) {
		boolean valide = true;
		String error = editTexts[0].getContext().getString(errorStringId);
		for (TextView et : editTexts) {
			if (et.getError() != null) continue;

			et.setText(et.getText().toString().trim());
			if (et.length() < 1) {
				et.setError(error);
				valide = false;
			} else et.setError(null);
		}
		return valide;
	}

	public static boolean checkSelected(int messageStringId, ListView list) {
		if (list.getCheckedItemPosition() == ListView.INVALID_POSITION) {
			Toast.makeText(list.getContext(), messageStringId, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	public static boolean checkUrlValide(int errorStringId, TextView... editTexts) {
		boolean valide = true;
		String error = editTexts[0].getContext().getString(errorStringId);
		for (TextView et : editTexts) {
			if (et.getError() != null) continue;

			et.setText(et.getText().toString().trim());
			if (!URLUtil.isNetworkUrl("" + et.getText())) {
				et.setError(error);
				valide = false;
			} else et.setError(null);
		}
		return valide;
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static void collapseSearchView(SearchView sv, Menu menu, int menuItemId) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			menu.findItem(menuItemId).collapseActionView();
		} else {
			sv.setQuery(null, false);
			sv.setIconified(true);
		}
	}

	public static ProgressDialog createProgressDialog(Context context, int messageStringId) {
		ProgressDialog d = new ProgressDialog(context);
		d.setMessage(context.getString(messageStringId));
		return d;
	}

	public static TextView createTextView(Context context, int layoutId, int stringId) {
		TextView t = (TextView) View.inflate(context, layoutId, null);
		t.setText(stringId);
		return t;
	}

	public static TextView createTextView(Context context, int layoutId, int stringId, int iconId) {
		TextView t = (TextView) View.inflate(context, layoutId, null);
		t.setText(stringId);

		int size = (int) (context.getResources().getDisplayMetrics().density * 48);
		BitmapDrawable d = (BitmapDrawable) context.getResources().getDrawable(iconId);
		d.setBounds(0, 0, size, size);
		d.setGravity(Gravity.CENTER);
		t.setCompoundDrawables(d, null, null, null);
		t.setPadding(0, t.getPaddingTop(), t.getPaddingRight(), t.getPaddingBottom());

		return t;
	}

	public static int getResourceIdFromAttr(Context context, int attr) {
		TypedValue typedValue = new TypedValue();
		((Activity) context).getTheme().resolveAttribute(attr, typedValue, true);
		return typedValue.resourceId;
	}

	public static void hideDown(final View view, boolean smooth) {
		if (view.getVisibility() == View.VISIBLE) {
			if (smooth) {
				AnimationSet set = new AnimationSet(true);
				set.addAnimation(new AlphaAnimation(1, 0));
				set.addAnimation(new TranslateAnimation(0, 0, 0, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1));
				set.setDuration(ANIM_DURATION);
				set.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationEnd(Animation animation) {
						view.setVisibility(View.GONE);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {}

					@Override
					public void onAnimationStart(Animation animation) {}
				});

				view.startAnimation(set);
			} else view.setVisibility(View.GONE);
		}
	}

	public static int px(Context context, float dip) {
		return (int) (context.getResources().getDisplayMetrics().density * dip);
	}

	public static void replaceActivity(Activity current, Intent intent) {
		current.startActivity(intent);
		current.finish();
		current.overridePendingTransition(0, 0);
	}

	public static void restartActivity(Activity a) {
		a.startActivity(new Intent(a, a.getClass()));
		a.finish();
		a.overridePendingTransition(0, 0);
	}

	public static Dialog setMessage(AlertDialog d, int stringId) {
		d.setMessage(d.getContext().getString(stringId));
		return d;
	}

	public static void setOnClickListener(Fragment fragment, int viewId, OnClickListener listener) {
		fragment.getView().findViewById(viewId).setOnClickListener(listener);
	}

	public static void setTextViewBitmap(TextView t, Bitmap image) {
		if (image == null) t.setCompoundDrawables(null, null, null, null);
		else {
			BitmapDrawable d = new BitmapDrawable(t.getContext().getResources(), image);
			t.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
		}
	}

	public static void setTextViewBitmapMediumDensity(TextView t, Bitmap image) {
		if (image == null) t.setCompoundDrawables(null, null, null, null);
		else {
			image.setDensity(DisplayMetrics.DENSITY_MEDIUM);
			BitmapDrawable d = new BitmapDrawable(t.getContext().getResources(), image);
			t.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
		}
	}

	public static Dialog setTitle(Dialog d, int stringId) {
		d.setTitle(stringId);
		return d;
	}

	public static void showUp(View view, boolean smooth) {
		if (view.getVisibility() != View.VISIBLE) {
			view.setVisibility(View.VISIBLE);

			if (smooth) {
				AnimationSet set = new AnimationSet(true);
				set.addAnimation(new AlphaAnimation(0, 1));
				set.addAnimation(new TranslateAnimation(0, 0, 0, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0));
				set.setDuration(ANIM_DURATION);
				view.startAnimation(set);
			}
		}
	}

	public static void swapView(Fragment fragment, View fromView, View toView) {
		if (fromView.getVisibility() != View.GONE) {
			fromView.setVisibility(View.GONE);
			if (fragment.isResumed()) {
				fromView.startAnimation(AnimationUtils.loadAnimation(fragment.getActivity(), android.R.anim.fade_out));
				toView.startAnimation(AnimationUtils.loadAnimation(fragment.getActivity(), android.R.anim.fade_in));
			}
		}
	}
}