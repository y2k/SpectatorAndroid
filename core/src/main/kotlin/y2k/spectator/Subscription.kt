package y2k.spectator

import com.google.gson.annotations.SerializedName

/**
 * Created by y2k on 1/3/16.
 */
class Subscription {

    @SerializedName("Id")
    var id: Int = 0

    @SerializedName("Group")
    var group: String? = null

    @SerializedName("Source")
    var source: String? = null

    @SerializedName("SubscriptionId")
    var subscriptionId: Int = 0

    @SerializedName("Thumbnail")
    var thumbnail: Int = 0

    @SerializedName("Title")
    var title: String? = null

    @SerializedName("UnreadCount")
    var unreadCount: Int = 0
}