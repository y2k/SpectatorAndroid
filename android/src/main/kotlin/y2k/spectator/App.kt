package y2k.spectator

import android.app.Application

/**
 * Created by y2k on 1/2/16.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        ServiceLocator.instance = ServiceLocator(this)
    }
}