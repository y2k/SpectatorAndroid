package y2k.spectator

import com.google.gson.annotations.SerializedName
import com.squareup.okhttp.ResponseBody
import retrofit.http.*
import rx.Observable

/**
 * Created by y2k on 1/3/16.
 */
interface Api {

    @PUT("subscriptions")
    @FormUrlEncoded
    fun create(@Field("Source") url: String, @Field("Title") title: String): Observable<Unit>

    @POST("subscriptions/{id}")
    @FormUrlEncoded
    fun edit(@Path("id") id: Int, @Field("Title") title: String): Observable<Unit>

    @DELETE("subscriptions/{id}")
    fun delete(@Path("id") id: Int): Observable<Unit>

    @GET("subscriptions")
    fun subscriptions(): Observable<SubscriptionResponse>

    @PUT("account")
    @FormUrlEncoded
    fun login(@Field("code") code: String, @Field("redirectUri") redirectUri: String): Observable<Unit>

    @GET("snapshots")
    fun snapshots(): Observable<SnapshotsResponse>

    @GET("images/{id}")
    fun images(@Path("id") id: Int, @Query("width") width: Int, @Query("height") height: Int): Observable<ResponseBody>
}

class SubscriptionResponse {

    @SerializedName("Subscriptions")
    lateinit var subscriptions: List<Subscription>
}

class SnapshotsResponse {

    @SerializedName("Snapshots")
    lateinit var snapshots: List<Snapshot>
}