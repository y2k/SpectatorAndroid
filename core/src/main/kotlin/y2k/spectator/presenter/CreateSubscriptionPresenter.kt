package y2k.spectator.presenter

import rx.Scheduler
import rx.schedulers.Schedulers
import y2k.spectator.Api

/**
 * Created by y2k on 1/3/16.
 */
class CreateSubscriptionPresenter(
    private val view: View,
    private val api: Api,
    private val uiScheduler: Scheduler) {

    fun create() {
        // TODO:
        view.setBusy(true)
        api.create(view.link, view.title)
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe({
                    // TODO:
                    view.setBusy(false)
                }, {
                    it.printStackTrace()
                    view.setBusy(false)
                })
    }

    interface View {

        val title: String
        val link: String

        fun setBusy(isBusy: Boolean)
    }
}