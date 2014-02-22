package net.itwister.spectator.model.settings;

import net.itwister.spectator.App;
import android.preference.PreferenceManager;

public class SettingsManager {

	public boolean isNotificationsEnabled() {
		return PreferenceManager.getDefaultSharedPreferences(App.getInstance()).getBoolean("notifications", true);
	}
}