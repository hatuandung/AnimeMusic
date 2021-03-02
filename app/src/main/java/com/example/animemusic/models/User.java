package com.example.animemusic.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("api_token")
    @Expose
    private String apiToken;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("bio")
    @Expose
    private String bio;

    @SerializedName("color")
    @Expose
    private String color;

    @SerializedName("config")
    @Expose
    private UserConfig config;

    @SerializedName("country_code")
    @Expose
    private String countryCode;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("followers_count")
    @Expose
    private int followersCount;

    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("role_id")
    @Expose
    private int roleId;

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public UserConfig getConfig() {
        return config;
    }

    public void setConfig(UserConfig config) {
        this.config = config;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

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

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}
