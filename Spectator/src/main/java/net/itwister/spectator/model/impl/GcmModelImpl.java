package net.itwister.spectator.model.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.itwister.spectator.model.GcmModel;
import net.itwister.spectator.model.SyncModel;
import net.itwister.spectator.model.web.SpectatorWebClient;
import net.itwister.tools.inner.Ln;
import android.content.Intent;
import bindui.Task;

@Singleton
public class GcmModelImpl implements GcmModel {

	@Inject
	private SyncModel syncModel;

	@Inject
	private SpectatorWebClient web;

	@Override
	public void onMessage(Intent intent) {
		try {
			syncModel.syncSubscriptionsAndShowNotification();
		} catch (Exception e) {
			Ln.printStackTrace(e);
		}
	}

	@Override
	public Task<Void> onRegistered(final String registrationId) {
		return new Task<Void>() {

			@Override
			protected void onExecute() throws Exception {
				web.api().setGcmRegistrationId(registrationId);
			}
		};
	}
}