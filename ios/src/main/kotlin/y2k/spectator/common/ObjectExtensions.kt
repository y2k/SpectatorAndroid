package y2k.spectator.common

import org.ocpsoft.prettytime.PrettyTime
import java.util.*

/**
 * Created by y2k on 2/11/16.
 */

private val prettyTime = PrettyTime()

fun Long.formatDate(): String {
    return prettyTime.format(Date(this))
}