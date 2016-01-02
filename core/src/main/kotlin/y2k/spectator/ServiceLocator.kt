package y2k.spectator

import rx.Scheduler

/**
 * Created by y2k on 1/2/16.
 */
object ServiceLocator {

    lateinit var platform: Platform

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