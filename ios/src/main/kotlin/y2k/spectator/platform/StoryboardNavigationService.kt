package y2k.spectator.platform

import org.robovm.apple.foundation.NSArray
import org.robovm.apple.uikit.UIApplication
import org.robovm.apple.uikit.UINavigationController
import y2k.spectator.service.NavigationService

/**
 * Created by y2k on 2/10/16.
 */
class StoryboardNavigationService : NavigationService {

    override fun openMain() {
        val vc = navigationController.storyboard.instantiateViewController("Main")
        navigationController.setViewControllers(NSArray(vc), true)
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