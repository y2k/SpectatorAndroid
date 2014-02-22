//package net.itwister.spectator.view.subscriptions;
//
//import net.itwister.spectator.R;
//import net.itwister.spectator.loaders.SpectatorCursorLoader;
//import net.itwister.spectator.model.SubscriptionModel;
//import net.itwister.spectator.services.SyncService;
//import net.itwister.spectator.services.SyncService.SyncTarget;
//import net.itwister.spectator.view.base.SpectatorFragment;
//import net.itwister.spectator.view.common.LoaderBroadcastReceiver;
//import net.itwister.spectator.view.common.ProgressFragment;
//import net.itwister.spectator.view.createsubscription.CreateSubscriptionActivity;
//import net.itwister.spectator.view.home.menu.SubscriptionsAdapter;
//import roboguice.inject.InjectView;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.support.v4.app.LoaderManager.LoaderCallbacks;
//import android.support.v4.content.Loader;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AbsListView;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ListView;
//import android.widget.ViewAnimator;
//
//import com.google.inject.Inject;
//
//public class SubscriptionListFragment extends SpectatorFragment implements LoaderCallbacks<Cursor> {
//
//	private static final String STATE_SELECTED = "state_selected";
//	private static final String ARG_TAG_NAME = "arg_tag_name";
//	private static final String ARG_TAG_ID = "arg_tag_id";
//
//	@Inject
//	private SubscriptionsAdapter adapter;
//
//	private boolean stateSelected;
//
//	@InjectView(R.id.list)
//	private ListView list;
//
//	@InjectView(R.id.stub)
//	private ViewAnimator stub;
//
//	private BroadcastReceiver receiver;
//
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//		setHasOptionsMenu(true);
//
//		list.setEmptyView(stub);
//		list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
//
//		ProgressFragment.initialize(getChildFragmentManager(), R.id.progress, SyncTarget.Subscriptions, false);
//
//		if (savedInstanceState != null) {
//			stateSelected = savedInstanceState.getBoolean(STATE_SELECTED);
//		}
//
//		list.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				getActivity().startActionMode(new SelectSubscriptionActionMode(getActivity(), list, position));
//			}
//		});
//
//		list.setAdapter(adapter);
//		setListShown(false);
//		getLoaderManager().initLoader(0, null, this);
//	};
//
//	@Override
//	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//		return new SpectatorCursorLoader() {
//
//			@Inject
//			private SubscriptionModel model;
//
//			@Override
//			protected Cursor getCursorInBackground(Context context) throws Exception {
//				return model.getSubscriptionsFromDatabase();
//			}
//		};
//	}
//
//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		inflater.inflate(getArguments().getInt(ARG_TAG_ID) == 0
//				? R.menu.subscriptions
//				: R.menu.edit_tag, menu);
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		return inflater.inflate(R.layout.fragment_subscriptions, null);
//	}
//
//	@Override
//	public void onLoaderReset(Loader<Cursor> loader) {
//		adapter.swapCursor(null);
//	}
//
//	@Override
//	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//		setListShown(true);
//		adapter.swapCursor(data);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//			case R.id.create:
//				startActivity(new Intent(getActivity(), CreateSubscriptionActivity.class));
//				return true;
//			case R.id.refresh:
//				SyncService.sync(SyncTarget.Subscriptions);
//				return true;
//		}
//		return false;
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		LoaderBroadcastReceiver.unregister(receiver);
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		receiver = LoaderBroadcastReceiver.register(receiver, getLoaderManager(), SyncService.ACTION_SYNC);
//	}
//
//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		outState.putBoolean(STATE_SELECTED, stateSelected);
//	}
//
//	private void setListShown(boolean shown) {
//		stub.setDisplayedChild(shown ? 1 : 0);
//	}
//
//	public static SubscriptionListFragment newInstance(int tagId, String tagName) {
//		Bundle args = new Bundle();
//		args.putInt(ARG_TAG_ID, tagId);
//		args.putString(ARG_TAG_NAME, tagName);
//		SubscriptionListFragment f = new SubscriptionListFragment();
//		f.setArguments(args);
//		return f;
//	}
//}