package com.example.animemusic.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatBuilder {
    private static ChatService chatService;

    public static ChatService getInstance() {
        if (chatService == null){
            chatService = new Retrofit.Builder()
                    .baseUrl("https://message.leakstuffs.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ChatService.class);
        }
        return chatService;
    }
}
