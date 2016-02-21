package y2k.spectator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import y2k.spectator.binding.ListViewHolder
import y2k.spectator.binding.bindingBuilder
import y2k.spectator.binding.command
import y2k.spectator.common.find
import y2k.spectator.common.inflate
import y2k.spectator.model.Subscription
import y2k.spectator.viewmodel.CreateSubscriptionViewModel

/**
 * Created by y2k on 1/3/16.
 */
class CreateSubscriptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_subscription)

        val vm = ServiceLocator.resolve(CreateSubscriptionViewModel::class)
        bindingBuilder(this) {
            visibility(R.id.analyze, vm.isBusy, true)
            visibility(R.id.list, vm.isBusy, true)
            editText(R.id.link, vm.link)
            loadingProgress(R.id.progress, vm.isBusy)
            recyclerView(R.id.list, vm.rssItems) {
                viewHolder {
                    VH(it.inflate(android.R.layout.simple_list_item_2)).apply {
                        itemView.command { vm.create(adapterPosition) }
                    }
                }
            }
        }
    }

    class VH(view: View) : ListViewHolder<Subscription>(view) {

        val title = view.find<TextView>(android.R.id.text1)
        val link = view.find<TextView>(android.R.id.text2)

        override fun update(item: Subscription) {
            title.text = item.title
            link.text = item.source
        }
    }
}