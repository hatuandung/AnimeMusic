package com.example.animemusic.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SoundCloundBuilder {
    private static SoundCloundService soundCloundService;

    public static SoundCloundService getInstance() {
        if (soundCloundService == null){
            soundCloundService = new Retrofit.Builder()
                    .baseUrl("https://api-v2.soundcloud.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(SoundCloundService.class);
        }
        return soundCloundService;
    }
}
