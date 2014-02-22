package net.itwister.spectator.view.viewsnapshot;

import net.itwister.spectator.R;
import net.itwister.spectator.model.SyncModel.SyncObject;
import net.itwister.spectator.view.common.base.SpectatorActivity;
import net.itwister.spectator.view.viewsnapshot.ImageSnapshotFragment.ImageFragmentHost;
import net.itwister.spectator.view.viewsnapshot.SnapshotPageFragment.SnapshotPageFragmentHost;
import android.content.Intent;
import android.os.Bundle;
import bindui.annotations.ContentView;
import bindui.annotations.InjectExtra;

@ContentView(R.layout.activity_single_snapshot)
public class SnapshotViewActivity extends SpectatorActivity implements SnapshotPageFragmentHost, ImageFragmentHost {

	private static final String EXTRA_SNAPSHOT_ID = "extra_snapshot_id";

	@InjectExtra(EXTRA_SNAPSHOT_ID)
	private int snapshotId;

	@Override
	public int getCurrentPage() {
		//		return getIntent().getIntExtra(EXTRA_POSITION, 0);
		return 0;
	}

	@Override
	public boolean isFullscreenEnabled() {
		return !getActionBar().isShowing();
	}

	@Override
	public void onToggleFullscreen() {
		if (getActionBar().isShowing()) getActionBar().hide();
		else getActionBar().show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.content, SnapshotPageFragment.newInstance(new SyncObject(snapshotId)))
					.add(R.id.menu, InformationFragment.newInstance(new SyncObject(snapshotId)))
					.commit();
		}
	}

	public static Intent newIntentFill(int snapshotId) {
		return new Intent().putExtra(EXTRA_SNAPSHOT_ID, snapshotId);
	}
}