package net.itwister.spectator.view.createsubscription;

import javax.inject.Inject;

import net.itwister.spectator.R;
import net.itwister.spectator.model.RssModel.RssItem;
import net.itwister.spectator.model.SubscriptionModel;
import net.itwister.spectator.model.helpers.SpectatorTask;
import net.itwister.spectator.view.common.base.SpectatorDialogFragment;
import net.itwister.spectator.view.helpers.Fragments;
import net.itwister.spectator.view.helpers.UiHelper;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;
import bindui.Task;
import bindui.Task.TaskCallback;
import bindui.annotations.InjectView;

public class CreateSubscriptionFromRssDialogFragment extends SpectatorDialogFragment {

	@Inject
	private SubscriptionModel model;

	@InjectView(R.id.list)
	private ListView list;

	@InjectView(R.id.root)
	private ViewAnimator animator;

	@InjectView(R.id.title)
	private EditText title;

	private Task<Void> activeTask;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);

		list.setAdapter(new Adapter((RssItem[]) getArguments().getSerializable(Fragments.PARAMETER)));

		getView().findViewById(R.id.ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!UiHelper.checkEmptyValide(R.string.error_request_value, title)) return;
				if (!UiHelper.checkSelected(R.string.error_you_must_select_a_url, list)) return;

				animator.setDisplayedChild(1);
				activeTask = model
						.create("" + title.getText(), ((RssItem) list.getItemAtPosition(list.getCheckedItemPosition())).url, true)
						.withCallback(new TaskCallback<Void>() {

							@Override
							public void onFail(Exception e) {
								Toast.makeText(getActivity(), "Can't create subscription", Toast.LENGTH_LONG).show();
								animator.setDisplayedChild(0);
							}

							@Override
							public void onSuccess(Void data) {
								dismissAllowingStateLoss();
							}
						})
						.async();
			}
		});

		if (savedInstanceState == null) list.setItemChecked(0, true);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return UiHelper.setTitle(super.onCreateDialog(savedInstanceState), R.string.create_subscription);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_create_subscription_from_rss, null);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		SpectatorTask.cancelWhenNotNull(activeTask);
	}

	public static CreateSubscriptionFromRssDialogFragment newInstance(RssItem[] rss) {
		return Fragments.instance(CreateSubscriptionFromRssDialogFragment.class, (Object) rss);
	}

	private class Adapter extends ArrayAdapter<RssItem> {

		public Adapter(RssItem[] items) {
			super(getActivity(), 0, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) convertView = View.inflate(getActivity(), android.R.layout.simple_list_item_activated_2, null);
			((TextView) convertView.findViewById(android.R.id.text1)).setText(getItem(position).title);
			((TextView) convertView.findViewById(android.R.id.text2)).setText(getItem(position).url);
			return convertView;
		}
	}
}