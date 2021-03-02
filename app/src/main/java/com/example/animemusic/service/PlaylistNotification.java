package com.example.animemusic.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.animemusic.R;
import com.example.animemusic.activity.MainActivity;
import com.example.animemusic.interfaces.NotificationType;

import java.util.Map;

public class PlaylistNotification extends BaseNotification {
    public static final String PLAYLIST_DESC = "playlist_description";
    public static final String PLAYLIST_ID = "playlist_id";
    public static final String PLAYLIST_NAME = "playlist_name";
    public static final String PLAYLIST_THUMBNAIL = "playlist_thumbnail";

    public PlaylistNotification(Context context) {
        super(context);
    }

    public void create(Map<String, String> map) {
        final NotificationCompat.Builder createBaseNotificationBuilder = createBaseNotificationBuilder();
        createBaseNotificationBuilder.setContentIntent(createContentIntent(map));
        createBaseNotificationBuilder.setContentTitle(map.get(PLAYLIST_NAME));
        createBaseNotificationBuilder.setContentText(map.get(PLAYLIST_DESC));
        int dimension = (int) this.context.getResources().getDimension(R.dimen.image_xs_size);
        Glide.with(this.context).asBitmap().load("https://lh3.googleusercontent.com/pw/ACtC-3flq_NcDF8Pu4waiMcyOlmboFLzf1QX0G-jumYNDuqYlfS4udfyj4dfF7qsld-jN7647zngStTwno3ioJt92tlsRs-mdylYTPuSZkJURrolIdUFDpDQsts7PRX7i2dWAa3-9GzlhcpoUpRbc4gM-nZr=w524-h409-no?authuser=0").into(new CustomTarget<Bitmap>(dimension, dimension) {
            public void onLoadCleared(Drawable drawable) {
            }

            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                createBaseNotificationBuilder.setLargeIcon(bitmap);
                createBaseNotificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
                PlaylistNotification.this.getNotificationManager().notify(0, createBaseNotificationBuilder.build());
            }

            public void onLoadFailed(Drawable drawable) {
                super.onLoadFailed(drawable);
            }
        });
    }

    private PendingIntent createContentIntent(Map<String, String> map) {
        Intent intent = new Intent(this.context, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("type", NotificationType.PLAYLIST.getValue());
        bundle.putString(PLAYLIST_ID, map.get(PLAYLIST_ID));
        bundle.putString(PLAYLIST_NAME, map.get(PLAYLIST_NAME));
        bundle.putString(PLAYLIST_THUMBNAIL, map.get(PLAYLIST_THUMBNAIL));
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        return PendingIntent.getActivity(this.context, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}