package com.example.animemusic.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.internal.view.SupportMenu;
import androidx.media.session.MediaButtonReceiver;

import com.example.animemusic.R;
import com.example.animemusic.activity.MainActivity;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class MediaNotificationManager {
    private static final String CHANNEL_ID = "com.example.android.musicplayer.channel";
    public static final int NOTIFICATION_ID = 412;
    private static final int REQUEST_CODE = 501;
    private static final String TAG = MediaNotificationManager.class.getSimpleName();
    private final NotificationCompat.Action mBufferingAction;
    private final NotificationCompat.Action mNextAction;
    private final NotificationManager mNotificationManager;
    private final NotificationCompat.Action mPauseAction ;
    private final NotificationCompat.Action mPlayAction;
    private final NotificationCompat.Action mPrevAction;
    private final MusicService mService;

    public MediaNotificationManager(MusicService musicService) {
        this.mService = musicService;
        this.mNotificationManager = (NotificationManager) musicService.getSystemService(Context.NOTIFICATION_SERVICE);


        mBufferingAction = new NotificationCompat.Action((int) R.drawable.ic_notification_buffering, (CharSequence) this.mService.getString(R.string.label_buffering), (PendingIntent) null);
        mNextAction = new NotificationCompat.Action((int) R.drawable.ic_step_forward, (CharSequence) this.mService.getString(R.string.label_next), MediaButtonReceiver.buildMediaButtonPendingIntent(this.mService, PlaybackStateCompat.ACTION_SKIP_TO_NEXT));
        mPauseAction = new NotificationCompat.Action((int) R.drawable.ic_mini_pause, (CharSequence) this.mService.getString(R.string.label_pause), MediaButtonReceiver.buildMediaButtonPendingIntent(this.mService, PlaybackStateCompat.ACTION_PAUSE));
        mPlayAction = new NotificationCompat.Action((int) R.drawable.ic_mini_play, (CharSequence) this.mService.getString(R.string.label_play), MediaButtonReceiver.buildMediaButtonPendingIntent(this.mService, PlaybackStateCompat.ACTION_PLAY));
        mPrevAction = new NotificationCompat.Action((int) R.drawable.ic_step_backward, (CharSequence) this.mService.getString(R.string.label_previous), MediaButtonReceiver.buildMediaButtonPendingIntent(this.mService, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        this.mNotificationManager.cancelAll();
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
    }

    public NotificationManager getNotificationManager() {
        return this.mNotificationManager;
    }

    public NotificationCompat.Builder buildNotification(MediaMetadataCompat mediaMetadataCompat, PlaybackStateCompat playbackStateCompat, MediaSessionCompat.Token token) {
        return buildNotification(playbackStateCompat, token, playbackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING, mediaMetadataCompat.getDescription());
    }

    private NotificationCompat.Builder buildNotification(PlaybackStateCompat playbackStateCompat, MediaSessionCompat.Token token, boolean z, MediaDescriptionCompat mediaDescriptionCompat) {
        if (isAndroidOOrHigher()) {
            createChannel();
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.mService, CHANNEL_ID);

        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(token)
                .setShowActionsInCompactView(0, 1, 2)
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this.mService,PlaybackStateCompat.ACTION_STOP )))
                .setColor(ContextCompat.getColor(this.mService, R.color.colorAccent))
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(createContentIntent())
                .setContentTitle(mediaDescriptionCompat.getTitle())
                .setContentText(mediaDescriptionCompat.getSubtitle())
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this.mService, PlaybackStateCompat.ACTION_STOP))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        if ((playbackStateCompat.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0) {
            builder.addAction(this.mPrevAction);
        }
        if (playbackStateCompat.getState() == PlaybackStateCompat.STATE_BUFFERING) {
            builder.addAction(this.mBufferingAction);
        } else if (playbackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
            builder.addAction(this.mPauseAction);
        } else {
            builder.addAction(this.mPlayAction);
        }
        if ((playbackStateCompat.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0) {
            builder.addAction(this.mNextAction);
        }
        return builder;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        if (this.mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "MediaSession", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setDescription("MediaSession and MediaPlayer");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(SupportMenu.CATEGORY_MASK);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            this.mNotificationManager.createNotificationChannel(notificationChannel);
            Log.d(TAG, "createChannel: New channel created");
            return;
        }
        Log.d(TAG, "createChannel: Existing channel reused");
    }

    private boolean isAndroidOOrHigher() {
        return Build.VERSION.SDK_INT >= 26;
    }

    private PendingIntent createContentIntent() {
        Intent intent = new Intent(this.mService, MainActivity.class);
        intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this.mService, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}