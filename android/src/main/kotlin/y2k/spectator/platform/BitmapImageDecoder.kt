package y2k.spectator.platform

import android.graphics.BitmapFactory
import y2k.spectator.service.ImageService

/**
 * Created by y2k on 2/11/16.
 */
class BitmapImageDecoder : ImageService.Decoder {

    override fun decode(data: ByteArray): Any {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }
}