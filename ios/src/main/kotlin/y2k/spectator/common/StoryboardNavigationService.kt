package y2k.spectator.common

import org.robovm.apple.uikit.UIApplication
import org.robovm.apple.uikit.UINavigationController
import y2k.spectator.service.NavigationService

/**
 * Created by y2k on 2/10/16.
 */
class StoryboardNavigationService : NavigationService {

    override fun openMain() {
        throw UnsupportedOperationException()
    }

    override fun openLogin() {
        val vc = navigationController.storyboard.instantiateViewController("Login")
        navigationController.pushViewController(vc, true)
    }

    override fun openAddSubscription() {
        throw UnsupportedOperationException()
    }

    override fun <T> open(presenter: Class<T>, arg: String?) {
        throw UnsupportedOperationException()
    }

    override fun getArgument(): String? {
        throw UnsupportedOperationException()
    }

    val navigationController: UINavigationController
        get() = UIApplication.getSharedApplication().windows[0].rootViewController as UINavigationController
}