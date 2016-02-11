package y2k.spectator

import android.app.Application
import android.graphics.BitmapFactory
import android.os.Handler
import rx.schedulers.Schedulers
import y2k.spectator.service.ImageService

/**
 * Created by y2k on 1/2/16.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        ServiceLocator.module = object : ServiceLocator.Module {

            private val handler = Handler()
            
            override val decoder = BitmapImageDecoder()
            override val uiScheduler = Schedulers.from { handler.post(it) }
            override val navigationService = AndroidNavigationService(this@App)
        }
    }

    class BitmapImageDecoder : ImageService.Decoder {

        override fun decode(data: ByteArray): Any {
            return BitmapFactory.decodeByteArray(data, 0, data.size)
        }
    }
}