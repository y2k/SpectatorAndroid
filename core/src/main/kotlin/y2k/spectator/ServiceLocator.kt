package y2k.spectator

import rx.Scheduler
import y2k.spectator.presenter.*
import y2k.spectator.service.ImageService
import y2k.spectator.service.NavigationService
import y2k.spectator.service.RestClient
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by y2k on 1/2/16.
 */
object ServiceLocator {

    lateinit var platform: Platform
    private val restClient = RestClient(TestCookeStorage())
    private val types = HashMap<KClass<*>, () -> Any>()

    init {
        register(SnapshotInfoViewModel::class) {
            SnapshotInfoViewModel(restClient.api, platform.navigationService, platform.uiScheduler)
        }
        register(SubscriptionsViewModel::class) {
            SubscriptionsViewModel(restClient, platform.uiScheduler)
        }
        register(SnapshotsViewModel::class) {
            SnapshotsViewModel(restClient.api, platform.navigationService, platform.uiScheduler)
        }
        register(LoginViewModel::class) {
            LoginViewModel(platform.navigationService, platform.uiScheduler, restClient.api)
        }
        register(CreateSubscriptionViewModel::class) {
            CreateSubscriptionViewModel(restClient.api, platform.uiScheduler)
        }
    }

    private fun <T : Any> register(type: KClass<T>, func: () -> T) {
        types[type] = func
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> resolve(type: KClass<T>): T = types[type] as T

    fun <T> resolveImageService(): ImageService<T> {
        return ImageService(restClient.api, platform.uiScheduler, platform.decoder)
    }

    interface Platform {

        val decoder: ImageService.Decoder
        val uiScheduler: Scheduler
        val navigationService: NavigationService
    }

    class TestCookeStorage : RestClient.CookieStorage {

        val storage = HashSet<String>()

        override fun getAll(): Set<String> {
            synchronized(storage) { return storage.toSet() }
        }

        override fun put(cookies: HashSet<String>) {
            synchronized(storage) { storage.addAll(cookies) }
        }
    }
}