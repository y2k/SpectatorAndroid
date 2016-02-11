package y2k.spectator.platform

import android.content.Context
import y2k.spectator.service.RestClient

/**
 * Created by y2k on 2/11/16.
 */
class SharedPreferencesCookieStorage(context: Context) : RestClient.CookieStorage {

    private val pref = context.getSharedPreferences("cookie", 0)

    override fun put(cookies: Set<String>) {
        pref.edit().putStringSet("cookie", cookies).apply()
    }

    override fun getAll(): Set<String> {
        return pref.getStringSet("cookie", emptySet())
    }
}