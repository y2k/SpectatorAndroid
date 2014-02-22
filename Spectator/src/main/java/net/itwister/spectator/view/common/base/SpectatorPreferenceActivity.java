package net.itwister.spectator.view.common.base;

import javax.inject.Inject;

import net.itwister.spectator.model.impl.AnalyticsModelImpl.AnalyticsActivityEventListner;
import android.preference.PreferenceActivity;

public class SpectatorPreferenceActivity extends PreferenceActivity {

	@Inject
	AnalyticsActivityEventListner analytics;

	@Override
	protected void onStart() {
		super.onStart();

		analytics.onStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

		analytics.onStop(this);
	}
}