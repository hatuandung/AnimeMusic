package com.example.animemusic.api;

import com.example.animemusic.models.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ChatService {
    @GET("/api/v1/messages")
    Call<List<Message>> getMessages();

    @GET("/api/v2/messages")
    Call<List<Message>> getMessagesV2();
}
