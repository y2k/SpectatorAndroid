package y2k.spectator.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.ImageView
import rx.subjects.PublishSubject
import y2k.spectator.Image
import y2k.spectator.ServiceLocator
import kotlin.properties.Delegates

/**
 * Created by y2k on 1/5/16.
 */
class WebImageView(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs) {

    private val imageEvent = PublishSubject.create<Image>()
    private val imageService = ServiceLocator.resolveImageService<Bitmap>()

    var image by Delegates.observable(Image.Default, {
        prop, old, new ->
        if (old != new) imageEvent.onNext(new.let { it.normalize(width, height) })
    })

    init {
        imageEvent
            .filter { it == null || !it.isEmpty }
            .distinctUntilChanged()
            .flatMap { imageService.get(it) }
            .map { it?.let { BitmapDrawable(context?.resources, it) } }
            .subscribe({ setImageDrawable(it) }, { it.printStackTrace() })
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        imageEvent.onNext(image.let { it.normalize(width, height) })
    }
}