package com.example.animemusic.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserConfig {
    @SerializedName("color")
    @Expose
    private String color;

    @SerializedName("current_frame_name")
    @Expose
    private String currentFrameName;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCurrentFrameName() {
        return currentFrameName;
    }

    public void setCurrentFrameName(String currentFrameName) {
        this.currentFrameName = currentFrameName;
    }
}
