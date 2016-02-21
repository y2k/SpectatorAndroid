package y2k.spectator.viewmodel

import rx.Scheduler
import y2k.spectator.common.binding
import y2k.spectator.common.subscribe
import y2k.spectator.model.Subscription
import y2k.spectator.service.Api
import y2k.spectator.service.NavigationService
import y2k.spectator.service.RssService

/**
 * Created by y2k on 1/3/16.
 */
class CreateSubscriptionViewModel(
    private val api: Api,
    private val service: RssService,
    private val uiScheduler: Scheduler,
    private val navigationService: NavigationService) {

    val link = binding("")
    val title = binding("")
    val isBusy = binding(false)
    val rssItems = binding(emptyList<Subscription>())

    fun analyze() {
        isBusy.value = true
        service
            .analyze(link.value)
            .subscribe(uiScheduler, {
                rssItems.value = it
                isBusy.value = false
            }, {
                it.printStackTrace()
                isBusy.value = false
            })
    }

//    fun create() {
//        isBusy.value = true
//        api.create(link.value, title.value)
//            .subscribe(uiScheduler, {
//                navigationService.openMain()
//            }, {
//                it.printStackTrace()
//                isBusy.value = false
//            })
//    }
}