package y2k.spectator

import rx.Scheduler

/**
 * Created by y2k on 1/2/16.
 */
object ServiceLocator {

    lateinit var platform: Platform

    private val restClient = RestClient()

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
                resolveAccountService())
    }

    private fun resolveAccountService(): AccounService {
        return AccounService()
    }

    interface Platform {
        fun resolveScheduler(): Scheduler
        fun resolveNavigationService(): NavigationService
    }
}