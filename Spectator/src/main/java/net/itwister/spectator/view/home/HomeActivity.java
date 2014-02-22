package net.itwister.spectator.view.home;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import net.itwister.spectator.BuildConfig;
import net.itwister.spectator.GCMIntentService;
import net.itwister.spectator.R;
import net.itwister.spectator.broadcast.SearchReceiver;
import net.itwister.spectator.model.AccountModel;
import net.itwister.spectator.model.AnalyticsModel;
import net.itwister.spectator.model.SubscriptionModel;
import net.itwister.spectator.model.WidgetModel;
import net.itwister.spectator.view.account.AccountActivity;
import net.itwister.spectator.view.common.Activities;
import net.itwister.spectator.view.common.base.SpectatorActivity;
import net.itwister.spectator.view.helpers.UiHelper;
import net.itwister.spectator.view.home.feed.FeedFragment;
import net.itwister.spectator.view.home.menu.MenuFragment.MenuFragmentHost;
import net.itwister.spectator.view.settings.SettingsActivity;

import javax.inject.Inject;

import bindui.annotations.ContentView;
import bindui.annotations.InjectExtra;
import bindui.annotations.InjectView;

@ContentView(R.layout.activity_home)
public class HomeActivity extends SpectatorActivity implements OnClickListener, MenuFragmentHost {

	private static final String EXTRA_INIT_TITLE = "extra_init_title";
	private static final String EXTRA_INIT_SUB_ID = "extra_init_sub_id";

	@InjectExtra(value = EXTRA_INIT_SUB_ID, optional = true)
	private int extraInitSubId;

	@InjectExtra(value = EXTRA_INIT_TITLE, optional = true)
	private String extraInitSubTitle;

	@InjectView(R.id.pager)
	private SlidingPaneLayout slider;

	@Inject
	private AccountModel accoutModel;

	@Inject
	private SubscriptionModel subsModel;

	@Inject
	private AnalyticsModel analytics;

	@Inject
	private WidgetModel widgets;

//	private ActionBarPullToRefresh pullToRefreshAttacher;
//
//	@Override
//	public PullToRefreshAttacher getPullToRefresh() {
//		return pullToRefreshAttacher;
//	}

	@Override
	public void onBackPressed() {
		if (!slider.isOpen()) slider.openPane();
		else super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.feed:
//				Intent i = FeedReceiver.intent(0, false, null);
//				LocalBroadcastManager.getInstance(this).sendBroadcastSync(i);
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (BuildConfig.DEBUG) StrictMode.enableDefaults();

		slider.setShadowResource(R.drawable.drawer_shadow);

		if (savedInstanceState == null) {
			if (accoutModel.isSignin()) {
				onSubscriptionSelected(extraInitSubId, extraInitSubTitle == null ? getString(R.string.feed) : extraInitSubTitle);

				try {
					GCMIntentService.initialize(this);
				} catch (Exception e) {
					if (BuildConfig.DEBUG) e.printStackTrace();
				}

			} else Activities.showAndClose(this, AccountActivity.class, null);
		}

		initialziePullToRefresh();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);

		final SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
		search.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				analytics.eventSearch(query);
				SearchReceiver.send(query);
				UiHelper.collapseSearchView(search, menu, R.id.search);
				return false;
			}
		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.settings:
				analytics.eventOpenSettings();
				Activities.show(this, SettingsActivity.class);
				return true;
			case R.id.singout:
				analytics.eventUserLogout();
				accoutModel.signout();
				widgets.deleteAllWidgetTask().async();
				Activities.showAndCloseAnimated(this, AccountActivity.class, null);
				return true;
			case R.id.refresh:
				analytics.eventUserRefreshHome();
				refresh();
				return true;
		}
		return false;
	}

	@Override
	public void onSubscriptionSelected(int id, String title) {
		FragmentManager fm = getSupportFragmentManager();
		String tag = "tag_sub_" + id;

		if (fm.findFragmentByTag(tag) == null) {
			fm.beginTransaction().replace(R.id.container, FeedFragment.newInstance(id, title, null), tag).commit();
		}

		slider.closePane();
		subsModel.clearUnreadCountAsync(id);
	}

	private void initialziePullToRefresh() {
//		Options o = new Options();
//		o.refreshOnUp = true;
//
//        pullToRefreshAttacher = ActionBarPullToRefresh.from(this).;
    }

	private void refresh() {
		FeedFragment f = (FeedFragment) getSupportFragmentManager().findFragmentById(R.id.container);
		if (f != null) {
			Bundle args = new Bundle();
			args.putInt(EXTRA_INIT_SUB_ID, f.getArgumentSubscriptionId());
			args.putString(EXTRA_INIT_TITLE, f.getArgumentTitle());
			Activities.showAndClose(this, HomeActivity.class, args);
		}
	}
}