package y2k.spectator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import y2k.spectator.common.*
import y2k.spectator.viewmodel.LoginViewModel

/**
 * Created by y2k on 1/2/16.
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val viewModel = ServiceLocator.resolve(LoginViewModel::class)
        bind(R.id.progress, viewModel.isBusy)
        find<WebView>(R.id.webView) {
            settings.javaScriptEnabled = true
            bindUrl(viewModel.url)
            bindTitle(viewModel.title)
        }
    }
}