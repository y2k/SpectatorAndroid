package net.itwister.spectator.view.home;

import javax.inject.Inject;

import net.itwister.spectator.App;
import net.itwister.spectator.BuildConfig;
import net.itwister.spectator.R;
import net.itwister.spectator.model.AccountModel;
import net.itwister.spectator.model.helpers.SpectatorTask;
import net.itwister.spectator.view.common.base.SpectatorFragment;
import net.itwister.spectator.view.helpers.AccountManagerHelper;
import net.itwister.spectator.view.helpers.UiHelper;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewAnimator;
import bindui.Task;
import bindui.Task.TaskCallback;
import bindui.annotations.InjectView;

// https://www.googleapis.com/oauth2/v1/userinfo?access_token=???
public class LoginFragment extends SpectatorFragment {

	private static final int ACCOUNT_REQUEST_CODE = 1002;

	@InjectView(R.id.animator)
	private ViewAnimator animator;

	private Task<Void> task;
	private boolean success;

	// TODO
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		UiHelper.setOnClickListener(this, R.id.ok, new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = AccountManager.newChooseAccountIntent(null, null, new String[] { "com.google" }, false, null, null, null, null);
				startActivityForResult(i, ACCOUNT_REQUEST_CODE);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACCOUNT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

			final AccountManager am = AccountManager.get(getActivity());
			String name = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

			final Account a = AccountManagerHelper.findGoogleAccount(getActivity(), name);
			task = new SpectatorTask<Void>() {

				@Inject
				private AccountModel model;

				@Override
				public void onExecute() throws Exception {
					AccountManagerFuture<Bundle> tokenFuture = am.getAuthToken(a, "oauth2:https://www.googleapis.com/auth/userinfo.email", null, getActivity(), null, null);
					String token = tokenFuture.getResult().getString(AccountManager.KEY_AUTHTOKEN);
					model.signin(token);
				}
			}.withCallback(new TaskCallback<Void>() {

				@Override
				public void onFail(Exception e) {
					if (BuildConfig.DEBUG) e.printStackTrace();
					Toast.makeText(App.getInstance(), R.string.error_failed_to_log_in_to_your_account, Toast.LENGTH_LONG).show();
				}

				@Override
				public void onFinish() {
					task = null;
					invalidateUi();
				}

				@Override
				public void onSuccess(Void data) {
					success = true;
				}
			});
			task.async();
			invalidateUi();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_login, null);
	}

	@Override
	public void onResume() {
		super.onResume();
		invalidateUi();
	}

	private void invalidateUi() {
		if (isResumed()) {
			if (success) ((LoginFragmentHost) getActivity()).onLoginSuccess();
			else animator.setDisplayedChild(task == null ? 0 : 1);
		}
	}

	public interface LoginFragmentHost {

		void onLoginSuccess();
	}
}