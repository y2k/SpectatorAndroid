package y2k.spectator

import rx.Scheduler
import y2k.spectator.common.subscribe

/**
 * Created by y2k on 1/3/16.
 */
class SubscriptionsPresenter(
    private val view: SubscriptionsPresenter.View,
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