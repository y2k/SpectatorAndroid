package net.itwister.spectator.view.home.menu;

import javax.inject.Inject;

import net.itwister.spectator.R;
import net.itwister.spectator.data.Subscription;
import net.itwister.spectator.loaders.JTaskCursorLoaderCallbacks;
import net.itwister.spectator.model.SubscriptionModel;
import net.itwister.spectator.model.SyncModel;
import net.itwister.spectator.view.common.LoaderBroadcastReceiver;
import net.itwister.spectator.view.common.base.SpectatorFragment;
import net.itwister.spectator.view.createsubscription.CreateSubscriptionDialogFragment;
import net.itwister.spectator.view.createsubscription.ImportRssDialogFragment;
import net.itwister.spectator.view.helpers.Fragments;
import net.itwister.spectator.view.stash.StashActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewAnimator;
import bindui.annotations.InjectView;

public class MenuFragment extends SpectatorFragment {

	private SubscriptionsAdapter adapter;

    @Inject
    private SubscriptionModel subModel;

    @InjectView(R.id.list)
	private ListView list;

	@InjectView(R.id.stub)
	private ViewAnimator stub;

	private BroadcastReceiver receiver;

	@Inject
	private SyncModel model;

	private boolean argOnlySubscriptions;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		argOnlySubscriptions = Fragments.getBoolean(this, 0);

		if (savedInstanceState == null) model.startSync(model.createSyncSubscriptions());

		getListView().setEmptyView(stub);

		if (!argOnlySubscriptions) {
			getListView().addHeaderView(createTextView(R.string.menu_feed, R.drawable.ic_feed_button, R.string.menu_settings_group));
			getListView().addHeaderView(createTextView(R.string.menu_favorite, R.drawable.ic_stash_button, 0));
			getListView().addHeaderView(createTextView(R.string.menu_add_subscription, R.drawable.ic_web_button, 0));
			getListView().addHeaderView(createTextView(R.string.menu_add_rss, R.drawable.ic_rss_button, 0));
		}
		getListView().setAdapter(adapter = new SubscriptionsAdapter(getActivity()));

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position >= getListView().getHeaderViewsCount()) {
					Cursor c = (Cursor) list.getItemAtPosition(position);
					((MenuFragmentHost) getActivity()).onSubscriptionSelected((int) id, c.getString(c.getColumnIndex(Subscription.TITLE)));
				} else if (position == 0) {
					((MenuFragmentHost) getActivity()).onSubscriptionSelected(0, getString(R.string.feed));
				} else if (position == 1) {
					startActivity(new Intent(getActivity(), StashActivity.class));
				} else if (position == 2) {
					new CreateSubscriptionDialogFragment().show(getFragmentManager(), null);
				} else if (position == 3) {
					new ImportRssDialogFragment().show(getFragmentManager(), null);
				}
			}
		});
		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (position >= getListView().getHeaderViewsCount()) {
					getActivity().startActionMode(new SubscriptionAcionCallback(getActivity(), (int) id));
					return true;
				}
				return false;
			}
		});

		setListShown(false);
		initializeLoader();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_subscriptions, null);
	}

	@Override
	public void onPause() {
		super.onPause();
		LoaderBroadcastReceiver.unregister(receiver);
	}

	@Override
	public void onResume() {
		super.onResume();
		receiver = LoaderBroadcastReceiver.register(receiver, getLoaderManager());
	}

//	@Override
//	public void onStart() {
//		super.onStart();
//		adapter.start();
//	}
//
//	@Override
//	public void onStop() {
//		super.onStop();
//		adapter.stop();
//	}

	private View createTextView(int stringId, int iconId, int groupStringId) {
		Context context = getActivity();
		View v = View.inflate(context, R.layout.item_subscription2, null);

		TextView t = (TextView) v.findViewById(R.id.title);
		t.setText(stringId);

		ImageView iv = (ImageView) v.findViewById(R.id.icon);
		iv.setImageResource(iconId);

		v.findViewById(R.id.group).setVisibility(groupStringId > 0 ? View.VISIBLE : View.GONE);
		if (groupStringId > 0) ((TextView) v.findViewById(R.id.groupTitle)).setText(groupStringId);

		return v;
	}

	private ListView getListView() {
		return list;
	}

	private void initializeLoader() {
        new JTaskCursorLoaderCallbacks(subModel.getSubscriptionsFromDatabaseTask(), adapter){

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                super.onLoadFinished(loader, data);
                setListShown(true);
            }
        }.initialize(this, 0);
	}

	private void setListShown(boolean shown) {
		stub.setDisplayedChild(shown ? 1 : 0);
	}

	/**
	 * Создать новый экземпляр {@link MenuFragment} с параметрами.
	 * @param onlySubscriptions Показывать только подписки без кнопок
	 *        управления (используется для создания виджета).
	 */
	public static MenuFragment newInstance(boolean onlySubscriptions) {
		return Fragments.instance(MenuFragment.class, onlySubscriptions);
	}

	public interface MenuFragmentHost {

		void onSubscriptionSelected(int id, String title);
	}
}