package com.example.animemusic.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transcoding {
    @SerializedName("format")
    @Expose
    private Format format;

    @SerializedName("snipped")
    @Expose
    private boolean snipped;

    @SerializedName("url")
    @Expose
    private String url;

    public Format getFormat() {
        return format;
    }

    public boolean isSnipped() {
        return snipped;
    }

    public String getUrl() {
        return url;
    }
}
