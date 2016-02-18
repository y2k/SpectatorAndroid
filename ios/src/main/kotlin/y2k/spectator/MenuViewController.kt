package y2k.spectator

import org.robovm.apple.uikit.*
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBAction
import org.robovm.objc.annotation.IBOutlet
import y2k.spectator.common.ListCell
import y2k.spectator.common.bind
import y2k.spectator.common.onClick
import y2k.spectator.model.Subscription
import y2k.spectator.viewmodel.SubscriptionsViewModel

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

        ServiceLocator
            .resolve(SubscriptionsViewModel::class)
            .apply {
                subscriptionList.bind(subscriptions)
                logoutButton.onClick { logout() }
            }
    }

    @CustomClass("SubscriptionCell")
    class SubscriptionCell : ListCell<Subscription>() {

        @IBOutlet lateinit var title: UILabel
        @IBOutlet lateinit var count: UILabel
        @IBOutlet lateinit var image: UIImageView

        override fun bind(data: Subscription) {
            title.bind(data.title!!)
            count.bind(data.unreadCount.toString())
            image.bind(data.image)
        }

        @IBAction fun actoinSelect() {
            // TODO:
        }
    }
}