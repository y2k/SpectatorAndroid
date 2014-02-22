package net.itwister.spectator.view.common;

import java.io.Serializable;

import net.itwister.spectator.view.home.HomeActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class Activities {

	public static final String PARAMETER = "arg1";
	public static final String PARAMETER2 = "arg2";
	public static final String PARAMETER3 = "arg3";
	public static final String PARAMETER4 = "arg4";

	private static final String[] ORDER = { PARAMETER, PARAMETER2, PARAMETER3, PARAMETER4 };

	public static Intent newIntent(Context context, Class<?> activityCls, Object... args) {
		Intent intent = new Intent(context, activityCls);

		for (int i = 0; i < args.length; i++) {
			Object o = args[i];
			if (o instanceof String) intent.putExtra(ORDER[i], (String) o);
			else if (o instanceof Integer) intent.putExtra(ORDER[i], (Integer) o);
			else if (o instanceof Serializable) intent.putExtra(ORDER[i], (Serializable) o);
		}

		return intent;
	}

	public static void show(HomeActivity current, Class<? extends Activity> target) {
		current.startActivity(new Intent(current, target));
	}

	public static void showAndClose(Activity current, Class<? extends Activity> target, Bundle extras) {
		showAndCloseAnimated(current, target, extras);
		current.overridePendingTransition(0, 0);
	}

	public static void showAndCloseAnimated(Activity current, Class<? extends Activity> target, Bundle extras) {
		Intent i = new Intent(current, target);
		if (extras != null) i.putExtras(extras);
		current.startActivity(i);
		current.finish();
	}
}