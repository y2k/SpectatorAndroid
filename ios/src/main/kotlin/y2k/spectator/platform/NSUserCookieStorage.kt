package y2k.spectator.platform

import org.robovm.apple.foundation.NSUserDefaults
import y2k.spectator.service.RestClient

/**
 * Created by y2k on 2/11/16.
 */
class NSUserCookieStorage : RestClient.CookieStorage {

    override fun put(cookies: Set<String>) {
        preferences.put("cookie", cookies.toList())
        preferences.synchronize()
    }

    override fun getAll(): Set<String> {
        return preferences.getStringArray("cookie")?.toSet() ?: emptySet()
    }

    private val preferences: NSUserDefaults
        get() = NSUserDefaults.getStandardUserDefaults()
}