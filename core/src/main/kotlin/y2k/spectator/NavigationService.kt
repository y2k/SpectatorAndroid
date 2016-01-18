package y2k.spectator

/**
 * Created by y2k on 1/2/16.
 */
interface NavigationService {

    fun openMain()

    fun openLogin()

    fun openAddSubscription()

    fun <T> open(presenter: Class<T>, arg: String? = null)

    fun getArgument(): String?
}