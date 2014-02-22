package net.itwister.spectator.view.common.base;

import bindui.app.UIDialogFragment;

public class SpectatorDialogFragment extends UIDialogFragment {

	@Override
	public void onDestroyView() {
		if (getDialog() != null && getRetainInstance()) getDialog().setOnDismissListener(null);
		super.onDestroyView();
	}
}