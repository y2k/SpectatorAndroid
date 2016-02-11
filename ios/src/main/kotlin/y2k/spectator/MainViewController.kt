package y2k.spectator

import org.robovm.apple.foundation.NSIndexPath
import org.robovm.apple.uikit.*
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBAction
import org.robovm.objc.annotation.IBOutlet
import y2k.spectator.common.SideMenu
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

        val sideMenu = SideMenu(this, "Menu")
        sideMenu.attach()

        val viewModel = ServiceLocator.resolve(SnapshotsViewModel::class)

        val dataSource = SnapshotsDataSource(list)
        list.dataSource = dataSource

        viewModel.snapshots.subject.subscribe {
            dataSource.update(it)
        }
    }

    class SnapshotsDataSource(
        private val tableView: UITableView) : UITableViewDataSourceAdapter() {

        protected val items = ArrayList<Snapshot>()
        val imageService = ServiceLocator.resolveImageService<UIImage>()

        override fun getCellForRow(tableView: UITableView, indexPath: NSIndexPath): UITableViewCell? {
            val cell = tableView.dequeueReusableCell("Snapshot", indexPath) as SnapshotViewCell
            val i = items[indexPath.row]

            cell.title.text = i.title
            cell.date.text = "" + i.updated

            imageService
                .get(i.image.normalize(cell.image.frame.width.toInt(), cell.image.frame.height.toInt()))
                .subscribe { cell.image.image = it }

            return cell
        }

        override fun getNumberOfRowsInSection(tableView: UITableView?, section: Long): Long {
            return items.size.toLong()
        }

        fun update(items: List<Snapshot>) {
            this.items.clear()
            this.items.addAll(items)
            tableView.reloadData()
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