package y2k.spectator

import rx.Scheduler

/**
 * Created by y2k on 1/2/16.
 */
abstract class BaseServiceLocator {

    fun resolveSnapshotsPresenter(view: SnapshotsPresenter.View): SnapshotsPresenter {
        return SnapshotsPresenter(view,
                resolveNavigationService(),
                resolveScheduler())
    }

    fun resolveLoginPresenter(view: LoginPresenter.View): LoginPresenter {
        return LoginPresenter(view,
                resolveNavigationService(),
                resolveScheduler(),
                resolveAccountService())
    }

    private fun resolveAccountService(): AccounService {
        return AccounService()
    }

    protected abstract fun resolveScheduler(): Scheduler

    protected abstract fun resolveNavigationService(): NavigationService
}