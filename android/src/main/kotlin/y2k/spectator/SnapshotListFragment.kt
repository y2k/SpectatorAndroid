package y2k.spectator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import y2k.spectator.common.*
import y2k.spectator.model.Snapshot
import y2k.spectator.viewmodel.SnapshotsViewModel
import y2k.spectator.widget.FixedAspectPanel
import y2k.spectator.widget.WebImageView

/**
 * Created by y2k on 1/2/16.
 */
class SnapshotListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewModel = ServiceLocator.resolve(SnapshotsViewModel::class)
        return inflater
            .inflate(R.layout.fragment_snapshots, container, false)
            .command(R.id.add) { viewModel.add() }
            .find<View>(R.id.login) {
                bind(viewModel.isNeedLogin)
                command { viewModel.login() }
            }
            .find<RecyclerView>(R.id.list) {
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                bind(viewModel.snapshots) {
                    onGetItemId { it.id.toLong() }
                    onCreateViewHolder { parent ->
                        VH(parent.inflate(R.layout.item_snapshot)).apply {
                            itemView.command(R.id.card) { viewModel.openSnapshot(adapterPosition) }
                        }
                    }
                }
            }
    }

    class VH(view: View) : ListViewHolder<Snapshot>(view) {

        val aspectPanel = view.findViewById(R.id.aspectPanel) as FixedAspectPanel
        val image = view.findViewById(R.id.image) as WebImageView
        val title = view.findViewById(android.R.id.text1) as TextView

        override fun update(item: Snapshot) {
            val aspect = item.thumbnailWidth.toFloat() / item.thumbnailHeight
            aspectPanel.aspect = aspect
            image.image = item.image
            title.text = item.title
        }
    }
}