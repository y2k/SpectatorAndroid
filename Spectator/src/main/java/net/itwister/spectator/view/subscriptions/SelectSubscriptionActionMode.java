//package net.itwister.spectator.view.subscriptions;
//
//import net.itwister.spectator.R;
//import android.support.v4.app.FragmentActivity;
//import android.util.SparseBooleanArray;
//import android.view.ActionMode;
//import android.view.ActionMode.Callback;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ListView;
//
//public class SelectSubscriptionActionMode implements Callback {
//
//	private final ListView listView;
//	private final int startPosition;
//	private OnItemClickListener backupListener;
//	private final FragmentActivity actiity;
//
//	public SelectSubscriptionActionMode(FragmentActivity actiity, ListView listView, int startPosition) {
//		this.actiity = actiity;
//		this.listView = listView;
//		// TODO Auto-generated constructor stub
//		this.startPosition = startPosition;
//	}
//
//	@Override
//	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//		//		switch (item.getItemId()) {
//		//			case R.id.createTag:
//		//				//				CreateTagFragment
//		//				//						.newInstance(getListView().getCheckedItemIds(), 0, null)
//		//				//						.show(getFragmentManager(), null);
//		//				CreateTagDialogFragment
//		//						.newInstance(listView.getCheckedItemIds())
//		//						.show(actiity.getSupportFragmentManager(), null);
//		//				return true;
//		//		}
//
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean onCreateActionMode(final ActionMode mode, Menu menu) {
//		mode.getMenuInflater().inflate(R.menu.ab_select_subscriptions, menu);
//		mode.setTitle("Create tag (1)");
//
//		backupListener = listView.getOnItemClickListener();
//
//		//		listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
//		listView.setItemChecked(startPosition, true);
//		listView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				// TODO Auto-generated method stub
//				//				int count = listView.getCheckedItemPositions().size();
//				int count = 0;
//				SparseBooleanArray checked = listView.getCheckedItemPositions();
//				for (int i = 0; i < checked.size(); i++) {
//					if (checked.valueAt(i)) count++;
//				}
//
//				if (count > 0) mode.setTitle("Create tag (" + count + ")");
//				else mode.finish();
//			}
//		});
//
//		//		listView.setOnItemSelectedListener(new OnItemSelectedListener() {
//		//
//		//			@Override
//		//			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//		//				// TODO Auto-generated method stub
//		//				mode.setTitle("" + listView.getCheckedItemCount());
//		//			}
//		//
//		//			@Override
//		//			public void onNothingSelected(AdapterView<?> parent) {
//		//				// TODO Auto-generated method stub
//		//
//		//			}
//		//		});
//
//		// TODO Auto-generated method stub
//		return true;
//	}
//
//	@Override
//	public void onDestroyActionMode(ActionMode mode) {
//		// TODO Auto-generated method stub
//
//		//		listView.getCheckedItemPositions().clear();
//		//		listView.invalidate();
//
//		SparseBooleanArray checked = listView.getCheckedItemPositions();
//		for (int i = checked.size() - 1; i >= 0; i--) {
//			if (checked.valueAt(i)) listView.setItemChecked(checked.keyAt(i), false);
//		}
//
//		//		listView.getCheckedItemPositions().clear();
//		//		listView.invalidate();
//
//		//		listView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
//		//		((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
//
//		listView.setOnItemClickListener(backupListener);
//	}
//
//	@Override
//	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//}