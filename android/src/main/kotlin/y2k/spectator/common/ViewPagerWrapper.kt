package y2k.spectator.common

import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import y2k.spectator.R

/**
 * Created by y2k on 1/18/16.
 */
class ViewPagerWrapper(val pager: ViewPager) {

    private var items: List<View>

    init {
        items = (pager.inflate(R.layout.layout_snapshot) as ViewGroup)
            .children()
            .peek { it.removeFromParent() }

        pager.adapter = object : PagerAdapter() {

            override fun getPageTitle(position: Int): CharSequence? {
                return arrayOf("Details", "Web Page", "Difference")[position]
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any? {
                container.addView(items[position])
                return items[position]
            }

            override fun destroyItem(container: ViewGroup, position: Int, state: Any?) {
                container.removeView(items[position])
            }

            override fun isViewFromObject(view: View?, state: Any?): Boolean {
                return view == state
            }

            override fun getCount(): Int {
                return items.size
            }
        }
    }

    fun findViewById(id: Int): View? {
        return items.map { it.findViewById(id) }.firstOrNull { it != null }
    }
}