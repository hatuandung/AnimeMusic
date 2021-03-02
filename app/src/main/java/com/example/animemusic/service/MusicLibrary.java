package com.example.animemusic.service;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.Toast;

import com.example.animemusic.R;
import com.example.animemusic.models.Song;
import com.example.animemusic.utils.QueueManager;
import com.example.animemusic.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import static android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE;

public class MusicLibrary {
    public static final int DEFAULT_QUEUE_INDEX = -1;
    public static final String METADATA_KEY_MEDIA_LOCAL = "android.media.metadata.MEDIA_LOCAL";
    public static final String METADATA_KEY_MEDIA_STREAM_TYPE = "android.media.metadata.MEDIA_STREAM_TYPE";
    public static final String METADATA_KEY_MEDIA_TYPE = "android.media.metadata.MEDIA_TYPE";
    private static LinkedHashMap<String, MediaMetadataCompat> mMediaMetadataMap = new LinkedHashMap<>(16, 0.75f, true);
    private static List<MediaSessionCompat.QueueItem> mQueueItems = new ArrayList();

    public static String getRoot() {
        return "root";
    }

    public static void initQueue(Context context) {
        for (Song createAndPutMediaMetadata : SharedPrefHelper.getInstance(context).getQueue()) {
            createAndPutMediaMetadata(createAndPutMediaMetadata);
        }
    }

    public static List<MediaBrowserCompat.MediaItem> getMediaItems() {
        ArrayList arrayList = new ArrayList();
        for (MediaMetadataCompat description : mMediaMetadataMap.values()) {
            arrayList.add(new MediaBrowserCompat.MediaItem(description.getDescription(), FLAG_PLAYABLE));
        }
        return arrayList;
    }

    public static List<MediaSessionCompat.QueueItem> getQueueItems() {
        return mQueueItems;
    }

    public static void setQueueItems(ArrayList<MediaSessionCompat.QueueItem> arrayList) {
        mQueueItems = arrayList;
    }

    public static MediaMetadataCompat getMediaMetadata(Context context, String str) {
        MediaMetadataCompat mediaMetadataCompat = mMediaMetadataMap.get(str);
        if (mediaMetadataCompat == null) {
            return null;
        }
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        String[] strArr = {MediaMetadataCompat.METADATA_KEY_MEDIA_ID, MediaMetadataCompat.METADATA_KEY_MEDIA_URI, MediaMetadataCompat.METADATA_KEY_TITLE, MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, MediaMetadataCompat.METADATA_KEY_ART_URI, MediaMetadataCompat.METADATA_KEY_ALBUM, MediaMetadataCompat.METADATA_KEY_ARTIST, MediaMetadataCompat.METADATA_KEY_GENRE, METADATA_KEY_MEDIA_STREAM_TYPE, METADATA_KEY_MEDIA_TYPE};
        for (int i = 0; i < 11; i++) {
            String str2 = strArr[i];
            builder.putString(str2, mediaMetadataCompat.getString(str2));
        }
        builder.putLong(METADATA_KEY_MEDIA_LOCAL, mediaMetadataCompat.getLong(METADATA_KEY_MEDIA_LOCAL));
        builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaMetadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        return builder.build();
    }

    public static MediaMetadataCompat createAndPutMediaMetadata(Song song) {
        MediaMetadataCompat build = new MediaMetadataCompat.Builder().putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.getId()).putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getTitle()).putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, song.getDescription()).putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, song.getStreamUrl()).putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, song.getDisplayIconUrl()).putString(MediaMetadataCompat.METADATA_KEY_ART_URI, song.getArtworkUrl()).putLong(MediaMetadataCompat.METADATA_KEY_DURATION, (long) song.getDuration()).putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getAlbum()).putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getArtist()).putString(MediaMetadataCompat.METADATA_KEY_GENRE, song.getGenre()).putString(METADATA_KEY_MEDIA_STREAM_TYPE, song.getStreamType()).putString(METADATA_KEY_MEDIA_TYPE, song.getMediaType()).putLong(METADATA_KEY_MEDIA_LOCAL, (long) song.getIsLocal()).build();
        MediaDescriptionCompat description = build.getDescription();
        MediaSessionCompat.QueueItem queueItem = new MediaSessionCompat.QueueItem(description, (long) description.hashCode());
        mMediaMetadataMap.put(song.getId(), build);
        mQueueItems.add(queueItem);
        return build;
    }

    public static int getQueueIndex(Context context) {
        return SharedPrefHelper.getInstance(context).getQueueIndex(-1);
    }

    public static void nextQueueIndex(Context context) {
        SharedPrefHelper.getInstance(context).nextQueueIndex();
    }

    public static void prevQueueIndex(Context context) {
        SharedPrefHelper.getInstance(context).prevQueueIndex();
    }

    public static void setQueueIndex(Context context, int i) {
        SharedPrefHelper.getInstance(context).saveQueueIndex(i);
    }

    public static void randomQueueIndex(Context context) {
        int queueIndex = getQueueIndex(context);
        int i = queueIndex;
        while (i == queueIndex) {
            i = new Random().nextInt(mQueueItems.size());
        }
        setQueueIndex(context, i);
    }

    public static void setRepeatMode(Context context, int i) {
        SharedPrefHelper.getInstance(context).setRepeatMode(i);
    }

    public static int getRepeatMode(Context context) {
        return SharedPrefHelper.getInstance(context).getRepeatMode(2);
    }

    public static void setShuffleMode(Context context, int i) {
        SharedPrefHelper.getInstance(context).setShuffleMode(i);
    }

    public static int getShuffleMode(Context context) {
        return SharedPrefHelper.getInstance(context).getShuffleMode(0);
    }

    public static void shuffle(Context context) {
        List<Song> queue = SharedPrefHelper.getInstance(context).getQueue();
        if (queue.size() == 0) {
            Toast.makeText(context, context.getText(R.string.something_wrong), Toast.LENGTH_LONG).show();
            return;
        }
        int queueIndex = getQueueIndex(context);
        QueueManager instance = QueueManager.getInstance();
        QueueManager.ShufflePlaylist shuffle = instance.shuffle(queue, queueIndex);
        instance.handleSaveQueue(context, shuffle.songs, shuffle.position);
    }
}