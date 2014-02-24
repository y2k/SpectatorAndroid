package net.itwister.spectator.loaders;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

import net.itwister.spectator.loaders.JTaskCursorLoader;

import bindui.extra.Task;
import bindui.adapters.UICursorPagerAdapter;

public class JTaskCursorLoaderCallbacks implements LoaderCallbacks<Cursor> {

	private final Task<Cursor> task;
	private CursorAdapter adapter;
	private UICursorPagerAdapter adapter2;

	public JTaskCursorLoaderCallbacks(Task<Cursor> task, CursorAdapter adapter) {
		if (task == null) throw new NullPointerException("task");

		this.task = task;
		this.adapter = adapter;
	}

	public JTaskCursorLoaderCallbacks(Task<Cursor> task, UICursorPagerAdapter adapter) {
		if (task == null) throw new NullPointerException("task");

		this.task = task;
		adapter2 = adapter;
	}

	// ==============================================================
	// Публичные методы
	// ==============================================================

	public void initialize(Fragment fragment, int id) {
		fragment.getLoaderManager().initLoader(id, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new JTaskCursorLoader(task);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (adapter != null) adapter.swapCursor(null);
		else adapter2.changeData(null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (adapter != null) adapter.swapCursor(data);
		else adapter2.changeData(data);
	}

	// ==============================================================
	// Статические методы
	// ==============================================================

	@Deprecated
	public static void init(Fragment f, int id, Task<Cursor> task, CursorAdapter adapter) {
		f.getLoaderManager().initLoader(id, null, new JTaskCursorLoaderCallbacks(task, adapter));
	}

	@Deprecated
	public static void init(FragmentActivity a, int id, Task<Cursor> task, CursorAdapter adapter) {
		a.getSupportLoaderManager().initLoader(id, null, new JTaskCursorLoaderCallbacks(task, adapter));
	}

	@Deprecated
	public static void init(FragmentActivity a, int id, Task<Cursor> task, UICursorPagerAdapter adapter) {
		a.getSupportLoaderManager().initLoader(id, null, new JTaskCursorLoaderCallbacks(task, adapter));
	}
}