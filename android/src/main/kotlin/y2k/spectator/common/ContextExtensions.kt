package y2k.spectator.common

import android.content.Context
import android.content.Intent
import kotlin.reflect.KClass

/**
 * Created by y2k on 2/18/16.
 */

fun <T : Any> Context.startActivity(type: KClass<T>, f: Intent.() -> Unit) {
    val intent = Intent(this, type.java)
    intent.f()
    startActivity(intent)
}

fun <T : Any> Context.startActivity(type: KClass<T>) {
    startActivity(Intent(this, type.java))
}