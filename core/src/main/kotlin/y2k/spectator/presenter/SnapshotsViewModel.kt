package y2k.spectator.presenter

import rx.Scheduler
import rx.schedulers.Schedulers
import y2k.spectator.common.binding
import y2k.spectator.model.Snapshot
import y2k.spectator.service.Api
import y2k.spectator.service.NavigationService

/**
 * Created by y2k on 1/2/16.
 */
class SnapshotsViewModel(
    private val api: Api,
    private val navigationService: NavigationService,
    private val uiScheduler: Scheduler) {

    val isNeedLogin = binding(false)
    val snapshots = binding(emptyList<Snapshot>())

    init {
        api.snapshots()
            .subscribeOn(Schedulers.io())
            .observeOn(uiScheduler)
            .subscribe ({
                snapshots.value = it.snapshots
            }, {
                it.printStackTrace()
                isNeedLogin.value = true
            })
    }

    fun login() {
        navigationService.openLogin()
    }

    fun add() {
        navigationService.openAddSubscription()
    }

    fun openSnapshot(position: Int) {
        openSnapshot(snapshots.value[position])
    }

    fun openSnapshot(snapshot: Snapshot) {
        navigationService.open(SnapshotInfoViewModel::class.java, "" + snapshot.id)
    }
}