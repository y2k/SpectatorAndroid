package y2k.spectator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import y2k.spectator.common.inflate
import y2k.spectator.model.Subscription
import y2k.spectator.presenter.SubscriptionsPresenter

/**
 * Created by y2k on 1/2/16.
 */
class MenuFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        ServiceLocator.resolveSubscriptionsPresenter(
                object : SubscriptionsPresenter.View {

                    val list = view.findViewById(R.id.list) as RecyclerView
                    val adapter = Adapter()

                    init {
                        list.layoutManager = LinearLayoutManager(activity)
                        list.adapter = adapter
                    }

                    override fun updateSubscriptions(subscriptions: List<Subscription>) {
                        adapter.items = subscriptions
                        adapter.notifyDataSetChanged()
                    }
                })

        return view
    }

    class Adapter : RecyclerView.Adapter<Adapter.VH>() {

        var items: List<Subscription> = emptyList()

        init {
            setHasStableIds(true)
        }

        override fun getItemId(position: Int): Long {
            return items[position].id.toLong()
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, position: Int): VH? {
            return VH(parent.inflate(android.R.layout.simple_list_item_2))
        }

        override fun onBindViewHolder(vh: VH, position: Int) {
            items[position].apply {
                (vh.itemView.findViewById(android.R.id.text1) as TextView).text = title
                (vh.itemView.findViewById(android.R.id.text2) as TextView).text = source
            }
        }

        class VH(view: View) : RecyclerView.ViewHolder(view) {
        }
    }
}