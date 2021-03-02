package com.example.animemusic.utils;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.media.MediaBrowserServiceCompat;

import java.util.ArrayList;
import java.util.List;

public class MediaBrowserHelper {
    public static final String TAG = MediaBrowserHelper.class.getSimpleName();
    private final List<MediaControllerCompat.Callback> mCallbackList = new ArrayList();
    public final Context mContext;
    public MediaBrowserCompat mMediaBrowser;
    private final MediaBrowserConnectionCallback mMediaBrowserConnectionCallback;
    private final Class<? extends MediaBrowserServiceCompat> mMediaBrowserServiceClass;
    public final MediaBrowserSubscriptionCallback mMediaBrowserSubscriptionCallback;
    public MediaControllerCompat mMediaController;
    public final MediaControllerCallback mMediaControllerCallback;

    private interface CallbackCommand {
        void perform(MediaControllerCompat.Callback callback);
    }

    protected void onChildrenLoaded(String str, List<MediaBrowserCompat.MediaItem> list) {
    }

    protected void onConnected(MediaControllerCompat mediaControllerCompat) {
    }

    protected void onDisconnected() {
    }

    public MediaBrowserHelper(Context context, Class<? extends MediaBrowserServiceCompat> cls) {
        this.mContext = context;
        this.mMediaBrowserServiceClass = cls;
        this.mMediaBrowserConnectionCallback = new MediaBrowserConnectionCallback();
        this.mMediaControllerCallback = new MediaControllerCallback();
        this.mMediaBrowserSubscriptionCallback = new MediaBrowserSubscriptionCallback();
    }

    public void onStart() {
        if (this.mMediaBrowser == null) {
            MediaBrowserCompat mediaBrowserCompat = new MediaBrowserCompat(this.mContext, new ComponentName(this.mContext, this.mMediaBrowserServiceClass), this.mMediaBrowserConnectionCallback, (Bundle) null);
            this.mMediaBrowser = mediaBrowserCompat;
            mediaBrowserCompat.connect();
        }
        Log.d(TAG, "onStart: Creating MediaBrowser, and connecting");
    }

    public void onStop() {
        MediaControllerCompat mediaControllerCompat = this.mMediaController;
        if (mediaControllerCompat != null) {
            mediaControllerCompat.unregisterCallback(this.mMediaControllerCallback);
            this.mMediaController = null;
        }
        MediaBrowserCompat mediaBrowserCompat = this.mMediaBrowser;
        if (mediaBrowserCompat != null && mediaBrowserCompat.isConnected()) {
            this.mMediaBrowser.disconnect();
            this.mMediaBrowser = null;
        }
        resetState();
        Log.d(TAG, "onStop: Releasing MediaController, Disconnecting from MediaBrowser");
    }

    public final MediaControllerCompat getMediaController() {
        return this.mMediaController;
    }

    public void resetState() {
        performOnAllCallbacks(new CallbackCommand() {
            public void perform(MediaControllerCompat.Callback callback) {
                callback.onPlaybackStateChanged((PlaybackStateCompat) null);
            }
        });
        Log.d(TAG, "resetState: ");
    }

    public MediaControllerCompat.TransportControls getTransportControls() {
        MediaControllerCompat mediaControllerCompat = this.mMediaController;
        if (mediaControllerCompat != null) {
            return mediaControllerCompat.getTransportControls();
        }
        Log.d(TAG, "getTransportControls: MediaController is null!");
        return null;
    }

    public void registerCallback(MediaControllerCompat.Callback callback) {
        if (callback != null) {
            this.mCallbackList.add(callback);
            MediaControllerCompat mediaControllerCompat = this.mMediaController;
            if (mediaControllerCompat != null) {
                MediaMetadataCompat metadata = mediaControllerCompat.getMetadata();
                if (metadata != null) {
                    callback.onMetadataChanged(metadata);
                }
                PlaybackStateCompat playbackState = this.mMediaController.getPlaybackState();
                if (playbackState != null) {
                    callback.onPlaybackStateChanged(playbackState);
                }
            }
        }
    }

    public void performOnAllCallbacks(CallbackCommand callbackCommand) {
        for (MediaControllerCompat.Callback next : this.mCallbackList) {
            if (next != null) {
                callbackCommand.perform(next);
            }
        }
    }

    private class MediaBrowserConnectionCallback extends MediaBrowserCompat.ConnectionCallback {
        private MediaBrowserConnectionCallback() {
        }

        public void onConnected() {
            try {
                MediaControllerCompat unused = MediaBrowserHelper.this.mMediaController = new MediaControllerCompat(MediaBrowserHelper.this.mContext, MediaBrowserHelper.this.mMediaBrowser.getSessionToken());
                MediaBrowserHelper.this.mMediaController.registerCallback(MediaBrowserHelper.this.mMediaControllerCallback);
                MediaBrowserHelper.this.mMediaControllerCallback.onMetadataChanged(MediaBrowserHelper.this.mMediaController.getMetadata());
                MediaBrowserHelper.this.mMediaControllerCallback.onPlaybackStateChanged(MediaBrowserHelper.this.mMediaController.getPlaybackState());
                MediaBrowserHelper.this.onConnected(MediaBrowserHelper.this.mMediaController);
                MediaBrowserHelper.this.mMediaBrowser.subscribe(MediaBrowserHelper.this.mMediaBrowser.getRoot(), MediaBrowserHelper.this.mMediaBrowserSubscriptionCallback);
            } catch (RemoteException e) {
                Log.d(MediaBrowserHelper.TAG, String.format("onConnected: Problem: %s", new Object[]{e.toString()}));
                throw new RuntimeException(e);
            }
        }

        public void onConnectionFailed() {
            super.onConnectionFailed();
            Log.d(MediaBrowserHelper.TAG, String.format("onConnectedFailed", new Object[0]));
        }

        public void onConnectionSuspended() {
            super.onConnectionSuspended();
            Log.d(MediaBrowserHelper.TAG, String.format("onConnectedSuspended", new Object[0]));
        }
    }

    public class MediaBrowserSubscriptionCallback extends MediaBrowserCompat.SubscriptionCallback {
        public MediaBrowserSubscriptionCallback() {
        }

        public void onChildrenLoaded(String str, List<MediaBrowserCompat.MediaItem> list) {
            MediaBrowserHelper.this.onChildrenLoaded(str, list);
        }
    }

    private class MediaControllerCallback extends MediaControllerCompat.Callback {
        private MediaControllerCallback() {
        }

        public void onMetadataChanged(final MediaMetadataCompat mediaMetadataCompat) {
            MediaBrowserHelper.this.performOnAllCallbacks(new CallbackCommand() {
                public void perform(MediaControllerCompat.Callback callback) {
                    callback.onMetadataChanged(mediaMetadataCompat);
                }
            });
        }

        public void onPlaybackStateChanged(final PlaybackStateCompat playbackStateCompat) {
            MediaBrowserHelper.this.performOnAllCallbacks(new CallbackCommand() {
                public void perform(MediaControllerCompat.Callback callback) {
                    callback.onPlaybackStateChanged(playbackStateCompat);
                }
            });
        }

        public void onSessionDestroyed() {
            MediaBrowserHelper.this.resetState();
            onPlaybackStateChanged((PlaybackStateCompat) null);
            MediaBrowserHelper.this.onDisconnected();
        }

        public void onQueueChanged(final List<MediaSessionCompat.QueueItem> list) {
            MediaBrowserHelper.this.performOnAllCallbacks(new CallbackCommand() {
                public void perform(MediaControllerCompat.Callback callback) {
                    callback.onQueueChanged(list);
                }
            });
        }
    }
}