package net.itwister.spectator.view.common;

import javax.inject.Inject;

import net.itwister.spectator.R;
import net.itwister.spectator.model.AnalyticsModel;
import net.itwister.spectator.model.SyncModel;
import net.itwister.spectator.model.SyncModel.SyncObject;
import net.itwister.spectator.view.common.base.SpectatorFragment;
import net.itwister.spectator.view.helpers.Fragments;
import net.itwister.spectator.view.helpers.UiHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ViewAnimator;
import bindui.annotations.InjectView;

public class ProgressFragment extends SpectatorFragment {

	@InjectView(R.id.animator)
	private ViewAnimator animator;

	@Inject
	private SyncModel model;

	private SyncObject sync;

	@Inject
	private AnalyticsModel analytics;

	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (model.isSyncInProgress(sync)) {
				UiHelper.showUp(animator, isResumed());
				animator.setDisplayedChild(0);
			} else {
				if (model.isLastSyncSuccess(sync)) UiHelper.hideDown(animator, isResumed());
				else animator.setDisplayedChild(1);
			}
		}
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		sync = (SyncObject) getArguments().getSerializable(Fragments.PARAMETER);

		getView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				analytics.eventReloadErrorList(sync.target, sync.query);

				model.startSync(sync);
				receiver.onReceive(getActivity(), null);
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_progress, null);
	}

	@Override
	public void onPause() {
		super.onPause();

		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
	}

	@Override
	public void onResume() {
		super.onResume();

		model.registerObserverReceiver(receiver);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("not_first_start", true);
	}

	public static void initialize(FragmentManager manager, int targetViewId, SyncObject sync) {
		if (manager.findFragmentById(targetViewId) == null) {
			ProgressFragment f = Fragments.instance(ProgressFragment.class, sync);
			manager.beginTransaction().add(targetViewId, f).commit();
		}
	}
}