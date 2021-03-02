package com.example.animemusic.models;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.animemusic.utils.Filter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Entity
public class Song extends BaseModel {
    @ColumnInfo
    @SerializedName("album")
    @Expose
    private String album;

    @ColumnInfo
    @SerializedName("artist")
    @Expose
    private String artist;

    @ColumnInfo
    @SerializedName("artwork_url")
    @Expose
    private String artworkUrl;

    @ColumnInfo
    @SerializedName("description")
    @Expose
    private String description;

    @ColumnInfo
    @SerializedName("duration")
    @Expose
    private int duration;

    @ColumnInfo
    @SerializedName("favorite_count")
    @Expose
    private int favoriteCount;

    @ColumnInfo
    @SerializedName("genre")
    @Expose
    private String genre;

    @PrimaryKey
    @SerializedName("id")
    @Expose
    @NonNull
    private String id;

    private int isLocal;

    @ColumnInfo
    @SerializedName("media_type")
    @Expose
    private String mediaType;

    @ColumnInfo
    @SerializedName("play_count")
    @Expose
    private int playCount;

    @ColumnInfo
    @SerializedName("rank")
    @Expose
    private int rank;

    @ColumnInfo
    @SerializedName("stream_type")
    @Expose
    private String streamType;

    @ColumnInfo
    @SerializedName("stream_url")
    @Expose
    private String streamUrl;

    @ColumnInfo
    @SerializedName("title")
    @Expose
    private String title;

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        return ((Song) obj).id.equals(getId());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return Filter.getInstance().filterText(this.title);

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtworkUrl() {
        String str = this.artworkUrl;
        return str != null ? str.replace("-large.", "-t500x500.") : str;

    }

    public String getDisplayIconUrl() {
        return this.artworkUrl;
    }


    public void setArtworkUrl(String artworkUrl) {
        this.artworkUrl = artworkUrl;
    }

    public String getDescription() {
        return Filter.getInstance().filterText(this.description);
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getIsLocal() {
        return isLocal;
    }

    public void setIsLocal(int isLocal) {
        this.isLocal = isLocal;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getStreamType() {
        return streamType;
    }

    public void setStreamType(String streamType) {
        this.streamType = streamType;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }
}
