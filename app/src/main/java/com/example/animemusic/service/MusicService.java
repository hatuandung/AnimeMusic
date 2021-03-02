package com.example.animemusic.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.animemusic.R;
import com.example.animemusic.models.Config;
import com.example.animemusic.utils.NetworkHelper;
import com.example.animemusic.utils.SharedPrefHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import static com.example.animemusic.service.MediaNotificationManager.NOTIFICATION_ID;

public class MusicService extends MediaBrowserServiceCompat {
    public static final String CUSTOM_ACTION_STOP_AND_PLAY = "stop_and_play_new";
    private static final String TAG = MusicService.class.getSimpleName();

    public MediaNotificationManager mMediaNotificationManager;

    public PlayerAdapter mPlayback;
    private MediaMetadataCompat mPreparedMedia;
    public boolean mServiceInStartedState;
    public MediaSessionCompat mSession;

    private MediaSessionCallback mediaSessionCallback;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mSession, intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MusicLibrary.initQueue(this);
        mSession = new MediaSessionCompat(this, TAG);
        mediaSessionCallback = new MediaSessionCallback();

        mSession.setCallback(mediaSessionCallback);

        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);


        setSessionToken(mSession.getSessionToken());
        mSession.setActive(true);

        mMediaNotificationManager = new MediaNotificationManager(this);

        Config config = SharedPrefHelper.getInstance(getApplicationContext()).getConfig();

        if (config == null) {
            Toast.makeText(this, getText(R.string.something_wrong), Toast.LENGTH_LONG).show();
            return;
        }

        mPlayback = new ExoPlayerAdapter(this, new MediaPlayerListener(), config.getScClientId(), config.isS3Streaming());
        Log.e(TAG, "onCreate: MusicService creating MediaSession, and MediaNotificationManager");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        try {
            mMediaNotificationManager.onDestroy();
            mPlayback.stop();
            mSession.release();
            Log.e(TAG, "onDestroy: MediaPlayerAdapter stopped, and MediaSession released");
        } catch (Exception unused) {
        }
    }

//    public MediaBrowserServiceCompat.BrowserRoot onGetRoot(String str, int i, Bundle bundle) {
//        return new MediaBrowserServiceCompat.BrowserRoot(MusicLibrary.getRoot(), null);
//    }


    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(MusicLibrary.getRoot(), null);
    }

