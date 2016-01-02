package y2k.spectator

import android.os.Handler
import rx.Scheduler
import rx.schedulers.Schedulers

/**
 * Created by y2k on 1/2/16.
 */
object ServiceLocator : BaseServiceLocator() {

    private val handler = Handler()
    private val scheduler = Schedulers.from { handler.post(it) }

    override fun resolveScheduler(): Scheduler {
        return scheduler
    }

    override fun resolveNavigationService(): NavigationService {
        return StubNavigationService()
    }

    private class StubNavigationService : NavigationService {

        override fun navigateToMain() {
            // TODO:
        }
    }
}