package com.example.animemusic.utils;

import android.app.Activity;
import android.content.Intent;

import com.example.animemusic.models.Song;

import java.lang.ref.WeakReference;
import java.util.Locale;

public class Helper {
    public static String durationToString(int i) {
        if (i < 0) {
            i = 0;
        }
        int i2 = i / 1000;
        return String.format(Locale.US, "%02d:%02d", new Object[]{Integer.valueOf(i2 / 60), Integer.valueOf(i2 % 60)});
    }

    public static String minuteToString(int i) {
        return String.format("%02d'", new Object[]{Integer.valueOf(i)});
    }

    public static void shareSong(Song song, WeakReference<Activity> weakReference) {
        Activity activity = (Activity) weakReference.get();
        String str = song.getTitle() + "\n\nDownload app at: https://play.google.com/store/apps/details?id=" + activity.getPackageName();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.TEXT", str);
        intent.setType("text/plain");
        activity.startActivityForResult(intent, 126);
    }

}