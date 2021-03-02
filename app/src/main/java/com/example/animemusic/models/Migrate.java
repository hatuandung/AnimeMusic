package com.example.animemusic.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Migrate {
    @SerializedName("app_desc")
    @Expose
    private String appDesc;

    @SerializedName("app_icon_url")
    @Expose
    private String appIconUrl;

    @SerializedName("app_title")
    @Expose
    private String appTitle;

    @SerializedName("btn_1_label")
    @Expose
    private String btn1Label;

    @SerializedName("btn_2_label")
    @Expose
    private String btn2Label;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("url_1")
    @Expose
    private String url1;

    @SerializedName("url_2")
    @Expose
    private String url2;

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public String getAppIconUrl() {
        return appIconUrl;
    }

    public void setAppIconUrl(String appIconUrl) {
        this.appIconUrl = appIconUrl;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    public String getBtn1Label() {
        return btn1Label;
    }

    public void setBtn1Label(String btn1Label) {
        this.btn1Label = btn1Label;
    }

    public String getBtn2Label() {
        return btn2Label;
    }

    public void setBtn2Label(String btn2Label) {
        this.btn2Label = btn2Label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl1() {
        return url1;
    }

    public void setUrl1(String url1) {
        this.url1 = url1;
    }

    public String getUrl2() {
        return url2;
    }

    public void setUrl2(String url2) {
        this.url2 = url2;
    }
}
