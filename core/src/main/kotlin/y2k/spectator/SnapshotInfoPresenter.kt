package y2k.spectator

import rx.Scheduler
import rx.schedulers.Schedulers

/**
 * Created by y2k on 1/18/16.
 */
class SnapshotInfoPresenter(
    private val view: SnapshotInfoPresenter.View,
    private val api: Api,
    private val navigationService: NavigationService,
    private val uiScheduler: Scheduler) {

    init {
        api.snapshot(navigationService.getArgument()!!)
            .subscribeOn(Schedulers.io())
            .observeOn(uiScheduler)
            .subscribe {
                view.updateInfo(it)
            }
    }

    fun tabSelected(position: Int) {
        // throw UnsupportedOperationException("not implemented") // FIXME:
    }

    interface View {

        fun updateInfo(snapshot: Snapshot)
    }
}