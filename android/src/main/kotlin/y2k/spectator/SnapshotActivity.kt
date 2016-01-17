package y2k.spectator

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import y2k.spectator.common.ViewPagerWrapper

/**
 * Created by y2k on 1/17/16.
 */
class SnapshotActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snapshot)

        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        var wrapper = ViewPagerWrapper(findViewById(R.id.pager) as ViewPager)
        (findViewById(R.id.tabs) as TabLayout).setupWithViewPager(wrapper.pager)

        ServiceLocator.resolveSnapshotPresenter(
            object : SnapshotInfoPresenter.View {
                // TODO:
            })
    }
}