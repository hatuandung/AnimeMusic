package com.example.animemusic.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Entity
public class Playlist {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    private int id;

    @ColumnInfo
    @SerializedName("name")
    @Expose
    private String name;

    @ColumnInfo
    @SerializedName("plays_count")
    @Expose
    private int playsCount;

    @ColumnInfo
    @SerializedName("songs_count")
    @Expose
    private int songsCount;

    @ColumnInfo
    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlaysCount() {
        return playsCount;
    }

    public void setPlaysCount(int playsCount) {
        this.playsCount = playsCount;
    }

    public int getSongsCount() {
        return songsCount;
    }

    public void setSongsCount(int songsCount) {
        this.songsCount = songsCount;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public boolean equals(Object obj) {
        if (obj == this || ((Playlist) obj).id == getId()) {
            return true;
        }
        return false;
    }

}
