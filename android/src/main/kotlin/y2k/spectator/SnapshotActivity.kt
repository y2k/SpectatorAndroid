package y2k.spectator

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView
import y2k.spectator.binding.bindingBuilder
import y2k.spectator.common.ViewPagerWrapper
import y2k.spectator.common.find
import y2k.spectator.viewmodel.SnapshotInfoViewModel

/**
 * Created by y2k on 1/17/16.
 */
class SnapshotActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = initializeContentView()
        val vm = ServiceLocator.resolve(SnapshotInfoViewModel::class)

        val titleView = view.find<TextView>(R.id.title)
        val updatedView = view.find<TextView>(R.id.updated)
        bindingBuilder(view) {
            webView(R.id.contentView, vm.contentUrl)
            webView(R.id.diffView, vm.diffUrl)
            viewPager(view.pager, vm.page)
            action(vm.info) {
                titleView.text = it.title
                updatedView.text = "Created: ${it.updated}"
            }
        }
    }

    private fun initializeContentView(): ViewPagerWrapper {
        setContentView(R.layout.activity_snapshot)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        var wrapper = ViewPagerWrapper(findViewById(R.id.pager) as ViewPager)
        find<TabLayout>(R.id.tabs).setupWithViewPager(wrapper.pager)
        return wrapper
    }
}