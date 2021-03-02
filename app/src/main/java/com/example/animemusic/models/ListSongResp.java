package com.example.animemusic.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ListSongResp extends BaseModel {

    @SerializedName("songs")
    @Expose
    private List<Song> songs = new ArrayList();

    @SerializedName("total")
    @Expose
    private int total;

    public List<Song> getSongs() {
        return songs;
    }

    public int getTotal() {
        return total;
    }
}
