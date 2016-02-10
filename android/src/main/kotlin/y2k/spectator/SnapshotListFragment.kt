package y2k.spectator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import y2k.spectator.common.ListAdapter
import y2k.spectator.common.bind
import y2k.spectator.common.find
import y2k.spectator.common.inflate
import y2k.spectator.model.Snapshot
import y2k.spectator.widget.FixedAspectPanel
import y2k.spectator.widget.WebImageView

/**
 * Created by y2k on 1/2/16.
 */
class SnapshotListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_snapshots, container, false)
        val presenter = ServiceLocator.resolveSnapshotsPresenter()

        view.findViewById(R.id.add).bind { presenter.add() }
        view.findViewById(R.id.login).apply {
            bind(presenter.isNeedLogin)
            bind { presenter.login() }
        }
        view.find<RecyclerView>(R.id.list).apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = Adapter().apply {
                bind(presenter.snapshots)
                clickListener = { presenter.openSnapshot(it) }
            }
        }
        return view
    }

    class Adapter : ListAdapter<Snapshot, Adapter.VH>() {

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

        override fun getItemId(position: Int): Long {
            return items[position].id.toLong()
        }

        class VH(view: View) : RecyclerView.ViewHolder(view) {

            val aspectPanel = view.findViewById(R.id.aspectPanel) as FixedAspectPanel
            val image = view.findViewById(R.id.image) as WebImageView
            val title = view.findViewById(android.R.id.text1) as TextView
        }
    }
}