package net.itwister.spectator.view.screenshots;

import net.itwister.spectator.R;
import net.itwister.spectator.model.SyncModel.SyncObject;
import net.itwister.spectator.view.common.Activities;
import net.itwister.spectator.view.common.SpectatorViewPager;
import net.itwister.spectator.view.common.base.SpectatorActivity;
import net.itwister.spectator.view.screenshots.ScreenshotItemFragment.OnScreenshotClickListner;
import net.itwister.tools.widgets.ZoomImageView;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import bindui.annotations.InjectExtra;

public class ScreenshotsActivity extends SpectatorActivity implements OnScreenshotClickListner {

	@InjectExtra(Activities.PARAMETER)
	SyncObject extraSyncObject;

	@InjectExtra(Activities.PARAMETER2)
	int extraInitPos;

	@InjectExtra(Activities.PARAMETER3)
	int extraCount;

	SpectatorViewPager pager;

	@Override
	public void onScreenshotClicked(ZoomImageView image) {
		if (getActionBar().isShowing()) {
			getActionBar().hide();
			image.setIgnoreUserTouch(false);
			pager.setBlockTouch(true);

			pager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
			ObjectAnimator.ofObject(pager, "backgroundColor", new ArgbEvaluator(), Color.TRANSPARENT, Color.BLACK).start();
		} else {
			getActionBar().show();
			image.setIgnoreUserTouch(true);
			image.setZoomToFullscreen();
			pager.setBlockTouch(false);

			pager.setSystemUiVisibility(0);
			ObjectAnimator a = ObjectAnimator.ofObject(pager, "backgroundColor", new ArgbEvaluator(), Color.BLACK, Color.TRANSPARENT);
			a.addListener(new AnimatorListenerAdapter() {

				@Override
				public void onAnimationEnd(Animator animation) {
					pager.setBackgroundDrawable(null);
				}
			});
			a.start();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(pager = new SpectatorViewPager(this));
		pager.setId(R.id.pager);

		pager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return extraCount;
			}

			@Override
			public Fragment getItem(int position) {
				return ScreenshotItemFragment.newInstance(extraSyncObject, position);
			}
		});

		if (savedInstanceState == null) pager.setCurrentItem(extraInitPos);
	}

	@Deprecated
	public static Intent newIntent(Context context, int snapshotId, int initPosition, int count) {
		return newIntent(context, new SyncObject(snapshotId), initPosition, count);
	}

	public static Intent newIntent(Context context, SyncObject syncObject, int initPosition, int count) {
		return Activities.newIntent(context, ScreenshotsActivity.class, syncObject, initPosition, count);
	}
}