package y2k.spectator.presenter

import rx.Scheduler
import y2k.spectator.model.Snapshot
import y2k.spectator.service.Api
import y2k.spectator.service.NavigationService
import y2k.spectator.common.subscribe

/**
 * Created by y2k on 1/18/16.
 */
class SnapshotInfoPresenter(
    private val view: View,
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
            0 -> {
                view.updateBrowser("", "about:blank")
                view.updateDiffBrowser("", "about:blank")
            }
            1 -> {
                view.updateDiffBrowser("", "about:blank")
                api.content(snapshotId)
                    .subscribe(uiScheduler) { view.updateBrowser(it.string(), snapshot!!.source) }
            }
            2 -> {
                view.updateBrowser("", "about:blank")
                api.content(snapshotId)
                    .subscribe(uiScheduler) { view.updateDiffBrowser(it.string(), snapshot!!.source) }
            }
        }
    }

    interface View {

        fun updateInfo(snapshot: Snapshot)

        fun updateBrowser(data: String, baseUrl: String)

        fun updateDiffBrowser(data: String, baseUrl: String)
    }
}