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

/**
 * Created by y2k on 1/2/16.
 */
class SnapshotListFragment : Fragment() {

    lateinit var presenter: SnapshotsPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_snapshots, container, false)

        presenter = ServiceLocator.resolveSnapshotsPresenter(
                object : SnapshotsPresenter.View {

                    val login = view.findViewById(R.id.login)
                    val list = view.findViewById(R.id.list) as RecyclerView
                    val adapter = Adapter()

                    init {
                        view.findViewById(R.id.add).setOnClickListener { presenter.add() }
                        list.layoutManager = LinearLayoutManager(activity)
                        list.adapter = adapter
                        login.setOnClickListener { presenter.login() }
                    }

                    override fun update(snapshots: List<Snapshot>) {
                        adapter.items = snapshots
                        adapter.notifyDataSetChanged()
                    }

                    override fun setLoginButton(visible: Boolean) {
                        login.visibility = if (visible) View.VISIBLE else View.GONE
                    }
                })

        return view
    }

    class Adapter : RecyclerView.Adapter<Adapter.VH>() {

        var items: List<Snapshot> = emptyList()

        override fun onBindViewHolder(vh: VH, position: Int) {
            items[position].apply {
                (vh.itemView.findViewById(android.R.id.text1) as TextView).text = title
                (vh.itemView.findViewById(android.R.id.text2) as TextView).text = source
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH? {
            return VH(parent.inflate(android.R.layout.simple_list_item_2))
        }

        override fun getItemCount(): Int {
            return items.size
        }

        class VH(view: View?) : RecyclerView.ViewHolder(view)
    }
}