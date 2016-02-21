package y2k.spectator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import y2k.spectator.binding.bindingBuilder
import y2k.spectator.viewmodel.LoginViewModel

/**
 * Created by y2k on 1/2/16.
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val viewModel = ServiceLocator.resolve(LoginViewModel::class)

        bindingBuilder(this) {
            visibility(R.id.progress, viewModel.isBusy)
            webView(R.id.webView) {
                settings.javaScriptEnabled = true
                url(viewModel.url)
                title(viewModel.title)
            }
        }

        //        bind(R.id.progress, viewModel.isBusy)
        //        find<WebView>(R.id.webView) {
        //            settings.javaScriptEnabled = true
        //            bindUrl(viewModel.url)
        //            bindTitle(viewModel.title)
        //        }
    }
}