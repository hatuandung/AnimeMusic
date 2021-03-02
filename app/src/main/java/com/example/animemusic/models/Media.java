package com.example.animemusic.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Media {
    @SerializedName("transcodings")
    @Expose
    private List<Transcoding> transcodings = new ArrayList();

    public List<Transcoding> getTranscodings() {
        return transcodings;
    }
}
