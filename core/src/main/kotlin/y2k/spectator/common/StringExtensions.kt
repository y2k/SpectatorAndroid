package y2k.spectator.common

/**
 * Created by y2k on 1/2/16.
 */

fun String?.findGroup(regex: String, group: Int = 1): String? {
    return this?.let { Regex(regex).find(it) }?.groups?.get(group)?.value
}