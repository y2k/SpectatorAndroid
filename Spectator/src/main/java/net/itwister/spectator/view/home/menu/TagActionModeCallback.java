//package net.itwister.spectator.view.home.menu;
//
//import net.itwister.spectator.R;
//import net.itwister.spectator.view.subscriptions.SubscriptionListActivity;
//import android.support.v4.app.FragmentActivity;
//import android.view.ActionMode;
//import android.view.ActionMode.Callback;
//import android.view.Menu;
//import android.view.MenuItem;
//
//public class TagActionModeCallback implements Callback {
//
//	private final String tagName;
//	private final int tagId;
//	private final FragmentActivity context;
//
//	public TagActionModeCallback(FragmentActivity context, String tagName, int tagId) {
//		this.context = context;
//		this.tagName = tagName;
//		this.tagId = tagId;
//	}
//
//	@Override
//	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//		switch (item.getItemId()) {
//			case R.id.delete:
//				DeleteTagDialogFragment.newInstance(tagId).show(context.getSupportFragmentManager(), null);
//				break;
//			case R.id.edit:
//				context.startActivity(SubscriptionListActivity.newIntent(context, tagId, tagName));
//				break;
//		}
//		mode.finish();
//		return true;
//	}
//
//	@Override
//	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//		mode.setTitle(tagName);
//		mode.getMenuInflater().inflate(R.menu.ab_tag, menu);
//		return true;
//	}
//
//	@Override
//	public void onDestroyActionMode(ActionMode mode) {}
//
//	@Override
//	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//		return false;
//	}
//}