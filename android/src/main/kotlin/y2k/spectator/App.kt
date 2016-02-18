package y2k.spectator

import android.app.Application
import android.os.Handler
import rx.Scheduler
import rx.schedulers.Schedulers
import y2k.spectator.platform.BitmapImageDecoder
import y2k.spectator.platform.SharedPreferencesCookieStorage
import y2k.spectator.service.ImageService
import y2k.spectator.service.NavigationService
import y2k.spectator.service.RestClient

/**
 * Created by y2k on 1/2/16.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val handler = Handler()
        ServiceLocator.let {
            it.register(RestClient.CookieStorage::class, SharedPreferencesCookieStorage(this))
            it.register(ImageService.Decoder::class, BitmapImageDecoder())
            it.register(Scheduler::class, Schedulers.from { handler.post(it) })
            it.register(NavigationService::class, AndroidNavigationService(this))
        }
    }
}