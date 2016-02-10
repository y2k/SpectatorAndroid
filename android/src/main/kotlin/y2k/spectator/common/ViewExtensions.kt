package y2k.spectator.common

import android.app.Activity
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by y2k on 1/3/16.
 */

fun ViewGroup.inflate(layoutId: Int): View {
    return LayoutInflater.from(context).inflate(layoutId, this, false)
}

fun ViewGroup.children(): List<View> {
    return (0..childCount - 1).map { getChildAt(it) }
}

fun View.removeFromParent() {
    (parent as ViewGroup).removeView(this)
}

fun ViewPager.addOnPageChangeListener(callback: (Int) -> Unit) {
    addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {

        override fun onPageSelected(position: Int) {
            callback(position)
        }
    })
}

@Suppress("UNCHECKED_CAST")
fun <T : View> Activity.find(id: Int): T {
    return findViewById(id) as T
}