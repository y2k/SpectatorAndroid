package net.itwister.spectator.view.helpers;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.widget.BaseAdapter;

public class UiTimer {

	private final Handler handler = new Handler();
	private final int defaultDelay;

	public UiTimer() {
		this(1000);
	}

	public UiTimer(int defaultDelay) {
		this.defaultDelay = defaultDelay;
	}

	public void clear() {
		handler.removeCallbacksAndMessages(null);
	}

	public void schedule(Runnable callback) {
		handler.removeCallbacksAndMessages(null);
		handler.postDelayed(callback, defaultDelay);
	}

	public void scheduleNotifyDataSetChanged(BaseAdapter adapter) {
		schedule(new AdapterRunnable(adapter));
	}

	static class AdapterRunnable implements Runnable {

		WeakReference<BaseAdapter> adapter;

		AdapterRunnable(BaseAdapter adapter) {
			this.adapter = new WeakReference<BaseAdapter>(adapter);
		}

		@Override
		public void run() {
			BaseAdapter a = adapter.get();
			if (a != null) a.notifyDataSetChanged();
		}
	}
}