package com.example.animemusic.interfaces;

public enum NotificationType {
    PLAYLIST("playlist"),
    SONG("song");

    private String value;

    private NotificationType(String str) {
        this.value = str;
    }

    public String getValue() {
        return this.value;
    }
}