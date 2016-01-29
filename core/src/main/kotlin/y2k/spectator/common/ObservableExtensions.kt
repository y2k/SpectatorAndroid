package y2k.spectator.common

import rx.Observable
import rx.Scheduler
import rx.schedulers.Schedulers

/**
 * Created by y2k on 1/28/16.
 */

fun <T> Observable<T>.subscribe(scheduler: Scheduler, onNext: (T) -> Unit) {
    this.subscribeOn(Schedulers.io())
        .observeOn(scheduler)
        .subscribe(onNext)
}

fun <T> Observable<T>.subscribe(scheduler: Scheduler, onNext: (T) -> Unit, onError: (Throwable) -> Unit) {
    this.subscribeOn(Schedulers.io())
        .observeOn(scheduler)
        .subscribe(onNext, onError)
}