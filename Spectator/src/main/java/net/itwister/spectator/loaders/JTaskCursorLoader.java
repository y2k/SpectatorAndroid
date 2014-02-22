package net.itwister.spectator.loaders;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import net.itwister.spectator.App;
import net.itwister.tools.inner.Ln;

import bindui.Task;

public class JTaskCursorLoader extends AsyncTaskLoader<Cursor> {

	private final ForceLoadContentObserver mObserver;
	private Cursor mCursor;
	private final Task<Cursor> task;

	public JTaskCursorLoader() {
		this(App.getInstance(), null);
	}

	public JTaskCursorLoader(Task<Cursor> task) {
		this(App.getInstance(), task);
	}

	private JTaskCursorLoader(Context context, Task<Cursor> task) {
		super(context);

		if (context == null) throw new NullPointerException("context");
		if (task == null) throw new NullPointerException("task");

		this.task = task;
		mObserver = new ForceLoadContentObserver();
	}

	/* Runs on the UI thread */
	@Override
	public void deliverResult(Cursor cursor) {
		if (isReset()) {
			// An async query came in while the loader is stopped
			if (cursor != null) {
				cursor.close();
			}
			return;
		}
		Cursor oldCursor = mCursor;
		mCursor = cursor;

		if (isStarted()) {
			super.deliverResult(cursor);
		}

		if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
			oldCursor.close();
		}
	}

	/* Runs on a worker thread */
	@Override
	public Cursor loadInBackground() {
		Cursor cursor = null;
		try {
			cursor = getCursorInBackground(getContext());
		} catch (Exception e) {
            Ln.printStackTrace(e);
        }

		if (cursor != null) {
			// Ensure the cursor window is filled
			cursor.getCount();
			registerContentObserver(cursor, mObserver);
		}
		return cursor;
	}

	@Override
	public void onCanceled(Cursor cursor) {
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}

	/**
	 * Registers an observer to get notifications from the content provider
	 * when the cursor needs to be refreshed.
	 */
	void registerContentObserver(Cursor cursor, ContentObserver observer) {
		cursor.registerContentObserver(mObserver);
	}

	protected Cursor getCursorInBackground(Context context) throws Exception {
		if (task == null) throw new NullPointerException("task");
		return task.executeSync();
	}

	@Override
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		if (mCursor != null && !mCursor.isClosed()) {
			mCursor.close();
		}
		mCursor = null;
	}

	/**
	 * Starts an asynchronous load of the contacts list data. When the result is ready the callbacks
	 * will be called on the UI thread. If a previous load has been completed and is still valid
	 * the result may be passed to the callbacks immediately.
	 * Must be called from the UI thread
	 */
	@Override
	protected void onStartLoading() {
		if (mCursor != null) {
			deliverResult(mCursor);
		}
		if (takeContentChanged() || mCursor == null) {
			forceLoad();
		}
	}

	/** Must be called from the UI thread */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}
}
