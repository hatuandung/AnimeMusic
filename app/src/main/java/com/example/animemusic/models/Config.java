package com.example.animemusic.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config {
    @SerializedName("event_send")
    @Expose
    private String eventSend;

    @SerializedName("event_floating")
    @Expose
    private String floatingEvent;

    @SerializedName("is_artwork")
    @Expose
    private int isArtWork;

    @SerializedName("is_migrate")
    @Expose
    private int isMigrate;

    @SerializedName("is_s3_streaming")
    @Expose
    private boolean isS3Streaming;

    @SerializedName("is_show_featured")
    @Expose
    private int isShowFeatured;

    @SerializedName("is_sync_enabled")
    @Expose
    private int isSyncEnabled;

    @SerializedName("event_messages")
    @Expose
    private String messageEvent;

    @SerializedName("dialog_migrate")
    @Expose
    private Migrate migrate;

    @SerializedName("sc_client_id")
    @Expose
    private String scClientId;

    public String getEventSend() {
        return eventSend;
    }

    public void setEventSend(String eventSend) {
        this.eventSend = eventSend;
    }

    public String getFloatingEvent() {
        return floatingEvent;
    }

    public void setFloatingEvent(String floatingEvent) {
        this.floatingEvent = floatingEvent;
    }

    public int getIsArtWork() {
        return isArtWork;
    }

    public void setIsArtWork(int isArtWork) {
        this.isArtWork = isArtWork;
    }

    public int getIsMigrate() {
        return isMigrate;
    }

    public void setIsMigrate(int isMigrate) {
        this.isMigrate = isMigrate;
    }

    public boolean isS3Streaming() {
        return isS3Streaming;
    }

    public void setS3Streaming(boolean s3Streaming) {
        isS3Streaming = s3Streaming;
    }

    public int getIsShowFeatured() {
        return isShowFeatured;
    }

    public void setIsShowFeatured(int isShowFeatured) {
        this.isShowFeatured = isShowFeatured;
    }

    public int getIsSyncEnabled() {
        return isSyncEnabled;
    }

    public void setIsSyncEnabled(int isSyncEnabled) {
        this.isSyncEnabled = isSyncEnabled;
    }

    public String getMessageEvent() {
        return messageEvent;
    }

    public void setMessageEvent(String messageEvent) {
        this.messageEvent = messageEvent;
    }

    public Migrate getMigrate() {
        return migrate;
    }

    public void setMigrate(Migrate migrate) {
        this.migrate = migrate;
    }

    public String getScClientId() {
        return scClientId;
    }

    public void setScClientId(String scClientId) {
        this.scClientId = scClientId;
    }
}
