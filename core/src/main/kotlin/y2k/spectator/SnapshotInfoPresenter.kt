package y2k.spectator

import rx.Scheduler

/**
 * Created by y2k on 1/18/16.
 */
class SnapshotInfoPresenter(
    private val view: SnapshotInfoPresenter.View,
    private val api: Api,
    private val navigationService: NavigationService,
    private val uiScheduler: Scheduler) {

    init {
        // TODO:
    }

    interface View
}