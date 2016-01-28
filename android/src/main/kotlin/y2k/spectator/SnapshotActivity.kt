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

/**
 * Created by y2k on 1/17/16.
 */
class SnapshotActivity : AppCompatActivity() {

    lateinit var presenter: SnapshotInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = initializeContentView()

        val titleView = view.findViewById(R.id.title) as TextView
        val updatedView = view.findViewById(R.id.updated)as TextView
        val contentView = view.findViewById(R.id.contentView) as WebView

        presenter = ServiceLocator.resolveSnapshotPresenter(
            object : SnapshotInfoPresenter.View {

                override fun updateBrowser(data: String, baseUrl: String) {
                    contentView.loadDataWithBaseURL(baseUrl, data, null, null, null)
                }

                override fun updateInfo(snapshot: Snapshot) {
                    snapshot.apply {
                        titleView.text = title
                        updatedView.text = "Created: $updated"
                    }
                }
            })
    }

    private fun initializeContentView(): ViewPagerWrapper {
        setContentView(R.layout.activity_snapshot)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        var wrapper = ViewPagerWrapper(findViewById(R.id.pager) as ViewPager)
        (findViewById(R.id.tabs) as TabLayout).setupWithViewPager(wrapper.pager)
        wrapper.pager.addOnPageChangeListener { presenter.tabSelected(it) }

        return wrapper
    }
}