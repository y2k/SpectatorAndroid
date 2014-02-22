package net.itwister.spectator.view.snapshots;

import javax.inject.Inject;

import net.itwister.spectator.R;
import net.itwister.spectator.model.AnalyticsModel;
import net.itwister.spectator.view.common.base.SpectatorActivity;
import net.itwister.spectator.view.helpers.UiHelper;
import net.itwister.spectator.view.home.feed.FeedFragment;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import bindui.annotations.InjectExtra;

public class SnapshotListActivity extends SpectatorActivity {

	private static final String EXTRA_TITLE = "extra_title";
	private static final String EXTRA_QUERY = "extra_query";
	private static final String EXTRA_LIST_ID = "extra_list_id";

	@InjectExtra(EXTRA_LIST_ID)
	private int listId;

	@InjectExtra(value = EXTRA_QUERY, optional = true)
	private String query;

	@InjectExtra(value = EXTRA_TITLE, optional = true)
	private String title;

	@Inject
	private AnalyticsModel analytics;

//	private PullToRefreshAttacher pullToRefreshAttacher;
//
//	@Override
//	public PullToRefreshAttacher getPullToRefresh() {
//		return pullToRefreshAttacher;
//	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		pullToRefreshAttacher = PullToRefreshAttacher.get(this);

		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace(android.R.id.content, FeedFragment.newInstance(listId, title, query))
					.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SearchView search = new SearchView(this);
		search.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				analytics.eventSearch(query);
				UiHelper.replaceActivity(SnapshotListActivity.this, SnapshotListActivity.createIntent(SnapshotListActivity.this, listId, query, query));
				return false;
			}
		});

		MenuItem m = menu.add("Search");
		m.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		m.setIcon(R.drawable.ic_action_search);
		m.setActionView(search);

		return true;
	}

	public static Intent createIntent(Context context, int listId, String query, String title) {
		Intent i = new Intent(context, SnapshotListActivity.class).putExtra(EXTRA_LIST_ID, listId);
		if (!TextUtils.isEmpty(query)) i.putExtra(EXTRA_QUERY, query);
		if (!TextUtils.isEmpty(title)) i.putExtra(EXTRA_TITLE, title);
		return i;
	}
}