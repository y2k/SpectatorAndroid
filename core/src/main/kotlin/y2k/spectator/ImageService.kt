package y2k.spectator

import rx.Observable
import rx.Scheduler
import rx.schedulers.Schedulers

/**
 * Created by y2k on 1/5/16.
 */
class ImageService<T>(
    private val api: Api,
    private val uiScheduler: Scheduler,
    private val decoder: ImageService.Decoder) {

    fun get(id: Int?, width: Int, height: Int): Observable<T> {
        if (id == null) return Observable.empty()

        println("IMAGE-SERVICE: $id | $width | $height")

        return api
            .images(id, width, height)
            .map { it.bytes() }
            .map { decoder.decode(it) as T }
            .subscribeOn(Schedulers.io())
            .observeOn(uiScheduler)
    }

    interface Decoder {

        fun decode(data: ByteArray): Any
    }
}