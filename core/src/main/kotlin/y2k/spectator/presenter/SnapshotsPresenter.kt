package y2k.spectator.presenter

import rx.Scheduler
import rx.schedulers.Schedulers
import y2k.spectator.Api
import y2k.spectator.model.Snapshot
import y2k.spectator.service.NavigationService

/**
 * Created by y2k on 1/2/16.
 */
class SnapshotsPresenter(
    private val view: View,
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

    fun openSnapshot(snapshot: Snapshot) {
        navigationService.open(SnapshotInfoPresenter::class.java, "" + snapshot.id)
    }

    interface View {

        fun setLoginButton(visible: Boolean)

        fun update(snapshots: List<Snapshot>)
    }
}