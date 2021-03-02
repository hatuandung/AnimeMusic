package com.example.animemusic.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.animemusic.R;

import java.util.Map;

public abstract class BaseNotification {
    protected static final int REQUEST_CODE = 502;
    protected Context context;
    private NotificationManager notificationManager;

    public abstract void create(Map<String, String> map);

    public BaseNotification(Context context) {
        this.context = context;
    }

    /* access modifiers changed from: protected */
    public NotificationCompat.Builder createBaseNotificationBuilder() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify_001");
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setPriority(2);
        builder.setAutoCancel(true);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            notificationManager.createNotificationChannel(new NotificationChannel("Your_channel_id", "channel_name_notification", NotificationManager.IMPORTANCE_HIGH));
            builder.setChannelId("Your_channel_id");
        }
        return builder;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }
}
