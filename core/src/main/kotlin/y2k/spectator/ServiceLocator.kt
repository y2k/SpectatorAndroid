package y2k.spectator

import rx.Scheduler
import y2k.spectator.presenter.*
import y2k.spectator.service.ImageService
import y2k.spectator.service.NavigationService
import java.util.*

/**
 * Created by y2k on 1/2/16.
 */
object ServiceLocator {

    lateinit var platform: Platform

    private val restClient = RestClient(TestCookeStorage())

    fun <T> resolveImageService(): ImageService<T> {
        return ImageService(restClient.api, platform.uiScheduler, platform.decoder)
    }

    fun resolveSnapshotPresenter(view: SnapshotInfoPresenter.View): SnapshotInfoPresenter {
        return SnapshotInfoPresenter(view,
            restClient.api,
            platform.navigationService,
            platform.uiScheduler)
    }

    fun resolveCreateSubscriptionPresenter(view: CreateSubscriptionPresenter.View): CreateSubscriptionPresenter {
        return CreateSubscriptionPresenter(view, restClient.api, platform.uiScheduler)
    }

    fun resolveSubscriptionsPresenter(view: SubscriptionsPresenter.View): SubscriptionsPresenter {
        return SubscriptionsPresenter(view, restClient, platform.uiScheduler)
    }

    fun resolveSnapshotsPresenter(view: SnapshotsPresenter.View): SnapshotsPresenter {
        return SnapshotsPresenter(view,
            restClient.api,
            platform.navigationService,
            platform.uiScheduler)
    }

    fun resolveLoginPresenter(view: LoginPresenter.View): LoginPresenter {
        return LoginPresenter(view,
            platform.navigationService,
            platform.uiScheduler,
            restClient.api)
    }

    interface Platform {

        val decoder: ImageService.Decoder
        val uiScheduler: Scheduler
        val navigationService: NavigationService
    }

    class TestCookeStorage : RestClient.CookieStorage {

        val storage = HashSet<String>()

        override fun getAll(): Set<String> {
            synchronized(storage) { return storage.toSet() }
        }

        override fun put(cookies: HashSet<String>) {
            synchronized(storage) { storage.addAll(cookies) }
        }
    }
}