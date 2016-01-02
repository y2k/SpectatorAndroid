package y2k.spectator

import android.app.Application
import android.os.Handler
import rx.Scheduler
import rx.schedulers.Schedulers

/**
 * Created by y2k on 1/2/16.
 */
class ServiceLocator(private val app: Application) : BaseServiceLocator() {

    private val handler = Handler()
    private val scheduler = Schedulers.from { handler.post(it) }
    private val navigationService = AndroidNavigationService(app)

    override fun resolveScheduler(): Scheduler {
        return scheduler
    }

    override fun resolveNavigationService(): NavigationService {
        return navigationService
    }

    companion object {

        lateinit var instance: ServiceLocator
    }
}