package net.itwister.spectator.view.helpers;

import net.itwister.spectator.App;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class BroadcastHelper {

	public static final String PARAMETER = "arg1";
	public static final String PARAMETER2 = "arg2";
	public static final String PARAMETER3 = "arg3";
	public static final String PARAMETER4 = "arg4";

	private static final String[] ORDER = { PARAMETER, PARAMETER2, PARAMETER3, PARAMETER4 };

	public static void regiser(String action, BroadcastReceiver receiver) {
		LocalBroadcastManager.getInstance(App.getInstance()).registerReceiver(receiver, new IntentFilter(action));
	}

	public static void send(String action, Object... args) {
		Intent in = new Intent(action);

		for (int i = 0; i < args.length; i++) {
			Object o = args[i];
			if (o instanceof Integer) in.putExtra(ORDER[i], (Integer) o);
		}

		LocalBroadcastManager.getInstance(App.getInstance()).sendBroadcast(in);
	}

	public static void unregister(BroadcastReceiver receiver) {
		LocalBroadcastManager.getInstance(App.getInstance()).unregisterReceiver(receiver);
	}

	public static abstract class GenericBroadcastReceiver<T> extends BroadcastReceiver {

		@SuppressWarnings("unchecked")
		@Override
		public void onReceive(Context context, Intent intent) {
			T i = (T) intent.getExtras().get(PARAMETER);
			onReceive(context, i);
		}

		public abstract void onReceive(Context context, T arg);
	}
}