package y2k.spectator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Created by y2k on 1/2/16.
 */
class LoginActivity : AppCompatActivity() {

    lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        presenter = ServiceLocator.resolveLoginPresenter(object : LoginPresenter.View {

            val webView = findViewById(R.id.webView) as WebView
            val progress = findViewById(R.id.progress)

            init {
                webView.settings.javaScriptEnabled = true
                webView.setWebViewClient(object : WebViewClient() {

                    override fun onPageFinished(view: WebView, url: String?) {
                        presenter.acceptPage(view.title)
                    }

                    override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                        view.loadUrl(url)
                        return true;
                    }
                })
            }

            override fun loadUrl(url: String) {
                webView.loadUrl(url)
            }

            override fun setBusy(isBusy: Boolean) {
                progress.visibility = View.VISIBLE
                progress.animate().alpha(if (isBusy) 1f else 0f)
            }
        })
    }
}