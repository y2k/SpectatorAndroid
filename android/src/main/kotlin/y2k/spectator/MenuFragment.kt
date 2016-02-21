package y2k.spectator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import y2k.spectator.binding.ListViewHolder
import y2k.spectator.binding.bindingBuilder
import y2k.spectator.common.find
import y2k.spectator.common.inflate
import y2k.spectator.model.Subscription
import y2k.spectator.viewmodel.SubscriptionsViewModel

/**
 * Created by y2k on 1/2/16.
 */
class MenuFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_menu, container, false)
        val vm = ServiceLocator.resolve(SubscriptionsViewModel::class)
        return bindingBuilder(view) {
            recyclerView(R.id.list, vm.subscriptions) {
                itemId { it.id.toLong() }
                viewHolder { VH(it.inflate(android.R.layout.simple_list_item_2)) }
            }
        }
    }

    class VH(view: View) : ListViewHolder<Subscription>(view) {

        val titleView = view.find<TextView>(android.R.id.text1)
        val linkView = view.find<TextView>(android.R.id.text2)

        override fun update(item: Subscription) {
            titleView.text = item.title
            linkView.text = item.source
        }
    }
}