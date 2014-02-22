package net.itwister.spectator.view.account;

import net.itwister.spectator.view.common.Activities;
import net.itwister.spectator.view.common.base.SpectatorActivity;
import net.itwister.spectator.view.home.HomeActivity;
import net.itwister.spectator.view.home.LoginFragment;
import net.itwister.spectator.view.home.LoginFragment.LoginFragmentHost;
import android.os.Bundle;

public class AccountActivity extends SpectatorActivity implements LoginFragmentHost {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(android.R.id.content, new LoginFragment()).commit();
		}
	}

	@Override
	public void onLoginSuccess() {
		Activities.showAndClose(this, HomeActivity.class, null);
	}
}