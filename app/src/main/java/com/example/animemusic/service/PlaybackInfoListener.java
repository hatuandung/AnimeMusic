package com.example.animemusic.service;


import android.support.v4.media.session.PlaybackStateCompat;

public abstract class PlaybackInfoListener {
    public void onPlaybackCompleted() {
    }

    public abstract void onPlaybackStateChange(PlaybackStateCompat playbackStateCompat);
}
