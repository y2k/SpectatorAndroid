package net.itwister.spectator.model.helpers;

import android.content.Context;
import bindui.InjectService;
import bindui.Task;

public abstract class SpectatorTask<ResultT> extends Task<ResultT> {

	public SpectatorTask() {
		InjectService.inject(this, SpectatorTask.class);
	}

	// ==============================================================
	// Защищенные методы
	// ==============================================================

	protected Context getContext() {
		return InjectService.getInstance(Context.class);
	}

	protected void throwIfInterrupted() throws InterruptedException {
		if (Thread.interrupted()) throw new InterruptedException();
	}

	public static void cancelWhenNotNull(Task<?> task) {
		if (task != null) task.cancel(true);
	}
}