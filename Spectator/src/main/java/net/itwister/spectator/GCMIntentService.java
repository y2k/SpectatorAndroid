package net.itwister.spectator;

import javax.inject.Inject;

import net.itwister.spectator.model.GcmModel;
import net.itwister.tools.inner.Ln;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import bindui.InjectService;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class GCMIntentService extends GCMBaseIntentService {

	@Inject
	private GcmModel model;

	public GCMIntentService() {
		super(Constants.SENDER_ID);

		InjectService.injectSimple(this);
	}

	@Override
	protected void onError(Context context, String errorId) {
		Ln.w("onError(errorId = %s)", errorId);
	}

	@Override
	protected void onMessage(Context context, final Intent intent) {
		model.onMessage(intent);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		model.onRegistered(registrationId).sync();
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {}

	public static void initialize(Context context) {
		if (BuildConfig.DEBUG) {
			GCMRegistrar.checkDevice(context);
			GCMRegistrar.checkManifest(context);
		}

		String regid = GCMRegistrar.getRegistrationId(context);
		if (TextUtils.isEmpty(regid)) GCMRegistrar.register(context, Constants.SENDER_ID);
		else InjectService.getInstance(GcmModel.class).onRegistered(regid).async();
	}
}