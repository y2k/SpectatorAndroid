package y2k.spectator

import org.robovm.apple.foundation.NSIndexPath
import org.robovm.apple.uikit.*
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBAction
import org.robovm.objc.annotation.IBOutlet
import y2k.spectator.common.ListDataSource
import y2k.spectator.common.addOnClick
import y2k.spectator.common.bind
import y2k.spectator.model.Subscription
import y2k.spectator.presenter.SubscriptionsViewModel

/**
 * Created by y2k on 10/02/16.
 */
@CustomClass("MenuViewController")
class MenuViewController : UIViewController() {

    @IBOutlet lateinit var closeButton: UIButton
    @IBOutlet lateinit var logoutButton: UIButton
    @IBOutlet lateinit var subscriptionList: UITableView

    override fun viewDidLoad() {
        super.viewDidLoad()

        ServiceLocator.resolve(SubscriptionsViewModel::class).apply {
            subscriptionList.dataSource = SubscriptionDataSource(subscriptionList).apply { bind(subscriptions) }
            logoutButton.addOnClick { logout() }
        }
    }

    class SubscriptionDataSource(tableView: UITableView) : ListDataSource<Subscription>(tableView) {

        override fun getCellForRow(tableView: UITableView, indexPath: NSIndexPath): UITableViewCell? {
            val cell = tableView.dequeueReusableCell("cell", indexPath) as SubscriptionCell
            val s = items[indexPath.row]
            cell.title.bind(s.title!!)
            cell.count.bind(s.unreadCount.toString())
            cell.image.bind(s.image)
            return cell
        }

        @CustomClass("SubscriptionCell")
        class SubscriptionCell : UITableViewCell() {

            @IBOutlet lateinit var title: UILabel
            @IBOutlet lateinit var count: UILabel
            @IBOutlet lateinit var image: UIImageView

            @IBAction fun actoinSelect() {
                // TODO:
            }
        }
    }
}