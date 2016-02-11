package y2k.spectator

import org.robovm.apple.uikit.*
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBAction
import org.robovm.objc.annotation.IBOutlet
import y2k.spectator.common.*
import y2k.spectator.model.Snapshot
import y2k.spectator.presenter.SnapshotsViewModel

/**
 * Created by y2k on 10/02/16.
 */
@CustomClass("MainViewController")
class MainViewController : UIViewController() {

    @IBOutlet lateinit var list: UITableView
    @IBOutlet lateinit var loginButton: UIButton

    override fun viewDidLoad() {
        super.viewDidLoad()
        val sideMenu = SideMenu(this, "Menu"); sideMenu.attach()

        ServiceLocator
            .resolve(SnapshotsViewModel::class)
            .apply {
                list.bind<Snapshot, SnapshotViewCell>(snapshots)
                loginButton.bind(isNeedLogin)
                loginButton.onClick { login() }
            }
    }

    @CustomClass("SnapshotViewCell")
    class SnapshotViewCell : ListCell<Snapshot>() {

        @IBOutlet lateinit var title: UILabel
        @IBOutlet lateinit var date: UILabel
        @IBOutlet lateinit var image: UIImageView

        @IBAction fun action() {
            // TODO:
        }

        override fun bind(data: Snapshot) {
            title.text = data.title
            date.text = data.updated.formatDate()
            image.bind(data.image)
        }
    }
}