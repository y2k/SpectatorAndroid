package y2k.spectator

import rx.Scheduler
import y2k.spectator.common.findGroup
import java.net.URLDecoder

/**
 * Created by y2k on 1/2/16.
 */
class LoginPresenter(
        private val view: LoginPresenter.View,
        private val navigationService: NavigationService,
        private val uiScheduler: Scheduler,
        private val accounService: AccounService) {

    init {
        view.loadUrl("https://accounts.google.com/o/oauth2/auth?" +
                "response_type=code" +
                "&client_id=445037560545.apps.googleusercontent.com" +
                "&scope=" + URLDecoder.decode ("https://www.googleapis.com/auth/userinfo.email") +
                "&redirect_uri=" + URLDecoder.decode ("urn:ietf:wg:oauth:2.0:oob:auto") +
                "&access_type=offline")
    }

    fun acceptPage(title: String?) {
        val code = title.findGroup("code=(.+)") ?: return
        view.setBusy(true)
        accounService
                .login(code)
                .observeOn(uiScheduler)
                .subscribe({
                    view.setBusy(false)
                    navigationService.navigateToMain()
                }, {
                    view.setBusy(false)
                    it.printStackTrace()
                })
    }

    interface View {

        fun loadUrl(url: String)

        fun setBusy(isBusy: Boolean)
    }
}