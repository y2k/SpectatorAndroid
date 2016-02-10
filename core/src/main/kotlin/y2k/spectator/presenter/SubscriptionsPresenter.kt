package y2k.spectator.presenter

import rx.Scheduler
import y2k.spectator.common.binding
import y2k.spectator.common.subscribe
import y2k.spectator.model.Subscription
import y2k.spectator.service.RestClient

/**
 * Created by y2k on 1/3/16.
 */
class SubscriptionsPresenter(
    private val restClient: RestClient,
    private val uiScheduler: Scheduler) {

    val subscriptions = binding(emptyList<Subscription>())

    init {
        restClient.api
            .subscriptions()
            .subscribe(uiScheduler,
                { subscriptions.value = it.subscriptions },
                { it.printStackTrace() })
    }
}