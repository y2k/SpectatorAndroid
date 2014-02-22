package net.itwister.spectator.view.common;

import net.itwister.spectator.App;
import net.itwister.spectator.model.SyncModel;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.LocalBroadcastManager;

import bindui.InjectService;

public class LoaderBroadcastReceiver extends BroadcastReceiver {

	private final LoaderManager manager;

	private LoaderBroadcastReceiver(LoaderManager manager) {
		this.manager = manager;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		manager.getLoader(0).forceLoad();
	}

	public static BroadcastReceiver register(BroadcastReceiver receiver, LoaderManager manager) {
		if (receiver == null) receiver = new LoaderBroadcastReceiver(manager);

//		SyncModel model = RoboGuice.getBaseApplicationInjector(App.getInstance()).getInstance(SyncModel.class);
        SyncModel model = InjectService.getInstance(SyncModel.class);
        model.registerObserverReceiver(receiver);

		//		LocalBroadcastManager.getInstance(App.getInstance()).registerReceiver(receiver, new IntentFilter(action));
		return receiver;
	}

	public static void unregister(BroadcastReceiver receiver) {
		if (receiver != null) LocalBroadcastManager.getInstance(App.getInstance()).unregisterReceiver(receiver);
	}
}