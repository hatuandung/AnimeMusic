package com.example.animemusic.service;

import android.content.Context;

import com.example.animemusic.interfaces.NotificationType;

public class NotificationBuilder {
    public static BaseNotification create(Context context, String str) {

        if (str.equals(NotificationType.PLAYLIST.getValue())) {
            return new PlaylistNotification(context);
        }
        return null;
    }


}
