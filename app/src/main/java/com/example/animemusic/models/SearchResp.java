package com.example.animemusic.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SearchResp {
    @SerializedName("collection")
    @Expose
    private List<SCSong> scSongs = new ArrayList();

    public List<SCSong> getSCSongs() {
        return this.scSongs;
    }

}
