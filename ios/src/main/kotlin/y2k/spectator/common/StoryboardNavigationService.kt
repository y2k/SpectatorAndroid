package y2k.spectator.common

import y2k.spectator.service.NavigationService

/**
 * Created by y2k on 2/10/16.
 */
class StoryboardNavigationService : NavigationService {
    override fun openMain() {
        throw UnsupportedOperationException()
    }

    override fun openLogin() {
        throw UnsupportedOperationException()
    }

    override fun openAddSubscription() {
        throw UnsupportedOperationException()
    }

    override fun <T> open(presenter: Class<T>, arg: String?) {
        throw UnsupportedOperationException()
    }

    override fun getArgument(): String? {
        throw UnsupportedOperationException()
    }
}