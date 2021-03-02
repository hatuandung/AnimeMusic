package com.example.animemusic.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiBuilder {
    private static APIService apiService;

    public static APIService getInstance() {
        if (apiService == null){
            apiService = new Retrofit.Builder()
                    .baseUrl("https://anime.leakstuffs.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(APIService.class);
        }
        return apiService;
    }
}
