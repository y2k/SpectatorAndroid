package y2k.spectator

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.webkit.WebView
import android.widget.TextView
import y2k.spectator.common.ViewPagerWrapper
import y2k.spectator.common.addOnPageChangeListener
import y2k.spectator.common.bind
import y2k.spectator.presenter.SnapshotInfoViewModel

/**
 * Created by y2k on 1/17/16.
 */
class SnapshotActivity : AppCompatActivity() {

    lateinit var viewModel: SnapshotInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = initializeContentView()

        val titleView = view.findViewById(R.id.title) as TextView
        val updatedView = view.findViewById(R.id.updated)as TextView

        viewModel = ServiceLocator.resolve(SnapshotInfoViewModel::class)

        (view.findViewById(R.id.contentView) as WebView).bind(viewModel.contentUrl)
        (view.findViewById(R.id.diffView) as WebView).bind(viewModel.diffUrl)

        viewModel.info.subject.subscribe {
            titleView.text = it.title
            updatedView.text = "Created: ${it.updated}"
        }
    }

    private fun initializeContentView(): ViewPagerWrapper {
        setContentView(R.layout.activity_snapshot)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        var wrapper = ViewPagerWrapper(findViewById(R.id.pager) as ViewPager)
        (findViewById(R.id.tabs) as TabLayout).setupWithViewPager(wrapper.pager)
        wrapper.pager.addOnPageChangeListener { viewModel.tabSelected(it) }

        return wrapper
    }
}