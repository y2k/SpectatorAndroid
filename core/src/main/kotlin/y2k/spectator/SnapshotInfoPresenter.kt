package y2k.spectator

import rx.Scheduler
import y2k.spectator.common.subscribe

/**
 * Created by y2k on 1/18/16.
 */
class SnapshotInfoPresenter(
    private val view: SnapshotInfoPresenter.View,
    private val api: Api,
    private val navigationService: NavigationService,
    private val uiScheduler: Scheduler) {

    private var snapshot: Snapshot? = null
    private var snapshotId: String

    init {
        snapshotId = navigationService.getArgument()!!
        api.snapshot(snapshotId)
            .subscribe(uiScheduler) {
                snapshot = it
                view.updateInfo(it)
            }
    }

    fun tabSelected(position: Int) {
        when (position) {
            1 -> {
                api.content(snapshotId)
                    .subscribe(uiScheduler) {
                        view.updateBrowser(it.string(), snapshot!!.source)
                    }
            }
        }
    }

    interface View {

        fun updateInfo(snapshot: Snapshot)

        fun updateBrowser(data: String, baseUrl: String)
    }
}