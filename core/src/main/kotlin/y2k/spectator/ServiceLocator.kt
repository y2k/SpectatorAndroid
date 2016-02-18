package y2k.spectator

import rx.Scheduler
import y2k.spectator.viewmodel.*
import y2k.spectator.service.Api
import y2k.spectator.service.ImageService
import y2k.spectator.service.NavigationService
import y2k.spectator.service.RestClient
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by y2k on 1/2/16.
 */
object ServiceLocator {

    private val types = HashMap<KClass<*>, () -> Any>()

    init {
        register(RestClient::class) { RestClient(resolve(RestClient.CookieStorage::class)) }
        register(Api::class) { resolve(RestClient::class).api }
        register(SnapshotInfoViewModel::class) {
            SnapshotInfoViewModel(resolve(Api::class), resolve(NavigationService::class), resolve(Scheduler::class))
        }
        register(SubscriptionsViewModel::class) {
            SubscriptionsViewModel(resolve(RestClient::class), resolve(Scheduler::class))
        }
        register(SnapshotsViewModel::class) {
            SnapshotsViewModel(resolve(Api::class), resolve(NavigationService::class), resolve(Scheduler::class))
        }
        register(LoginViewModel::class) {
            LoginViewModel(resolve(NavigationService::class), resolve(Scheduler::class), resolve(Api::class))
        }
        register(CreateSubscriptionViewModel::class) {
            CreateSubscriptionViewModel(resolve(Api::class), resolve(Scheduler::class))
        }
    }

    fun <T : Any> register(type: KClass<T>, singleton: T) {
        types[type] = { singleton }
    }

    fun <T : Any> register(type: KClass<T>, func: () -> T) {
        types[type] = func
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> resolve(type: KClass<T>): T = types[type]!!() as T

    // TODO: Убрать метод
    fun <T> resolveImageService(): ImageService<T> {
        return ImageService(resolve(Api::class), resolve(Scheduler::class), resolve(ImageService.Decoder::class))
    }
}