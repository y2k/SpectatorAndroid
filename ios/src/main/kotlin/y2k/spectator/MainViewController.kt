package y2k.spectator

import org.ocpsoft.prettytime.PrettyTime
import org.robovm.apple.foundation.NSIndexPath
import org.robovm.apple.uikit.*
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBAction
import org.robovm.objc.annotation.IBOutlet
import y2k.spectator.common.ListDataSource
import y2k.spectator.common.SideMenu
import y2k.spectator.common.bind
import y2k.spectator.model.Snapshot
import y2k.spectator.presenter.SnapshotsViewModel
import java.util.*

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
                list.dataSource = SnapshotsDataSource(list).apply { bind(snapshots) }
                loginButton.bind(isNeedLogin)
            }
    }

    class SnapshotsDataSource(tableView: UITableView) : ListDataSource<Snapshot>(tableView) {

        val imageService = ServiceLocator.resolveImageService<UIImage>()
        val prettyTime = PrettyTime()

        override fun getCellForRow(tableView: UITableView, indexPath: NSIndexPath): UITableViewCell? {
            val cell = tableView.dequeueReusableCell("Snapshot", indexPath) as SnapshotViewCell
            val i = items[indexPath.row]

            cell.title.text = i.title
            cell.date.text = prettyTime.format(Date(i.updated))

            imageService
                .get(i.image.normalize(cell.image.frame.width.toInt(), cell.image.frame.height.toInt()))
                .subscribe { cell.image.image = it }

            return cell
        }
    }

    @CustomClass("SnapshotViewCell")
    class SnapshotViewCell : UITableViewCell() {

        @IBOutlet lateinit var title: UILabel
        @IBOutlet lateinit var date: UILabel
        @IBOutlet lateinit var image: UIImageView

        @IBAction fun action() {
            // TODO:
        }
    }
}