package net.itwister.spectator;

import android.app.Application;
import android.content.Context;

import com.crittercism.app.Crittercism;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import net.itwister.spectator.model.AccountModel;
import net.itwister.spectator.model.AnalyticsModel;
import net.itwister.spectator.model.BillingModel;
import net.itwister.spectator.model.GcmModel;
import net.itwister.spectator.model.ImageModel;
import net.itwister.spectator.model.RssModel;
import net.itwister.spectator.model.SnapshotModel;
import net.itwister.spectator.model.StashModel;
import net.itwister.spectator.model.SubscriptionModel;
import net.itwister.spectator.model.SyncModel;
import net.itwister.spectator.model.WidgetModel;
import net.itwister.spectator.model.account.PermanentCookieStorage;
import net.itwister.spectator.model.database.SpectatorOpenHelper;
import net.itwister.spectator.model.impl.AccountModelImpl;
import net.itwister.spectator.model.impl.AnalyticsModelImpl;
import net.itwister.spectator.model.impl.BillingModelImpl;
import net.itwister.spectator.model.impl.GcmModelImpl;
import net.itwister.spectator.model.impl.ImageModelImpl;
import net.itwister.spectator.model.impl.RssModelImpl;
import net.itwister.spectator.model.impl.SnapshotModelImpl;
import net.itwister.spectator.model.impl.StashModelImpl;
import net.itwister.spectator.model.impl.SubscriptionModelImpl;
import net.itwister.spectator.model.impl.SyncModelImpl;
import net.itwister.spectator.model.impl.WidgetModelImpl;
import net.itwister.spectator.model.web.SpectatorWebClient;
import net.itwister.spectator.model.web.SpectatorWebClientImpl;

import org.apache.http.client.CookieStore;

import bindui.InjectService;

public class App extends Application {

	private static App sInstance;

	public App() {
		sInstance = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		if (!BuildConfig.DEBUG) Crittercism.initialize(this, "529e1de3d0d8f7014f000003");

		PermanentCookieStorage.initialize(this);
		initializeInjections();
	}

    private void initializeInjections() {
		InjectService.bindInstance(Context.class, this);
        InjectService.bindInstance(Application.class, this);

		InjectService.bindInstance(CookieStore.class, PermanentCookieStorage.getInstance());

		InjectService.bind(SubscriptionModel.class, SubscriptionModelImpl.class);
		InjectService.bind(SnapshotModel.class, SnapshotModelImpl.class);
		InjectService.bind(OrmLiteSqliteOpenHelper.class, SpectatorOpenHelper.class);

		InjectService.bind(AccountModel.class, AccountModelImpl.class);
		InjectService.bind(BillingModel.class, BillingModelImpl.class);
		InjectService.bind(ImageModel.class, ImageModelImpl.class);
		InjectService.bind(SyncModel.class, SyncModelImpl.class);
		InjectService.bind(StashModel.class, StashModelImpl.class);
		InjectService.bind(AnalyticsModel.class, AnalyticsModelImpl.class);
		InjectService.bind(WidgetModel.class, WidgetModelImpl.class);
		InjectService.bind(RssModel.class, RssModelImpl.class);
		InjectService.bind(GcmModel.class, GcmModelImpl.class);

		InjectService.bind(SpectatorWebClient.class, SpectatorWebClientImpl.class);
	}

	public static App getInstance() {
		return sInstance;
	}
}