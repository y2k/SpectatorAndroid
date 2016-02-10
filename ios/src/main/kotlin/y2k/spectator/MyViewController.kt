package y2k.spectator

import org.robovm.apple.uikit.UILabel
import org.robovm.apple.uikit.UIViewController
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBAction
import org.robovm.objc.annotation.IBOutlet

@CustomClass("MyViewController")
class MyViewController : UIViewController() {

    @IBOutlet
    private val label: UILabel? = null

    @IBAction
    private fun clicked() {
    	// TODO:
    }
}

