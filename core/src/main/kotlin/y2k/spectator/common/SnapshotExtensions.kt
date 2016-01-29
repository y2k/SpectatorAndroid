package y2k.spectator.common

import y2k.spectator.model.Snapshot

/**
 * Created by y2k on 1/28/16.
 */

fun Snapshot.getUrl(): String {
    return "http://localhost:5000/api/content/$id"
}