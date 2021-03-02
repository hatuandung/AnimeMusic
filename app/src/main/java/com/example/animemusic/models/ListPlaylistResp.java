package com.example.animemusic.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ListPlaylistResp {
    @SerializedName("playlists")
    @Expose
    private List<Playlist> playlists = new ArrayList();

    @SerializedName("total")
    @Expose
    private int total;

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public int getTotal() {
        return total;
    }
}
