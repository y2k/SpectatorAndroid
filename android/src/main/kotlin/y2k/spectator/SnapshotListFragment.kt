package y2k.spectator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import y2k.spectator.common.inflate
import y2k.spectator.model.Snapshot
import y2k.spectator.presenter.SnapshotsPresenter
import y2k.spectator.widget.FixedAspectPanel
import y2k.spectator.widget.WebImageView

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
                    list.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    adapter.clickListener = { presenter.openSnapshot(it) }
                    list.adapter = adapter
                    view.findViewById(R.id.add).setOnClickListener { presenter.add() }
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
        var clickListener: ((Snapshot) -> Unit)? = null

        override fun onBindViewHolder(vh: VH, position: Int) {
            items[position].apply {
                val aspect = thumbnailWidth.toFloat() / thumbnailHeight
                vh.aspectPanel.aspect = aspect
                vh.image.image = image
                vh.title.text = title
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH? {
            return VH(parent.inflate(R.layout.item_snapshot)).apply {
                itemView.findViewById(R.id.card).setOnClickListener {
                    clickListener?.invoke(items[adapterPosition])
                }
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }

        class VH(view: View) : RecyclerView.ViewHolder(view) {

            val aspectPanel = view.findViewById(R.id.aspectPanel) as FixedAspectPanel
            val image = view.findViewById(R.id.image) as WebImageView
            val title = view.findViewById(android.R.id.text1) as TextView
        }
    }
}