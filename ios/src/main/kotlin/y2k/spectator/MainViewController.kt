package y2k.spectator

import org.robovm.apple.uikit.UIButton
import org.robovm.apple.uikit.UITableView
import org.robovm.apple.uikit.UIViewController
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.spectator.common.SideMenu

/**
 * Created by y2k on 10/02/16.
 */
@CustomClass("MainViewController")
class MainViewController : UIViewController() {

    @IBOutlet lateinit var list: UITableView
    @IBOutlet lateinit var loginButton: UIButton

    override fun viewDidLoad() {
        super.viewDidLoad()

        val sideMenu = SideMenu(this, "Menu")
        sideMenu.attach()
    }
}