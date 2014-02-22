package net.itwister.spectator.view.helpers;

import net.itwister.spectator.BuildConfig;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class VersionHelper {

	public static String getVersionNumber(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			if (BuildConfig.DEBUG) e.printStackTrace();
			return null;
		}
	}

	public static String getVersion(Context context) {
		String num = getVersionNumber(context);
		return num + "." + (BuildConfig.DEBUG ? "D" : "R");
	}
}