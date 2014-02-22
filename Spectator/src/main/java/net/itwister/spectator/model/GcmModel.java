package net.itwister.spectator.model;

import android.content.Intent;
import bindui.Task;

public interface GcmModel {

	void onMessage(Intent intent);

	Task<Void> onRegistered(String registrationId);
}