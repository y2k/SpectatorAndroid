package y2k.spectator.common

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import java.util.*

/**
 * Created by y2k on 2/10/16.
 */
abstract class ListAdapter<R, T : RecyclerView.ViewHolder>(
    hasStableIds: Boolean = true) : RecyclerView.Adapter<T>() {

    protected val items = ArrayList<R>()

    init {
        setHasStableIds(hasStableIds)
    }

    fun update(items: List<R>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}