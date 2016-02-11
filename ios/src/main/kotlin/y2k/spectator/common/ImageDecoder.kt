package y2k.spectator.common

import org.robovm.apple.foundation.NSData
import org.robovm.apple.uikit.UIImage
import y2k.spectator.service.ImageService

/**
 * Created by y2k on 2/11/16.
 */
class ImageDecoder : ImageService.Decoder{

    override fun decode(data: ByteArray): Any {
        return UIImage(NSData(data))
    }
}