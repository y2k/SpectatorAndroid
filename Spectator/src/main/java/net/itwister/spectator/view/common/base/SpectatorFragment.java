package net.itwister.spectator.view.common.base;

import android.os.Handler;
import bindui.app.UIFragment;

public abstract class SpectatorFragment extends UIFragment {

	protected void post(Runnable r) {
		new Handler().post(r);
	}
}