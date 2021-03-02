package com.example.animemusic.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PublisherMetadata {
    @SerializedName("explicit")
    @Expose
    private boolean explicit;

    @SerializedName("isrc")
    @Expose
    private String isrc;

    public boolean isExplicit() {
        return explicit;
    }

    public String getIsrc() {
        return isrc;
    }
}
