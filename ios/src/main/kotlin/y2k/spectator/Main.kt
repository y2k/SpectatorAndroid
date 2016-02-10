package y2k.spectator

import org.robovm.apple.dispatch.DispatchQueue
import org.robovm.apple.foundation.NSAutoreleasePool
import org.robovm.apple.uikit.UIApplication
import org.robovm.apple.uikit.UIApplicationDelegateAdapter
import org.robovm.apple.uikit.UIApplicationLaunchOptions
import rx.Scheduler
import rx.schedulers.Schedulers
import y2k.spectator.common.StoryboardNavigationService
import y2k.spectator.service.ImageService
import y2k.spectator.service.NavigationService

class Main : UIApplicationDelegateAdapter() {

    override fun didFinishLaunching(application: UIApplication?, launchOptions: UIApplicationLaunchOptions?): Boolean {
        ServiceLocator.platform = object : ServiceLocator.Platform {
            override val decoder: ImageService.Decoder
                get() = throw UnsupportedOperationException()
            override val uiScheduler: Scheduler
                get() = Schedulers.from { DispatchQueue.getMainQueue().async(it) }
            override val navigationService: NavigationService
                get() = StoryboardNavigationService()
        }
        return true
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val pool = NSAutoreleasePool()
            UIApplication.main<UIApplication, Main>(args, null, Main::class.java)
            pool.release()
        }
    }
}