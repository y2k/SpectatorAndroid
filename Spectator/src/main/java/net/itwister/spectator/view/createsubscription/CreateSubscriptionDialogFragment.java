package net.itwister.spectator.view.createsubscription;

import javax.inject.Inject;

import net.itwister.spectator.App;
import net.itwister.spectator.R;
import net.itwister.spectator.data.Subscription;
import net.itwister.spectator.model.SubscriptionModel;
import net.itwister.spectator.model.SyncModel;
import net.itwister.spectator.view.common.base.SpectatorDialogFragment;
import net.itwister.spectator.view.helpers.Fragments;
import net.itwister.spectator.view.helpers.UiHelper;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewAnimator;

import bindui.extra.Task;
import bindui.annotations.InjectView;

public class CreateSubscriptionDialogFragment extends SpectatorDialogFragment implements OnClickListener {

	@InjectView(R.id.title)     private EditText title;
	@InjectView(R.id.source)    private EditText source;
	@InjectView(R.id.root)      private ViewAnimator root;

    @Inject	private SyncModel syncModel;
	@Inject	private SubscriptionModel model;

    private Task<Void> task;
	private Subscription subscription;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (subscription != null) {
			title.setText(subscription.title);
			source.setText(subscription.source);
			source.setEnabled(false);
			getView().findViewById(R.id.switchToRss).setVisibility(View.GONE);
		} else {
			source.setText(Fragments.getString(this, 1));
			title.setText(Fragments.getString(this, 3));
		}

		if (task != null && !task.isComplete()) root.setDisplayedChild(1);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button:
				if (UiHelper.checkEmptyValide(R.string.error_request_value, title, source)) {
//					task = subscription == null
//							? CreateSubscriptionTask.create(title.getText(), source.getText(), false, false)
//							: CreateSubscriptionTask.edit(subscription.id, title.getText());
//					task.setCallbacks(this);
//					task.async();

                    task = subscription == null
                            ? model.create("" + title.getText(), "" + source.getText(), false)
                            : model.editTask(subscription.id, "" + title.getText());
                    task.withCallback(new Task.TaskCallback<Void>() {

                        @Override
                        public void onFail(Exception exception) {
                            root.setDisplayedChild(0);
                    		Toast.makeText(App.getInstance(), R.string.error_could_not_create_a_subscription, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFinish() {
                            task = null;
                        }

                        @Override
                        public void onSuccess(Void data) {
                            dismissAllowingStateLoss();
                    		syncModel.startSync(syncModel.createSyncSubscriptions());
                        }
                    });
                    task.async();

					root.setDisplayedChild(1);
				}
				break;
			case R.id.switchToRss:
				dismiss();
				new ImportRssDialogFragment().show(getFragmentManager(), null);
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		subscription = Fragments.hasParams(this) && getArguments().size() > 0
				? model.getSubscriptionById(Fragments.getInt(this, 0)) : null;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return UiHelper.setTitle(super.onCreateDialog(savedInstanceState), subscription == null
				? R.string.dialog_create_subscription : R.string.dialog_edit_subscription);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_create_subscription, null);
		v.findViewById(R.id.button).setOnClickListener(this);
		v.findViewById(R.id.switchToRss).setOnClickListener(this);
		return v;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (task != null) task.cancel(true);
	}

//	@Override
//	public void onFail() {
//		task = null;
//		root.setDisplayedChild(0);
//	}

//	@Override
//	public void onPause() {
//		super.onPause();
//		if (task != null) task.setCallbacks(null);
//	}

//	@Override
//	public void onResume() {
//		super.onResume();
//
//		if (task != null) {
//			task.setCallbacks(this);
//			if (task.isComplete()) {
//				if (task.getResult() != null) onSuccess();
//				else onFail();
//			}
//		}
//	}

//	@Override
//	public void onSuccess() {
//		task = null;
//		dismissAllowingStateLoss();
//	}

	public static void show(Context context, int subscriptionId) {
		Fragments.show(context, CreateSubscriptionDialogFragment.class, subscriptionId);
	}

	public static void show(Context context, String defaultUrl, String defaultTitle, boolean isRss) {
		Fragments.show(context, CreateSubscriptionDialogFragment.class, 0, defaultUrl, isRss, defaultTitle);
	}
}