package net.itwister.spectator.view.stash;

import net.itwister.spectator.R;
import net.itwister.spectator.model.SnapshotModel;
import net.itwister.spectator.view.common.base.SpectatorActivity;
import net.itwister.spectator.view.home.feed.FeedFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class StashActivity extends SpectatorActivity  {

//	private PullToRefreshAttacher pullToRefreshAttacher;
//
//	@Override
//	public PullToRefreshAttacher getPullToRefresh() {
//		return pullToRefreshAttacher;
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			Fragment f = FeedFragment.newInstance(SnapshotModel.STASH_SUBSCRIPTION_ID, getString(R.string.title_stash), null);
			getSupportFragmentManager().beginTransaction().replace(android.R.id.content, f).commit();
		}

//		initialziePullToRefresh();
	}

//	private void initialziePullToRefresh() {
//		Options o = new Options();
//		o.refreshOnUp = true;
//		pullToRefreshAttacher = PullToRefreshAttacher.get(this, o);
//	}
}