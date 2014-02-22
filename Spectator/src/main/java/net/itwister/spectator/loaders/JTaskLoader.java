package net.itwister.spectator.loaders;

import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import net.itwister.spectator.App;

import bindui.Task;

public class JTaskLoader<D> extends AsyncTaskLoader<D> {

	private final Task<D> task;
	private final boolean autoStart;
	private ForceLoadContentObserver observer;

	public JTaskLoader(Task<D> task, Uri notificationUri, boolean autoStart) {
		super(App.getInstance());
		this.task = task;
		this.autoStart = autoStart;

		if (notificationUri != null) {
			observer = new ForceLoadContentObserver();
			App.getInstance().getContentResolver().registerContentObserver(notificationUri, false, observer);
		}
	}

	@Override
	public D loadInBackground() {
		return task.executeSyncWithoutException();
	}

	@Override
	protected void onReset() {
		if (observer != null) {
			App.getInstance().getContentResolver().unregisterContentObserver(observer);
		}
	}

	@Override
	protected void onStartLoading() {
		if (autoStart) forceLoad();
	}
}