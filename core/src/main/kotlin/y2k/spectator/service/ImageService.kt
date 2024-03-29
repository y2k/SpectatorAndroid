package y2k.spectator.service

import rx.Observable
import rx.Scheduler
import rx.schedulers.Schedulers
import y2k.spectator.model.Image

/**
 * Created by y2k on 1/5/16.
 */
class ImageService<T>(
    private val api: Api,
    private val uiScheduler: Scheduler,
    private val decoder: Decoder) {

    fun get(image: Image): Observable<T> {
        if (image.isDefault) return Observable.empty()

        println("IMAGE-SERVICE: $image")

        return api
            .images(image.id, image.width, image.height)
            .map { it.bytes() }
            .map { decode(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(uiScheduler)
    }

    @Suppress("unchecked_cast")
    private fun decode(bytes: ByteArray) = decoder.decode(bytes) as T

    interface Decoder {

        fun decode(data: ByteArray): Any
    }
}