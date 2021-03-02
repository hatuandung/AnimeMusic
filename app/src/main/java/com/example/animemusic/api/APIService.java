package com.example.animemusic.api;

import com.example.animemusic.models.Config;
import com.example.animemusic.models.FollowInfo;
import com.example.animemusic.models.Frame;
import com.example.animemusic.models.Home;
import com.example.animemusic.models.ListPlaylistResp;
import com.example.animemusic.models.ListSongResp;
import com.example.animemusic.models.Profile;
import com.example.animemusic.models.Song;
import com.example.animemusic.models.User;
import com.example.animemusic.models.UserConfig;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
    @POST("/api/v1/users/{id}/follow")
    Call<Boolean> followUser(@Path("id") int i, @Query("api_token") String str);

    @GET("/api/v1/songs/chart")
    Call<ListSongResp> getChart(@Query("limit") int i, @Query("offset") int i2);

    @GET("/api/v1/configs")
    Call<Config> getConfig();

    @GET("/api/v1/dashboard")
    Call<Home> getHome();

    @GET("/api/v1/playlists/featured")
    Call<ListPlaylistResp> getFeaturedPlaylists(@Query("limit") int i, @Query("offset") int i2);

    @GET("/api/v1/users/{id}/follow")
    Call<FollowInfo> getFollowInfo(@Path("id") int i, @Query("api_token") String str);

    @GET("/api/v1/configs/frames")
    Call<List<Frame>> getFrames();

    @GET("/api/v1/playlists")
    Call<ListPlaylistResp> getPlaylists(@Query("limit") int i, @Query("offset") int i2);

    @GET("/api/v1/users/{id}")
    Call<Profile> getProfile(@Path("id") int i, @Query("api_token") String str);

    @GET("/api/v1/playlists/{id}/songs")
    Call<ListSongResp> getSongsByPlaylist(@Path("id") int i, @Query("limit") int i2, @Query("offset") int i3);

    @PUT("/api/v1/songs/{id}/favorites")
    Call<Object> increaseFavorite(@Path("id") String str);

    @PUT("/api/v1/songs/{id}/play")
    Call<Object> increasePlay(@Path("id") String str);

    @FormUrlEncoded
    @POST("/api/v1/login/{provider}")
    Call<User> login(@Path("provider") String str, @Field("token") String str2);

    @FormUrlEncoded
    @POST("/api/v1/songs/request")
    Call<Object> requestSong(@Query("api_token") String str, @Field("keyword") String str2);

    @GET("/api/v1/songs/search")
    Call<List<Song>> search(@Query("keyword") String str, @Query("limit") int i, @Query("offset") int i2);

    @FormUrlEncoded
    @POST("/api/v1/users/device-token")
    Call<Boolean> storeDeviceToken(@Query("api_token") String str, @Field("token") String str2);

    @FormUrlEncoded
    @POST("/api/v1/sync/playlists")
    Call<Object> syncPlaylist(@Query("api_token") String str, @Field("name") String str2, @Field("source_id") int i, @Field("time_install") long j);

    @FormUrlEncoded
    @POST("/api/v1/sync/playlists/remove")
    Call<Object> syncRemovePlaylist(@Query("api_token") String str, @Field("source_id") int i, @Field("time_install") long j);

    @FormUrlEncoded
    @POST("/api/v1/sync/songs/remove")
    Call<Object> syncRemoveSong(@Query("api_token") String str, @Field("source_id") int i, @Field("song_id") String str2);

    @FormUrlEncoded
    @POST("/api/v1/sync/songs")
    Call<Object> syncSong(@Query("api_token") String str, @Field("source_id") int i, @Field("song_id") String str2, @Field("time_install") long j);

    @POST("/api/v1/users/configs")
    Call<User> updateMineConfigs(@Query("api_token") String str, @Body UserConfig userConfig);

    @FormUrlEncoded
    @PUT("/api/v1/users")
    Call<User> updateUser(@Query("api_token") String str, @Field("name") String str2, @Field("bio") String str3, @Field("gender") String str4, @Field("country_code") String str5, @Field("avatar") String str6);

}