package y2k.spectator.presenter

import rx.Scheduler
import y2k.spectator.RestClient
import y2k.spectator.common.subscribe
import y2k.spectator.model.Subscription

/**
 * Created by y2k on 1/3/16.
 */
class SubscriptionsPresenter(
    private val view: View,
    private val restClient: RestClient,
    private val uiScheduler: Scheduler) {

    init {
        restClient.api
            .subscriptions()
            .subscribe(uiScheduler, {
                view.updateSubscriptions(it.subscriptions)
            }, {
                it.printStackTrace()
            })
    }

    interface View {

        fun updateSubscriptions(subscriptions: List<Subscription>)
    }
}