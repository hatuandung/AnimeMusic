package com.example.animemusic.interfaces;

public enum StreamType {
    NORMAL("normal"),
    SOUNDCLOUD("soundcloud");

    private String value;

    StreamType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
