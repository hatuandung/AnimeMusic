package com.example.animemusic.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Home {
    @SerializedName("banners")
    @Expose
    private List<Playlist> banners = new ArrayList<>();

    @SerializedName("chart")
    @Expose
    private List<Song> chart = new ArrayList<>();

    @SerializedName("featured_playlists")
    @Expose
    private List<Playlist> featuredPlaylists = new ArrayList();

    @SerializedName("playlists")
    @Expose
    private List<Playlist> playlists = new ArrayList();

    public List<Playlist> getBanners() {
        return banners;
    }

    public List<Song> getChart() {
        return chart;
    }

    public List<Playlist> getFeaturedPlaylists() {
        return featuredPlaylists;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }
}
