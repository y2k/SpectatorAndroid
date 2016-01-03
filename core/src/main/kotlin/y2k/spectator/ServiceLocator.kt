package y2k.spectator

import rx.Scheduler
import java.util.*

/**
 * Created by y2k on 1/2/16.
 */
object ServiceLocator {

    lateinit var platform: Platform

    private val restClient = RestClient(TestCookeStorage())

    fun resolveCreateSubscriptionPresenter(view: CreateSubscriptionPresenter.View): CreateSubscriptionPresenter {
        return CreateSubscriptionPresenter(view, restClient.api, platform.resolveScheduler())
    }

    fun resolveSubscriptionsPresenter(view: SubscriptionsPresenter.View): SubscriptionsPresenter {
        return SubscriptionsPresenter(view, restClient, platform.resolveScheduler())
    }

    fun resolveSnapshotsPresenter(view: SnapshotsPresenter.View): SnapshotsPresenter {
        return SnapshotsPresenter(view,
                platform.resolveNavigationService(),
                platform.resolveScheduler())
    }

    fun resolveLoginPresenter(view: LoginPresenter.View): LoginPresenter {
        return LoginPresenter(view,
                platform.resolveNavigationService(),
                platform.resolveScheduler(),
                restClient.api)
    }

    interface Platform {
        fun resolveScheduler(): Scheduler
        fun resolveNavigationService(): NavigationService
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