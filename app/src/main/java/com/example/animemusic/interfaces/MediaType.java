package com.example.animemusic.interfaces;


public enum MediaType {
    PROGRESSIVE("progressive"),
    HLS("hls");


    private String value;

    MediaType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
