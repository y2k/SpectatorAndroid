package y2k.spectator

import rx.Scheduler
import rx.schedulers.Schedulers

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
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe({
                    view.updateSubscriptions(it.subscriptions)
                }, { it.printStackTrace() })
    }

    interface View {

        fun updateSubscriptions(subscriptions: List<Subscription>)
    }
}