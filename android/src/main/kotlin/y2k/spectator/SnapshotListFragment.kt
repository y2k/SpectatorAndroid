package y2k.spectator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import y2k.spectator.binding.ListViewHolder
import y2k.spectator.binding.bindingBuilder
import y2k.spectator.binding.command
import y2k.spectator.common.inflate
import y2k.spectator.model.Snapshot
import y2k.spectator.viewmodel.SnapshotsViewModel
import y2k.spectator.widget.FixedAspectPanel
import y2k.spectator.widget.WebImageView

/**
 * Created by y2k on 1/2/16.
 */
class SnapshotListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_snapshots, container, false)
        val vm = ServiceLocator.resolve(SnapshotsViewModel::class)
        return bindingBuilder(view) {
            click(R.id.add, { vm.add() })
            view(R.id.login) {
                visibility(vm.isNeedLogin)
                click({ vm.login() })
            }
            recyclerView(R.id.list, vm.snapshots) {
                itemId { it.id.toLong() }
                viewHolder {
                    VH(it.inflate(R.layout.item_snapshot)).apply {
                        itemView.command(R.id.card) { vm.openSnapshot(adapterPosition) }
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