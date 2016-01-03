package y2k.spectator

import com.google.gson.annotations.SerializedName
import retrofit.http.*
import rx.Observable

/**
 * Created by y2k on 1/3/16.
 */
interface Api {

    @PUT("subscriptions")
    @FormUrlEncoded
    fun add(@Query("Source") url: String, @Query("Title") title: String): Observable<Unit>

    @POST("subscriptions/{id}")
    @FormUrlEncoded
    fun edit(@Path("id") id: Int, @Query("Title") title: String): Observable<Unit>

    @DELETE("subscriptions/{id}")
    fun delete(@Path("id") id: Int): Observable<Unit>

    @GET("subscriptions")
    fun subscriptions(): Observable<SubscriptionResponse>
}

class SubscriptionResponse {

    @SerializedName("Subscriptions")
    lateinit var subscriptions: List<Subscription>
}