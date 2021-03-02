package com.example.animemusic.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Format {
    @SerializedName("protocol")
    @Expose
    private String protocol;

    public String getProtocol() {
        return protocol;
    }
}
