package y2k.spectator

import org.robovm.apple.uikit.UIViewController
import org.robovm.apple.uikit.UIWebView
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.spectator.common.bindTitle
import y2k.spectator.common.bindUrl
import y2k.spectator.presenter.LoginViewModel

/**
 * Created by y2k on 2/11/16.
 */
@CustomClass("LoginViewController")
class LoginViewController : UIViewController() {

    @IBOutlet lateinit var webView: UIWebView

    override fun viewDidLoad() {
        super.viewDidLoad()

        ServiceLocator
            .resolve(LoginViewModel::class)
            .apply {
                webView.bindUrl(url)
                webView.bindTitle(title)
            }
    }
}