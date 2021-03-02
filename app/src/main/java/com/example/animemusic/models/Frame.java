package com.example.animemusic.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Frame {
    @SerializedName("is_reward")
    @Expose
    private boolean isReward;

    @SerializedName("name")
    @Expose
    private String name;

    public boolean isReward() {
        return isReward;
    }

    public void setReward(boolean reward) {
        isReward = reward;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
