package net.itwister.spectator.view.viewsnapshot;

import net.itwister.spectator.R;
import net.itwister.spectator.data.Snapshot;
import net.itwister.spectator.data.generic.SnapshotWrapper;
import net.itwister.spectator.loaders.JTaskLoader;
import net.itwister.spectator.loaders.JTaskLoaderCallbacks;
import net.itwister.spectator.model.SnapshotModel;
import net.itwister.spectator.model.SyncModel.SyncObject;
import net.itwister.spectator.view.common.base.SpectatorFragment;
import net.itwister.spectator.view.helpers.BroadcastHelper;
import net.itwister.spectator.view.helpers.Fragments;
import net.itwister.tools.inner.Ln;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.ShareCompat;
import android.support.v4.app.ShareCompat.IntentBuilder;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import bindui.annotations.InjectView;

public class SnapshotPageFragment extends SpectatorFragment {

	private static final String STATE_SELECTED = "state_position";

	private Snapshot snapshot;
	private BroadcastReceiver receiver;
	//	private int argPostion;

	private int selected;

	@InjectView(R.id.progress)
	private ProgressBar progress;

	@Inject
	private SnapshotModel model;

	private SyncObject argSyncObject;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		argSyncObject = (SyncObject) getArguments().getSerializable(Fragments.PARAMETER);

		selected = savedInstanceState == null ? 0 : savedInstanceState.getInt(STATE_SELECTED);

		BroadcastHelper.regiser("page_changed", receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				invalideUi();
			}
		});

		initializeLoader();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.page, menu);
		ShareCompat.configureMenuItem(menu, R.id.share, IntentBuilder
				.from(getActivity()).setType("text/plain")
				.setText(snapshot == null ? "" : snapshot.source));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_snapshot_host, null);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		BroadcastHelper.unregister(receiver);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED, selected);
	}

	private void changeContainer(int type) {
		if (!isResumed()) return;
		String tag = "type" + type;
		Fragment f = getChildFragmentManager().findFragmentByTag(tag);
		if (f != null || snapshot == null) return;

		//		if (type == 0) f = ImageSnapshotFragment.newInstance(snapshot.thumbnail, argPostion);
		if (type == 0) f = ImageSnapshotFragment.newInstance(snapshot.thumbnail, argSyncObject.position);
		else if (type == 1) f = WebSnapshotFragment.newInstance(snapshot.id, WebSnapshotFragment.TYPE_WEB, snapshot.updated);
		else if (type == 2) f = WebSnapshotFragment.newInstance(snapshot.id, WebSnapshotFragment.TYPE_DIFF, snapshot.updated);

		if (f != null) {
			getChildFragmentManager().beginTransaction().replace(R.id.container, f, tag).commit();
			getChildFragmentManager().executePendingTransactions();
		}
	}

	private void initializeLoader() {
        new JTaskLoaderCallbacks<SnapshotWrapper>(model.getSnapshotTask(argSyncObject),null){

            @Override
            public void onLoadFinished(Loader<SnapshotWrapper> loader, SnapshotWrapper data) {
                if (data != null) {
                    snapshot = data.snapshot;
                    getActivity().invalidateOptionsMenu();
                    post(new Runnable() {

                        @Override
                        public void run() {
                            invalideUi();
                        }
                    });
                }
            }
        }.initialize(this,0);

//		getLoaderManager().initLoader(0, null, new LoaderCallbacks<SnapshotWrapper>() {
//
//            @Override
//            public Loader<SnapshotWrapper> onCreateLoader(int id, Bundle args) {
//                return new SpectatorTaskLoader<SnapshotWrapper>(model.getSnapshotTask(argSyncObject));
//            }
//
//            @Override
//            public void onLoaderReset(Loader<SnapshotWrapper> loader) {
//            }
//
//            @Override
//            public void onLoadFinished(Loader<SnapshotWrapper> loader, SnapshotWrapper data) {
//                if (data == null) return;
//
//                snapshot = data.snapshot;
//                getActivity().invalidateOptionsMenu();
//
//                post(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        invalideUi();
//                    }
//                });
//            }
//        });
	}

	private void invalideUi() {
		if (!isResumed()) return;
		//		boolean current = argPostion == ((SnapshotPageFragmentHost) getActivity()).getCurrentPage();
		boolean current = argSyncObject.position == ((SnapshotPageFragmentHost) getActivity()).getCurrentPage();

		if (snapshot == null) {
			if (current) getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		} else {
			final ArrayAdapter<NavigationItem> list = new ArrayAdapter<NavigationItem>(getActivity(), android.R.layout.simple_dropdown_item_1line);
			if (snapshot.thumbnail > 0 && snapshot.hasScreenshots) list.add(new NavigationItem(getString(R.string.type_poster), 0));
			if (snapshot.hasContent) list.add(new NavigationItem(getString(R.string.type_web_preview), 1));
			if (snapshot.hasRevisions) list.add(new NavigationItem(getString(R.string.type_difference), 2));

			if (current) {
				getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
				getActivity().getActionBar().setListNavigationCallbacks(list, new OnNavigationListener() {

					@Override
					public boolean onNavigationItemSelected(int itemPosition, long itemId) {
						selected = itemPosition;
						changeContainer(list.getItem(itemPosition).type);
						return true;
					}
				});

				getActivity().getActionBar().setSelectedNavigationItem(selected);
			}

			if (selected < list.getCount()) changeContainer(list.getItem(selected).type);
			else setProgress(false, false, 0);
		}
	}

	void setProgress(boolean visible, boolean indeterminate, float progress) {
		this.progress.setVisibility(visible ? View.VISIBLE : View.GONE);
		this.progress.setIndeterminate(indeterminate);
		this.progress.setProgress((int) (100 * progress));
	}

	@Deprecated
	public static SnapshotPageFragment newInstance(int subscriptionId, String query, int position) {
		return newInstance(new SyncObject(new SyncObject(subscriptionId, query), position));
	}

	public static SnapshotPageFragment newInstance(SyncObject syncObject) {
		return Fragments.instance(SnapshotPageFragment.class, syncObject);
	}

	public interface SnapshotPageFragmentHost {

		int getCurrentPage();
	}

	static class NavigationItem {

		String title;
		int type;

		NavigationItem(String title, int type) {
			this.title = title;
			this.type = type;
		}

		@Override
		public String toString() {
			return title;
		}
	}
}