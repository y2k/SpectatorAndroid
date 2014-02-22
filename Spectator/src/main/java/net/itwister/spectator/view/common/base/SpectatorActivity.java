package net.itwister.spectator.view.common.base;

import javax.inject.Inject;

import net.itwister.spectator.model.impl.AnalyticsModelImpl.AnalyticsActivityEventListner;
import bindui.app.UIActivity;

public class SpectatorActivity extends UIActivity {

	@Inject
	AnalyticsActivityEventListner analytics;

	@Inject
	ForegroundModule foregroundModule;

	@Override
	protected void onStart() {
		super.onStart();

		foregroundModule.onStart();
		analytics.onStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

		foregroundModule.onStop();
		analytics.onStop(this);
	}

	public static class ForegroundModule {

		private static volatile int sForegroundIndex;

		public void onStart() {
			sForegroundIndex++;
		}

		public void onStop() {
			sForegroundIndex--;
		}

		public static boolean isForeground() {
			return sForegroundIndex > 0;
		}
	}
}