package y2k.spectator

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by y2k on 1/17/16.
 */
class SnapshotActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)

        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        var pager = findViewById(R.id.pager) as ViewPager
        pager.adapter = Adapter()

        var tabs = findViewById(R.id.tabs) as TabLayout
        tabs.setTabsFromPagerAdapter(pager.adapter)
        pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs));
    }

    class InfoFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return View(activity)
        }
    }

    inner class Adapter() : FragmentPagerAdapter(supportFragmentManager) {

        override fun getItem(position: Int): Fragment? {
            return when (position) {
                else -> InfoFragment()
            }
        }

        override fun getPageTitle(position: Int): String {
            return arrayOf("Details", "Web Page", "Difference")[position]
        }

        override fun getCount(): Int {
            return 3
        }
    }
}