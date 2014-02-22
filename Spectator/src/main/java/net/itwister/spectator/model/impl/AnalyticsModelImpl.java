package net.itwister.spectator.model.impl;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.itwister.spectator.BuildConfig;
import net.itwister.spectator.model.AnalyticsModel;
import net.itwister.spectator.model.SyncModel.SyncTarget;
import net.itwister.tools.inner.Ln;
import android.content.Context;

import com.flurry.android.FlurryAgent;

@Singleton
public class AnalyticsModelImpl implements AnalyticsModel {

	@Override
	public void eventOpenSettings() {
		safeLogEvent("OpenSettings");
	}

	@Override
	public void eventOpenSnapshot(long id) {
		safeLogEvent("OpenSnapshot", "Id", "" + id);
	}

	@Override
	public void eventReloadErrorList(SyncTarget target, String query) {
		safeLogEvent("ReloadErrorList", "Target", "" + target, "Query", "" + query);
	}

	@Override
	public void eventSearch(String query) {
		safeLogEvent("Search", "Query", query);
	}

	@Override
	public void eventStashAdd(boolean success) {
		safeLogEvent("AddStash", "Success", "" + success);
	}

	@Override
	public void eventStashRemove(boolean success) {
		safeLogEvent("RemoveStash", "Success", "" + success);
	}

	@Override
	public void eventUserLogout() {
		safeLogEvent("Logout");
	}

	@Override
	public void eventUserRefreshHome() {
		safeLogEvent("RefreshHome");
	}

	@Override
	public void eventUserWantSendFeedback() {
		safeLogEvent("UserWantSendFeedback");
	}

	private void safeLogEvent(String action, String... paramNameValues) {
		if (BuildConfig.DEBUG) {
			Ln.i("Analytics [" + action + "][" + paramNameValues.length + "]");
		} else {
			try {
				if (paramNameValues.length < 2) {
					FlurryAgent.logEvent(action);
				} else {
					Map<String, String> args = new HashMap<String, String>();
					for (int i = 0; i < paramNameValues.length - 1; i += 2) {
						args.put(paramNameValues[i], paramNameValues[i + 1]);
					}
					FlurryAgent.logEvent(action, args);
				}
			} catch (Exception e) {
				if (BuildConfig.DEBUG) e.printStackTrace();
			}
		}
	}

	public static class AnalyticsActivityEventListner {

		@Inject
		private Context context;

		public void onStart(Context context) {
			if (!BuildConfig.DEBUG) FlurryAgent.onStartSession(context, "KBGZ7TPVSKT5PJGC7CQ2");
		}

		public void onStop(Context context) {
			if (!BuildConfig.DEBUG) FlurryAgent.onEndSession(context);
		}
	}
}