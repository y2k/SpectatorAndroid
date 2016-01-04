package y2k.spectator.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import kotlin.properties.Delegates

/**
 * Created by y2k on 1/5/16.
 */
class FixedAspectPanel(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    var aspect by Delegates.observable(1f, {
        prop, old, new ->
        if (old != new) requestLayout()
    })

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var w: Int
        var h: Int

        if (MeasureSpec.getSize(widthMeasureSpec) != 0) {
            w = MeasureSpec.getSize(widthMeasureSpec)
            h = (w / aspect).toInt()
        } else if (MeasureSpec.getSize(heightMeasureSpec) != 0) {
            h = MeasureSpec.getSize(heightMeasureSpec)
            w = (h * aspect).toInt()
        } else {
            throw  IllegalStateException()
        }
        setMeasuredDimension(w, h)

        for (i in 0..childCount - 1)
            getChildAt(i).measure(
                MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0..childCount - 1)
            getChildAt(i).layout(l, t, r, b)
    }
}