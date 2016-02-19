package y2k.spectator.viewmodel

import rx.Scheduler
import rx.schedulers.Schedulers
import y2k.spectator.common.binding
import y2k.spectator.service.Api
import y2k.spectator.service.NavigationService

/**
 * Created by y2k on 1/3/16.
 */
class CreateSubscriptionViewModel(
    private val api: Api,
    private val uiScheduler: Scheduler,
    private val navigationService: NavigationService) {

    val link = binding("")
    val title = binding("")
    val isBusy = binding(false)

    fun create() {
        isBusy.value = false
        api.create(link.value, title.value)
            .subscribeOn(Schedulers.io())
            .observeOn(uiScheduler)
            .subscribe({
                navigationService.openMain()
            }, {
                it.printStackTrace()
                isBusy.value = false
            })
    }
}