package net.itwister.spectator.view.home.menu;

import net.itwister.spectator.R;
import net.itwister.spectator.view.createsubscription.CreateSubscriptionDialogFragment;
import android.content.Context;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;

public class SubscriptionAcionCallback implements Callback {

	private final int subscriptionId;
	private final Context context;

	public SubscriptionAcionCallback(Context context, int subscriptionId) {
		this.context = context;
		this.subscriptionId = subscriptionId;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.edit:
				CreateSubscriptionDialogFragment.show(context, subscriptionId);
				break;
			case R.id.delete:
				DeleteSubscriptionDialogFragment.show(context, subscriptionId);
				break;
		}
		mode.finish();
		return true;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		mode.getMenuInflater().inflate(R.menu.subscription_actionmode, menu);
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}
}