package y2k.spectator.viewmodel

import rx.Scheduler
import y2k.spectator.common.binding
import y2k.spectator.common.subscribe
import y2k.spectator.model.Page
import y2k.spectator.model.Snapshot
import y2k.spectator.service.Api
import y2k.spectator.service.NavigationService

/**
 * Created by y2k on 1/18/16.
 */
class SnapshotInfoViewModel(
    private val api: Api,
    private val navigationService: NavigationService,
    private val uiScheduler: Scheduler) {

    private var snapshot: Snapshot? = null
    private var snapshotId: String

    val info = binding(Snapshot())
    val contentUrl = binding(Page())
    val diffUrl = binding(Page())

    init {
        snapshotId = navigationService.getArgument()!!
        api.snapshot(snapshotId)
            .subscribe(uiScheduler) {
                snapshot = it
                info.value = it
            }
    }

    fun tabSelected(position: Int) {
        when (position) {
            0 -> {
                contentUrl.value = Page()
                diffUrl.value = Page()
            }
            1 -> {
                diffUrl.value = Page()
                api.content(snapshotId).subscribe(uiScheduler) {
                    contentUrl.value = Page(snapshot!!.source, it.string())
                }
            }
            2 -> {
                contentUrl.value = Page()
                api.content(snapshotId).subscribe(uiScheduler) {
                    diffUrl.value = Page(snapshot!!.source, it.string())
                }
            }
        }
    }
}