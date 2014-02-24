package net.itwister.spectator.loaders;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import net.itwister.spectator.loaders.JTaskLoader;

import bindui.extra.Task;

public class JTaskLoaderCallbacks<D> implements LoaderCallbacks<D> {

	private final Task<D> task;
	private final Uri notificationUri;

	private final boolean autoStart = true;

	public JTaskLoaderCallbacks(Task<D> task, Uri notificationUri) {
		this.task = task;
		this.notificationUri = notificationUri;
	}

	//	private JTaskLoaderCallbacks(Task<D> task) {
	//		this.task = task;
	//	}

	public void initialize(Fragment fragment, int id) {
		fragment.getLoaderManager().initLoader(id, null, this);
	}

	public void initialize(FragmentActivity activity, int id) {
		activity.getSupportLoaderManager().initLoader(id, null, this);
	}

	@Override
	public Loader<D> onCreateLoader(int id, Bundle args) {
		return new JTaskLoader<D>(task, notificationUri, autoStart);
	}

	@Override
	public void onLoaderReset(Loader<D> loader) {}

	@Override
	public void onLoadFinished(Loader<D> loader, D data) {}

	// ==============================================================
	// Статические методы
	// ==============================================================

	public static <T> void init(Fragment f, int id, Task<T> task) {
		init(f, id, task, null);
	}

	public static <T> void init(Fragment f, int id, Task<T> task, Uri notificationUri) {
		f.getLoaderManager().initLoader(id, null, new JTaskLoaderCallbacks<T>(task, notificationUri));
	}

	public static <T> void init(FragmentActivity a, int id, Task<T> task) {
		init(a, id, task, null);
	}

	public static <T> void init(FragmentActivity a, int id, Task<T> task, Uri notificationUri) {
		a.getSupportLoaderManager().initLoader(id, null, new JTaskLoaderCallbacks<T>(task, notificationUri));
	}
}