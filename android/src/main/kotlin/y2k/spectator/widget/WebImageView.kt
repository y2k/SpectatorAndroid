package y2k.spectator.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import rx.subjects.PublishSubject
import y2k.spectator.ServiceLocator
import kotlin.properties.Delegates

/**
 * Created by y2k on 1/5/16.
 */
class WebImageView(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs) {

    private val eventBus = PublishSubject.create<Image>()
    private val imageService = ServiceLocator.resolveImageService<Bitmap>()

    var image by Delegates.observable(null as Int?, {
        prop, old, new ->
        if (old != new) eventBus.onNext(Image(new, width, height))
    })

    init {
        eventBus
            .filter { it.width >= 0 && it.height >= 0 }
            .distinctUntilChanged()
            .flatMap { imageService.get(it.image, it.width, it.height) }
            .map { it?.let { BitmapDrawable(context?.resources, it) } }
            .subscribe({ setImageDrawable(it) }, { it.printStackTrace() })
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        Log.i("WEB_IMAGE_VIEW", "onLayout($changed, $left, $top, $right, $bottom)")

        eventBus.onNext(Image(image, width, height))
    }

    data class Image(val image: Int?, val width: Int, val height: Int)
}