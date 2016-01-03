package y2k.spectator

import rx.Scheduler
import rx.schedulers.Schedulers

/**
 * Created by y2k on 1/2/16.
 */
class SnapshotsPresenter(
        private val view: SnapshotsPresenter.View,
        private val api: Api,
        private val navigationService: NavigationService,
        private val uiScheduler: Scheduler) {

    init {
        view.setLoginButton(false)

        api.snapshots()
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe ({
                    view.update(it.snapshots)
                }, {
                    it.printStackTrace()
                    view.setLoginButton(true)
                })
    }

    fun login() {
        navigationService.openLogin()
    }

    fun add() {
        navigationService.openAddSubscription()
    }

    interface View {

        fun setLoginButton(visible: Boolean)

        fun update(snapshots: List<Snapshot>)
    }
}