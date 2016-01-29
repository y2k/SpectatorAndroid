package y2k.spectator.model

import com.google.gson.annotations.SerializedName

/**
 * Created by y2k on 1/3/16.
 */
class Snapshot {

    val image: Image
        get() = if (thumbnail == 0) Image.Default else Image(thumbnail)

    @SerializedName("HasContent")
    var hasContent: Boolean = false

    @SerializedName("HasRevisions")
    var hasRevisions: Boolean = false

    @SerializedName("HasScreenshots")
    var hasScreenshots: Boolean = false

    @SerializedName("Id")
    var id: Int = 0

    //    @SerializedName("Images")
    //    lateinit var images: List<String>

    @SerializedName("Source")
    lateinit var source: String

    @SerializedName("SubscriptionIcon")
    var subscriptionIcon: Int = 0

    @SerializedName("SubscriptionId")
    var subscriptionId: Int = 0

    @SerializedName("SubscriptionName")
    lateinit var subscriptionName: String

    @SerializedName("Thumbnail")
    var thumbnail: Int = 0

    @SerializedName("ThumbnailHeight")
    var thumbnailHeight: Int = 0

    @SerializedName("ThumbnailWidth")
    var thumbnailWidth: Int = 0

    @SerializedName("Title")
    lateinit var title: String

    @SerializedName("Updated")
    var updated: Long = 0
}