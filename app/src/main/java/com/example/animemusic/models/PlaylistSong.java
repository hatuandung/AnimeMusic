package com.example.animemusic.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"playlistId", "songId"})
public class PlaylistSong {
    @NonNull
    public int playlistId;

    @NonNull
    public String songId;

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public PlaylistSong(String songId, int playlistId ) {
        this.songId = songId;
        this.playlistId = playlistId;
    }
}