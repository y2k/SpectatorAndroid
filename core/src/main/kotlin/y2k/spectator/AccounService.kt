package y2k.spectator

import rx.Observable
import java.util.concurrent.TimeUnit

/**
 * Created by y2k on 1/2/16.
 */
class AccounService {

    fun login(code: String): Observable<Unit> {
        return Observable.just(Unit).delay(2, TimeUnit.SECONDS)
    }
}