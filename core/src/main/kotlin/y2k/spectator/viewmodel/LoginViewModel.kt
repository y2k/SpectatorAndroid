package y2k.spectator.viewmodel

import rx.Scheduler
import rx.schedulers.Schedulers
import y2k.spectator.common.binding
import y2k.spectator.common.findGroup
import y2k.spectator.service.Api
import y2k.spectator.service.NavigationService
import java.net.URLEncoder

/**
 * Created by y2k on 1/2/16.
 */
class LoginViewModel(
    private val navigationService: NavigationService,
    private val uiScheduler: Scheduler,
    private val api: Api) {

    val url = binding("about:blank")
    val isBusy = binding(false)
    val title = binding("")

    init {
        url.value = "https://accounts.google.com/o/oauth2/auth?" +
            "response_type=code" +
            "&client_id=445037560545.apps.googleusercontent.com" +
            "&scope=" + URLEncoder.encode("https://www.googleapis.com/auth/userinfo.email") +
            "&redirect_uri=" + URLEncoder.encode("urn:ietf:wg:oauth:2.0:oob:auto") +
            "&access_type=offline"

        title.subscribe {
            val code = it.findGroup("code=(.+)") ?: return@subscribe
            isBusy.value = true
            api.login(code, "urn:ietf:wg:oauth:2.0:oob:auto")
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe({
                    isBusy.value = false
                    navigationService.openMain()
                }, {
                    it.printStackTrace()
                    isBusy.value = false
                })
        }
    }
}