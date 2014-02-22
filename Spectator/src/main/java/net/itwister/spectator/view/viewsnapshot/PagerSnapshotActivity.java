package net.itwister.spectator.view.viewsnapshot;

import javax.inject.Inject;

import net.itwister.spectator.R;
import net.itwister.spectator.loaders.JTaskLoaderCallbacks;
import net.itwister.spectator.model.ImageModel;
import net.itwister.spectator.model.SnapshotModel;
import net.itwister.spectator.model.SyncModel;
import net.itwister.spectator.model.SyncModel.SyncObject;
import net.itwister.spectator.model.database.ObserverManager;
import net.itwister.spectator.view.common.ProgressFragment;
import net.itwister.spectator.view.common.SpectatorViewPager;
import net.itwister.spectator.view.common.base.SpectatorActivity;
import net.itwister.spectator.view.helpers.BroadcastHelper;
import net.itwister.spectator.view.viewsnapshot.ImageSnapshotFragment.ImageFragmentHost;
import net.itwister.spectator.view.viewsnapshot.SnapshotPageFragment.SnapshotPageFragmentHost;
import net.itwister.tools.inner.Ln;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.SimpleDrawerListener;
import android.view.View;
import bindui.annotations.ContentView;
import bindui.annotations.InjectExtra;
import bindui.annotations.InjectView;

@ContentView(R.layout.activity_pager_snapshot)
public class PagerSnapshotActivity extends SpectatorActivity implements ImageFragmentHost, SnapshotPageFragmentHost {

	private static final String STATE_COUNT = "state_count";
	private static final String INIT_COUNT = "init_count";
	private static final String INIT_POSITION = "init_position";
	private static final String EXTRA_SYNC_OBJECT = "extra_sync_object";

	@InjectExtra(INIT_POSITION)	    private int extraInitPosition;
	@InjectExtra(INIT_COUNT)	    private int extraInitCount;
	@InjectExtra(EXTRA_SYNC_OBJECT)	private SyncObject extraSync;

	@InjectView(R.id.container)	private SpectatorViewPager pager;
	@InjectView(R.id.drawer)	private DrawerLayout drawer;

	@Inject	private ImageModel imageModel;
	@Inject private SyncModel syncModel;
    @Inject private SnapshotModel snapModel;

    private int currentCount;
	private int lastLoadCount;

	@Override
	public int getCurrentPage() {
		return pager.getCurrentItem();
	}

	@Override
	public boolean isFullscreenEnabled() {
		return !getActionBar().isShowing();
	}

	@Override
	public void onToggleFullscreen() {
		final View root = findViewById(android.R.id.content);

		if (getActionBar().isShowing()) {
			getActionBar().hide();

			root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
			ObjectAnimator.ofObject(root, "backgroundColor", new ArgbEvaluator(), Color.TRANSPARENT, Color.BLACK).start();
		} else {
			getActionBar().show();

			root.setSystemUiVisibility(0);
			ObjectAnimator a = ObjectAnimator.ofObject(root, "backgroundColor", new ArgbEvaluator(), Color.BLACK, Color.TRANSPARENT);
			a.addListener(new AnimatorListenerAdapter() {

				@Override
				public void onAnimationEnd(Animator animation) {
					root.setBackgroundDrawable(null);
				}
			});
			a.start();
		}
		pager.setBlockTouch(!getActionBar().isShowing());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			currentCount = extraInitCount;
		} else {
			currentCount = savedInstanceState.getInt(STATE_COUNT);
		}

		ProgressFragment.initialize(getSupportFragmentManager(), R.id.progress, extraSync);

		pager.setOnPageChangeListener(new SimpleOnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				BroadcastHelper.send("page_changed", position, extraInitCount);
				if (position == pager.getAdapter().getCount() - 1) syncNextPage();

				imageModel.globalCancelAllExcept(new String[] { "" + (position - 1), "" + position, "" + (position + 1), "t" + (position - 1), "t" + position, "t" + (position + 1) });
			}
		});

		pager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return currentCount;
			}

			@Override
			public Fragment getItem(int position) {
				return SnapshotPageFragment.newInstance(extraSync.subId, extraSync.query, position);
			}
		});

		if (savedInstanceState == null) {
			pager.setCurrentItem(extraInitPosition);
		}

		initializeLoader();
		initializeDrawer();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (isFinishing()) imageModel.globalCancelAllExcept(new String[0]);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(EXTRA_SYNC_OBJECT, currentCount);
	}

	private void initializeDrawer() {
		drawer.setDrawerListener(new SimpleDrawerListener() {

			@Override
			public void onDrawerClosed(View drawer) {
				Ln.i("close, %s", drawer);
				FragmentManager fm = getSupportFragmentManager();
				Fragment f = fm.findFragmentById(R.id.menu);
				if (f != null) fm.beginTransaction().remove(f).commit();
			}

			@Override
			public void onDrawerOpened(View drawer) {
				Ln.i("open, %s", drawer);
				InformationFragment f = InformationFragment.newInstance(new SyncObject(extraSync, pager.getCurrentItem()));
				FragmentManager fm = getSupportFragmentManager();
				fm.beginTransaction().replace(R.id.menu, f).commit();
			}
		});
	}

	private void initializeLoader() {
        new JTaskLoaderCallbacks<Integer>(snapModel.getCountTask(extraSync), null) {

            @Override
            public void onLoadFinished(Loader<Integer> loader, Integer data) {
                currentCount = data;
                pager.getAdapter().notifyDataSetChanged();
            }
        }.initialize(this, 0);
	}

	private void syncNextPage() {
		if (lastLoadCount >= currentCount) return;
		lastLoadCount = currentCount;
		extraSync.reset = false;
		syncModel.startSync(extraSync);
	}

	public static Intent newIntent(Context context, SyncObject sync, int initPosition, int count) {
		return new Intent(context, PagerSnapshotActivity.class)
				.putExtra(EXTRA_SYNC_OBJECT, sync)
				.putExtra(INIT_POSITION, initPosition)
				.putExtra(INIT_COUNT, count);
	}
}