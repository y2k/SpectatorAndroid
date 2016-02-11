package y2k.spectator.common

import org.robovm.apple.foundation.NSIndexPath
import org.robovm.apple.uikit.UITableView
import org.robovm.apple.uikit.UITableViewCell
import org.robovm.apple.uikit.UITableViewDataSourceAdapter
import java.util.*

/**
 * Created by y2k on 2/11/16.
 */
abstract class ListDataSource<T>(private val tableView: UITableView) : UITableViewDataSourceAdapter() {

    protected val items = ArrayList<T>()

    override fun getNumberOfRowsInSection(tableView: UITableView?, section: Long): Long {
        return items.size.toLong()
    }

    fun update(items: List<T>) {
        this.items.clear()
        this.items.addAll(items)
        tableView.reloadData()
    }

    class Default<T, TC : ListCell<T>>(tableView: UITableView) : ListDataSource<T>(tableView) {

        @Suppress("UNCHECKED_CAST")
        override fun getCellForRow(tableView: UITableView, indexPath: NSIndexPath): UITableViewCell? {
            val cell = tableView.dequeueReusableCell("cell", indexPath) as TC
            return cell.apply { bind(items[indexPath.row]) }
        }
    }
}