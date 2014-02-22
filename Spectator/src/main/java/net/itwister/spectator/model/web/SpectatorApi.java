package net.itwister.spectator.model.web;

import net.itwister.spectator.data.protobuf.SnapshotsResponseProtos.ProtoSnapshotsResponse;
import net.itwister.spectator.data.protobuf.SnapshotsResponseProtos.ProtoSnapshotsResponse.ProtoSnapshot;
import net.itwister.spectator.data.protobuf.SnapshotsResponseProtos.ProtoSubscriptionResponse;

import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;

public interface SpectatorApi {

	@FormUrlEncoded
	@POST("/api/subscription2")
	Void createSubscription(@Field("Source") String source, @Field("DontUsePlugins") boolean dontUsePlugins, @Field("Title") String title, @Field("IsRss") boolean isRss);

	@DELETE("/api/subscription2/{id}")
    Void deleteSubscription(@Part("id") int id);

	@FormUrlEncoded
	@POST("/api/subscription2/{id}")
    Void editSubscription(@Part("id") int id, @Field("title") String title);

    @FormUrlEncoded
	@POST("/Account/Login")
    Void login(@Field("token") String token);

	@FormUrlEncoded
	@POST("/api/push")
    Void setGcmRegistrationId(@Field("registrationId") String registrationId);

	@GET("/api/snapshot2/{id}")
	ProtoSnapshot snapshot(@Part("id") int id);

	@GET("/api/snapshot2")
	ProtoSnapshotsResponse snapshotList(@Query("subId") Integer subId, @Query("query") String query, @Query("toId") Integer toId, @Query("count") int count);

	@GET("/api/stash")
	ProtoSnapshotsResponse stashList(@Query("toId") Integer toId, @Query("count") int count);

	@GET("/api/subscription2")
	ProtoSubscriptionResponse subscriptionList();

    @FormUrlEncoded
    @POST("/api/stash")
    Void stashAdd(@Field("id")int snapshotId);

    @DELETE("/api/stash/{id}")
    Void stashDelete(@Part("id") int snapshotId);

    @GET("/Content/Index/{id}")
    String content(@Part("id") int snapshotId);

    @GET("/Content/Diff/{id}")
    String contentDiff(@Part("id") int snapshotId);
}