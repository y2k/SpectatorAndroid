package y2k.spectator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import y2k.spectator.common.bind
import y2k.spectator.common.bindLoadUrl
import y2k.spectator.common.bindTitle
import y2k.spectator.common.find
import y2k.spectator.presenter.LoginViewModel

/**
 * Created by y2k on 1/2/16.
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val viewModel = ServiceLocator.resolve(LoginViewModel::class)
        findViewById(R.id.progress).bind(viewModel.isBusy)
        find<WebView>(R.id.webView).apply {
            settings.javaScriptEnabled = true
            bindLoadUrl(viewModel.url)
            bindTitle(viewModel.title)
        }
    }
}