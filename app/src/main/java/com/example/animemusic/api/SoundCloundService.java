package com.example.animemusic.api;

import com.example.animemusic.models.SCSong;
import com.example.animemusic.models.SearchResp;
import com.example.animemusic.models.StreamResp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface SoundCloundService {
    @GET
    Call<StreamResp> getStreamUrl(@Url String str, @Query("client_id") String str2);

    @GET
    Call<SCSong> resolveTrack(@Url String str, @Query("client_id") String str2);

    @GET
    Call<SearchResp> search(@Url String str, @Query("q") String str2, @Query("limit") int i, @Query("offset") int i2, @Query("client_id") String str3);
}
