package com.example.animemusic.models;

import android.util.Property;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Message {
    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("color")
    @Expose
    private String color;

    @SerializedName("created_at")
    @Expose
    private long createdAt;

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("msg")
    @Expose
    private String msg;

    private List<Property> properties;

    @SerializedName("user")
    @Expose
    private User user;

    @SerializedName("user_id")
    @Expose
    private int userId;

    @SerializedName("username")
    @Expose
    private String username;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Property> getProperties() {
        List<Property> list = this.properties;
        return list == null ? new ArrayList() : list;
    }

    public void setProperties(List<Property> list) {
        this.properties = list;
    }
    public Message clone() {
        Message message = new Message();
        message.setId(getId());
        message.setMsg(getMsg());
        message.setAvatar(getAvatar());
        message.setUserId(getUserId());
        message.setUsername(getUsername());
        message.setCreatedAt(this.createdAt);
        message.setColor(getColor());
        message.setProperties(getProperties());
        message.setUser(getUser());
        return message;
    }

}
