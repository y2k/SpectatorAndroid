package y2k.spectator

import org.robovm.apple.uikit.UIButton
import org.robovm.apple.uikit.UITableView
import org.robovm.apple.uikit.UIViewController
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet

/**
 * Created by y2k on 10/02/16.
 */
@CustomClass("MenuViewController")
class MenuViewController : UIViewController() {

    @IBOutlet lateinit var closeButton: UIButton
    @IBOutlet lateinit var logoutButton: UIButton
    @IBOutlet lateinit var subscriptionList: UITableView
}