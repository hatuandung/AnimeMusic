package com.example.animemusic.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FollowInfo {
    @SerializedName("followers")
    @Expose
    private List<FollowUser> followers;

    @SerializedName("followees")
    @Expose
    private List<FollowUser> followings;

    public List<FollowUser> getFollowers() {
        return followers;
    }

    public void setFollowers(List<FollowUser> followers) {
        this.followers = followers;
    }

    public List<FollowUser> getFollowings() {
        return followings;
    }

    public void setFollowings(List<FollowUser> followings) {
        this.followings = followings;
    }
}
