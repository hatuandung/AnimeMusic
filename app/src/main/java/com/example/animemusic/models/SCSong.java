package com.example.animemusic.models;

import com.example.animemusic.interfaces.MediaType;
import com.example.animemusic.interfaces.StreamType;
import com.example.animemusic.utils.Filter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class SCSong {
    @SerializedName("artwork_url")
    @Expose
    private String artworkUrl;

    @SerializedName("full_duration")
    @Expose
    private int duration;

    @SerializedName("likes_count")
    @Expose
    private int favoriteCount;

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("media")
    @Expose
    private Media media;

    @SerializedName("playback_count")
    @Expose
    private int playCount;

    @SerializedName("publisher_metadata")
    @Expose
    private PublisherMetadata publisherMetadata;

    @SerializedName("title")
    @Expose
    private String title;

    public String getArtworkUrl() {
        String str = this.artworkUrl;
        return str != null ? str.replace("-large.", "-t500x500.") : str;

    }

    public void setArtworkUrl(String artworkUrl) {
        this.artworkUrl = artworkUrl;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public PublisherMetadata getPublisherMetadata() {
        return publisherMetadata;
    }

    public void setPublisherMetadata(PublisherMetadata publisherMetadata) {
        this.publisherMetadata = publisherMetadata;
    }

    public String getTitle() {
        return Filter.getInstance().filterText(this.title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Song toSong() {
        Song song = new Song();
        song.setId(UUID.nameUUIDFromBytes(Integer.toString(getId()).getBytes()).toString());
        song.setTitle(getTitle());
        song.setArtworkUrl(getArtworkUrl());
        song.setDuration(getDuration());
        song.setMediaType(MediaType.HLS.getValue());
        String str = null;
        try {
            if (!getMedia().getTranscodings().get(0).isSnipped()) {
                str = getMedia().getTranscodings().get(0).getUrl();
            }
        } catch (Exception unused) {
        }
        song.setStreamUrl(str);
        song.setStreamType(StreamType.SOUNDCLOUD.getValue());
        return song;
    }

}
