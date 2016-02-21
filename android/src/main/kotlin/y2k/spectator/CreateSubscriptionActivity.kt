package y2k.spectator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import y2k.spectator.common.*
import y2k.spectator.model.Subscription
import y2k.spectator.viewmodel.CreateSubscriptionViewModel

/**
 * Created by y2k on 1/3/16.
 */
class CreateSubscriptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_subscription)
        ServiceLocator
            .resolve(CreateSubscriptionViewModel::class)
            .apply {
                command(R.id.analyze) { analyze() }
                editText(R.id.link, link)

                bind(R.id.analyze, isBusy, true)
                loadingProgress(R.id.progress, isBusy)

                recyclerView(R.id.list, rssItems) {
                    viewHolder {
                        VH(it.inflate(android.R.layout.simple_list_item_2))
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