//    public MediaBrowserServiceCompat.BrowserRoot onGetRoot(String str, int i, Bundle bundle) {
//        return new MediaBrowserServiceCompat.BrowserRoot(MusicLibrary.getRoot(), null);
//    }


    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(MusicLibrary.getMediaItems());
    }

    public void prepare() {
        int queueIndex = MusicLibrary.getQueueIndex(this);
        List<MediaSessionCompat.QueueItem> queueItems = MusicLibrary.getQueueItems();
        if (queueIndex >= 0 && !queueItems.isEmpty() && queueIndex < queueItems.size()) {
            MediaMetadataCompat mediaMetadata = MusicLibrary.getMediaMetadata(this, queueItems.get(queueIndex).getDescription().getMediaId());
            mPreparedMedia = mediaMetadata;
            if (mediaMetadata != null) {
                mSession.setMetadata(mediaMetadata);
            }
            if (!mSession.isActive()) {
                mSession.setActive(true);
                Log.e(TAG, "prepare: ");
            }
        }
    }

    private boolean isReadyToPlay() {
        return !MusicLibrary.getQueueItems().isEmpty();
    }

    public void play() {
        if (isReadyToPlay()) {
            if (!NetworkHelper.isOnline(this)) {
                Toast.makeText(this, getText(R.string.no_internet), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!mServiceInStartedState) {
                Log.e("mylog", "start service");
                try {
                    startService(new Intent(getApplicationContext(), MusicService.class));
                    mServiceInStartedState = true;
                } catch (Exception unused) {
                    Toast.makeText(this, getText(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (mPreparedMedia == null) {
                prepare();
            }
            mPlayback.playFromUri(mPreparedMedia);
            Log.d(TAG, "onPlayFromMediaId: MediaSession active");
        }
    }

    public void pause() {
        mPlayback.pause();
    }

    public void stop() {
        mPlayback.stop();
        mSession.setActive(false);
    }

    public void skipToNext() {
        mPreparedMedia = null;
        MusicLibrary.nextQueueIndex(this);
        play();
    }

    public void skipToPrevious() {
        mPreparedMedia = null;
        MusicLibrary.prevQueueIndex(this);
        play();
    }

    public void seekTo(long j) {
        mPlayback.seekTo(j);
    }

    public void setRepeatMode(int i) {
        MusicLibrary.setRepeatMode(this, i);
    }

    public void setShuffleMode(int i) {
        MusicLibrary.setShuffleMode(this, i);
        if (i == 1) {
            MusicLibrary.shuffle(getApplicationContext());
        }
    }

    public class MediaSessionCallback extends MediaSessionCompat.Callback {


        public MediaSessionCallback() {
        }

        @Override
        public void onPrepare() {
            prepare();
        }

        @Override
        public void onPlay() {
            play();
        }

        @Override
        public void onPause() {
            pause();
        }

        @Override
        public void onStop() {
            stop();
        }

        @Override
        public void onSkipToNext() {
            skipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            skipToPrevious();
        }

        @Override
        public void onSeekTo(long pos) {
            seekTo(pos);
        }

        @Override
        public void onSetRepeatMode(int repeatMode) {
            super.onSetRepeatMode(repeatMode);
            setRepeatMode(repeatMode);
        }

        @Override
        public void onSetShuffleMode(int shuffleMode) {
            super.onSetShuffleMode(shuffleMode);
            setShuffleMode(shuffleMode);
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);
            if (extras.equals(MusicService.CUSTOM_ACTION_STOP_AND_PLAY)) {
                stop();
                prepare();
                play();
//                Log.e(TAG, "onCustomAction: " );
            }
        }
    }

    public class MediaPlayerListener extends PlaybackInfoListener {
        private final ServiceManager mServiceManager ;

        MediaPlayerListener() {
            mServiceManager = new ServiceManager();
        }

        @Override
        public void onPlaybackStateChange(PlaybackStateCompat playbackStateCompat) {
            mSession.setPlaybackState(playbackStateCompat);

//            int state = playbackStateCompat.getState();
//            if (state != PlaybackStateCompat.STATE_STOPPED) {
//                if (state != PlaybackStateCompat.STATE_PAUSED) {
//                    if (state == PlaybackStateCompat.STATE_PLAYING) {
//                        mServiceManager.moveServiceToStartedState(playbackStateCompat);
//                        return;
//                    } else if (state != PlaybackStateCompat.STATE_BUFFERING) {
//                        return;
//                    }
//                }
//                mServiceManager.updateNotification(playbackStateCompat);
//                return;
//            }
//            mServiceManager.moveServiceOutOfStartedState(playbackStateCompat);
            mSession.setPlaybackState(playbackStateCompat);

            switch (playbackStateCompat.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mServiceManager.moveServiceToStartedState(playbackStateCompat);
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    mServiceManager.updateNotification(playbackStateCompat);
                    break;
                case PlaybackStateCompat.STATE_STOPPED:
                    mServiceManager.moveServiceOutOfStartedState(playbackStateCompat);
                    break;
            }
        }

        @Override
        public void onPlaybackCompleted() {
            super.onPlaybackCompleted();
            if (MusicLibrary.getRepeatMode(MusicService.this) == 2) {
                skipToNext();
            } else if (MusicLibrary.getRepeatMode(MusicService.this) == 1) {
                seekTo(0);
                play();
            } else if (MusicLibrary.getRepeatMode(MusicService.this) == 0) {
                seekTo(0);
                pause();
            }
        }

        class ServiceManager {

            public void moveServiceToStartedState(PlaybackStateCompat playbackStateCompat) {
                final NotificationCompat.Builder buildNotification = mMediaNotificationManager.buildNotification(mPlayback.getCurrentMedia(), playbackStateCompat, getSessionToken());
                String string = mPlayback.getCurrentMedia().getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI);
                if (string != null) {
                    int dimension = (int) MusicService.this.getResources().getDimension(R.dimen.image_xs_size);
                    Glide.with(MusicService.this).asBitmap().load(string).apply(RequestOptions.timeoutOf(1000)).centerCrop().into(new CustomTarget<Bitmap>(dimension, dimension) {
                        public void onLoadCleared(Drawable drawable) {
                        }

                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                            buildNotification.setLargeIcon(bitmap);
                            if (!mServiceInStartedState) {
                                ContextCompat.startForegroundService(
                                        MusicService.this,
                                        new Intent(MusicService.this, MusicService.class));
                                mServiceInStartedState = true;
                            }
                            startForeground(NOTIFICATION_ID, buildNotification.build());
                            Log.e("mylog", "onResourceReady ");
                        }

                        public void onLoadFailed(Drawable drawable) {
                            super.onLoadFailed(drawable);
                            Log.e("mylog", "error");
                            buildNotification.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round));
                            if (!mServiceInStartedState) {
                                ContextCompat.startForegroundService(
                                        MusicService.this,
                                        new Intent(MusicService.this, MusicService.class));
                                mServiceInStartedState = true;
                            }
                            startForeground(NOTIFICATION_ID, buildNotification.build());
                        }
                    });
                    return;
                }
                buildNotification.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
                if (!mServiceInStartedState) {
                    ContextCompat.startForegroundService(
                            MusicService.this,
                            new Intent(MusicService.this, MusicService.class));
                    mServiceInStartedState = true;
                }
                startForeground(NOTIFICATION_ID, buildNotification.build());

            }

            public void updateNotification(PlaybackStateCompat playbackStateCompat) {
                if (playbackStateCompat.getState() == PlaybackStateCompat.STATE_PAUSED) {
                    stopForeground(false);
                }
                final NotificationCompat.Builder buildNotification = mMediaNotificationManager.buildNotification(mPlayback.getCurrentMedia(), playbackStateCompat, getSessionToken());
                new Target() {
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                        buildNotification.setLargeIcon(bitmap);
                        mMediaNotificationManager.getNotificationManager().notify(NOTIFICATION_ID, buildNotification.build());
                    }

                    public void onBitmapFailed(Exception exc, Drawable drawable) {

                        buildNotification.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round));
                        mMediaNotificationManager.getNotificationManager().notify(NOTIFICATION_ID, buildNotification.build());
                    }

                    public void onPrepareLoad(Drawable drawable) {
                        Log.d("swipe", "prepared");
                    }
                };
                String string = mPlayback.getCurrentMedia().getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI);
                if (string != null) {
                    int dimension = (int) MusicService.this.getResources().getDimension(R.dimen.image_xs_size);
                    Glide.with(MusicService.this).asBitmap().load(string).apply((BaseRequestOptions<?>) RequestOptions.timeoutOf(1)).centerCrop().into(new CustomTarget<Bitmap>(dimension, dimension) {
                        public void onLoadCleared(Drawable drawable) {
                        }

                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                            buildNotification.setLargeIcon(bitmap);
                            mMediaNotificationManager.getNotificationManager().notify(NOTIFICATION_ID, buildNotification.build());
                            Log.e(TAG, "onResourceReady: ");
                        }

                        public void onLoadFailed(Drawable drawable) {
                            super.onLoadFailed(drawable);
                            Log.e("mylog", "error");
                            buildNotification.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
                            mMediaNotificationManager.getNotificationManager().notify(NOTIFICATION_ID, buildNotification.build());
                        }
                    });
                    return;
                }
                buildNotification.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
                mMediaNotificationManager.getNotificationManager().notify(NOTIFICATION_ID, buildNotification.build());
            }

            public void moveServiceOutOfStartedState(PlaybackStateCompat playbackStateCompat) {
                stopForeground(true);
                stopSelf();
                mServiceInStartedState = false;
            }
        }
    }
}
