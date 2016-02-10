package y2k.spectator.presenter

import rx.Scheduler
import rx.schedulers.Schedulers
import y2k.spectator.common.binding
import y2k.spectator.service.Api

/**
 * Created by y2k on 1/3/16.
 */
class CreateSubscriptionPresenter(
    private val api: Api,
    private val uiScheduler: Scheduler) {

    val link = binding("")
    val title = binding("")
    val isBusy = binding(false)

    fun create() {
        isBusy.value = false
        api.create(link.value, title.value)
            .subscribeOn(Schedulers.io())
            .observeOn(uiScheduler)
            .subscribe({
                // TODO:
                isBusy.value = false
            }, {
                it.printStackTrace()
                isBusy.value = false
            })
    }
}