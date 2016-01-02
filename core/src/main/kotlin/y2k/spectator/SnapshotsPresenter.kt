package y2k.spectator

import rx.Observable
import rx.Scheduler
import java.util.concurrent.TimeUnit

/**
 * Created by y2k on 1/2/16.
 */
class SnapshotsPresenter(
        private val view: SnapshotsPresenter.View,
        private val navigationService: NavigationService,
        private val uiScheduler: Scheduler) {

    init {
        Observable
                .just(null)
                .delay(2, TimeUnit.SECONDS)
                .observeOn(uiScheduler)
                .subscribe { view.showLogin() }
    }

    fun login() {
        navigationService.navigateToLogin()
    }

    interface View {

        fun showLogin()
    }
}