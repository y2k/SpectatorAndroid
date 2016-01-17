package y2k.spectator.common

/**
 * Created by y2k on 1/18/16.
 */

fun <T> List<T>.peek(operation: (T) -> Unit): List<T> {
    forEach(operation)
    return this
}