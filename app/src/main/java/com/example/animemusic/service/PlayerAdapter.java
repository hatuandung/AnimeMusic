package com.example.animemusic.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.support.v4.media.MediaMetadataCompat;


public abstract class PlayerAdapter {
    private static final IntentFilter AUDIO_NOISY_INTENT_FILTER = new IntentFilter("android.media.AUDIO_BECOMING_NOISY");
    private static final float MEDIA_VOLUME_DEFAULT = 1.0f;
    private static final float MEDIA_VOLUME_DUCK = 0.2f;
    private final Context mApplicationContext;
    private final AudioFocusHelper mAudioFocusHelper;
    /* access modifiers changed from: private */
    public final AudioManager mAudioManager;
    private final BroadcastReceiver mAudioNoisyReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.media.AUDIO_BECOMING_NOISY".equals(intent.getAction()) && PlayerAdapter.this.isPlaying()) {
                PlayerAdapter.this.pause();
            }
        }
    };
    private boolean mAudioNoisyReceiverRegistered = false;
    /* access modifiers changed from: private */
    public boolean mPlayOnAudioFocus = false;

    public abstract MediaMetadataCompat getCurrentMedia();

    public abstract boolean isPlaying();

    /* access modifiers changed from: protected */
    public abstract void onPause();

    /* access modifiers changed from: protected */
    public abstract void onPlay();

    /* access modifiers changed from: protected */
    public abstract void onStop();

    public abstract void playFromMedia(MediaMetadataCompat mediaMetadataCompat);

    public abstract void playFromUri(MediaMetadataCompat mediaMetadataCompat);

    public abstract void seekTo(long j);

    public abstract void setVolume(float f);

    public PlayerAdapter(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.mApplicationContext = applicationContext;
        this.mAudioManager = (AudioManager) applicationContext.getSystemService(Context.AUDIO_SERVICE);
        this.mAudioFocusHelper = new AudioFocusHelper();
    }

    public final void play() {
        if (this.mAudioFocusHelper.requestAudioFocus()) {
            registerAudioNoisyReceiver();
            onPlay();
        }
    }

    public final void pause() {
        if (!this.mPlayOnAudioFocus) {
            this.mAudioFocusHelper.abandonAudioFocus();
        }
        unregisterAudioNoisyReceiver();
        onPause();
    }

    public final void stop() {
        this.mAudioFocusHelper.abandonAudioFocus();
        unregisterAudioNoisyReceiver();
        onStop();
    }

    private void registerAudioNoisyReceiver() {
        if (!this.mAudioNoisyReceiverRegistered) {
            this.mApplicationContext.registerReceiver(this.mAudioNoisyReceiver, AUDIO_NOISY_INTENT_FILTER);
            this.mAudioNoisyReceiverRegistered = true;
        }
    }

    private void unregisterAudioNoisyReceiver() {
        if (this.mAudioNoisyReceiverRegistered) {
            this.mApplicationContext.unregisterReceiver(this.mAudioNoisyReceiver);
            this.mAudioNoisyReceiverRegistered = false;
        }
    }

    private final class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {
        private AudioFocusHelper() {
        }

        /* access modifiers changed from: private */
        public boolean requestAudioFocus() {
            return PlayerAdapter.this.mAudioManager.requestAudioFocus(this, 3, 1) == 1;
        }

        /* access modifiers changed from: private */
        public void abandonAudioFocus() {
            PlayerAdapter.this.mAudioManager.abandonAudioFocus(this);
        }

        public void onAudioFocusChange(int i) {
            if (i == -3) {
                PlayerAdapter.this.setVolume(PlayerAdapter.MEDIA_VOLUME_DUCK);
            } else if (i != -2) {
                if (i == -1) {
                    PlayerAdapter.this.mAudioManager.abandonAudioFocus(this);
                    boolean unused = PlayerAdapter.this.mPlayOnAudioFocus = false;
                    PlayerAdapter.this.stop();
                } else if (i == 1) {
                    if (PlayerAdapter.this.mPlayOnAudioFocus && !PlayerAdapter.this.isPlaying()) {
                        PlayerAdapter.this.play();
                    } else if (PlayerAdapter.this.isPlaying()) {
                        PlayerAdapter.this.setVolume(1.0f);
                    }
                    boolean unused2 = PlayerAdapter.this.mPlayOnAudioFocus = false;
                }
            } else if (PlayerAdapter.this.isPlaying()) {
                boolean unused3 = PlayerAdapter.this.mPlayOnAudioFocus = true;
                PlayerAdapter.this.pause();
            }
        }
    }
}