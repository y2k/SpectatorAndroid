package y2k.spectator

import android.app.Application
import android.os.Handler
import rx.Scheduler
import rx.schedulers.Schedulers

/**
 * Created by y2k on 1/2/16.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        ServiceLocator.platform = object : ServiceLocator.Platform {

            private val handler = Handler()
            private val scheduler = Schedulers.from { handler.post(it) }
            private val navigationService = AndroidNavigationService(this@App)

            override fun resolveScheduler(): Scheduler {
                return scheduler
            }

            override fun resolveNavigationService(): NavigationService {
                return navigationService
            }
        }
    }
}