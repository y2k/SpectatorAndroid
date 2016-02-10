package y2k.spectator

import rx.Scheduler
import y2k.spectator.presenter.*
import y2k.spectator.service.ImageService
import y2k.spectator.service.NavigationService
import y2k.spectator.service.RestClient
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

    fun resolveCreateSubscriptionPresenter(): CreateSubscriptionPresenter {
        return CreateSubscriptionPresenter(restClient.api, platform.uiScheduler)
    }

    fun resolveSubscriptionsPresenter(): SubscriptionsPresenter {
        return SubscriptionsPresenter(restClient, platform.uiScheduler)
    }

    fun resolveSnapshotsPresenter(): SnapshotsPresenter {
        return SnapshotsPresenter(restClient.api, platform.navigationService, platform.uiScheduler)
    }

    fun resolveLoginPresenter(): LoginPresenter {
        return LoginPresenter(platform.navigationService, platform.uiScheduler, restClient.api)
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