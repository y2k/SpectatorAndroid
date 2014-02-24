package net.itwister.spectator.view.home.menu;

import javax.inject.Inject;

import net.itwister.spectator.App;
import net.itwister.spectator.R;
import net.itwister.spectator.model.SubscriptionModel;
import net.itwister.spectator.model.SyncModel;
import net.itwister.spectator.model.helpers.SpectatorTask;
import net.itwister.spectator.view.common.base.SpectatorDialogFragment;
import net.itwister.spectator.view.helpers.Fragments;
import net.itwister.spectator.view.helpers.UiHelper;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import bindui.extra.Task;

public class DeleteSubscriptionDialogFragment extends SpectatorDialogFragment {

    @Inject
    private SubscriptionModel model;

    @Inject
    private SyncModel syncModel;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

        model.delete(getArguments().getInt(Fragments.PARAMETER))
                .withCallback(new Task.TaskCallback<Void>() {

                    @Override
                    public void onFail(Exception exception) {
                        Toast.makeText(App.getInstance(), R.string.error_could_not_delete_subscription, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFinish() {
                        dismissAllowingStateLoss();
                    }

                    @Override
                    public void onSuccess(Void data) {
                        syncModel.startSync(syncModel.createSyncSubscriptions());
                    }
                }).async();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return UiHelper.createProgressDialog(getActivity(), R.string.delete_subscription_dialog);
	}

	public static void show(Context context, int subscriptionId) {
		Fragments.show(context, DeleteSubscriptionDialogFragment.class, subscriptionId);
	}
}