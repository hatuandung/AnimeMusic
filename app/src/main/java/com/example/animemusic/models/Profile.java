package com.example.animemusic.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Profile extends User {
    @SerializedName("favorite_songs")
    @Expose
    private List<Song> favoriteSongs;

    @SerializedName("is_followed")
    @Expose
    private boolean isFollowed;

    @SerializedName("my_playlists")
    @Expose
    private List<Playlist> myPlaylists;

    public List<Song> getFavoriteSongs() {
        return favoriteSongs;
    }

    public void setFavoriteSongs(List<Song> favoriteSongs) {
        this.favoriteSongs = favoriteSongs;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }

    public List<Playlist> getMyPlaylists() {
        return myPlaylists;
    }

    public void setMyPlaylists(List<Playlist> myPlaylists) {
        this.myPlaylists = myPlaylists;
    }

    public User getUser() {
        User user = new User();
        user.setId(getId());
        user.setCountryCode(getCountryCode());
        user.setName(getName());
        user.setEmail(getEmail());
        user.setAvatar(getAvatar());
        user.setBio(getBio());
        user.setColor(getColor());
        user.setGender(getGender());
        user.setRoleId(getRoleId());
        user.setConfig(getConfig());
        return user;
    }

}
