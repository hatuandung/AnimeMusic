package com.example.animemusic.utils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.media.session.MediaControllerCompat;

import com.example.animemusic.asyncTasks.SaveQueueAsync;
import com.example.animemusic.models.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueueManager {
    private static QueueManager instance;
    private List<SaveQueueAsync> saveQueues = new ArrayList();

    public static synchronized QueueManager getInstance() {
        QueueManager queueManager;
        synchronized (QueueManager.class) {
            if (instance == null) {
                instance = new QueueManager();
            }
            queueManager = instance;
        }
        return queueManager;
    }

    public void clear() {
        for (SaveQueueAsync listener : saveQueues) {
            listener.setListener(null);
        }
        saveQueues = new ArrayList();
    }

    public ShufflePlaylist shuffle(List<Song> list, int i) {
        ArrayList arrayList = new ArrayList(list);
        Song song = (Song) arrayList.get(i);
        Collections.shuffle(arrayList);
        arrayList.remove(song);
        arrayList.add(0, song);
        return new ShufflePlaylist(arrayList, 0);
    }

    public void handleSaveQueue(Context context, List<Song> list, int i) {
        saveQueues.add(new SaveQueueAsync(context, list, i));
        if (saveQueues.size() > 1) {
            for (int i2 = 0; i2 < saveQueues.size() - 1; i2++) {
                if (saveQueues.get(i2).getStatus() == AsyncTask.Status.RUNNING) {
                    saveQueues.get(i2).cancel(true);
                }
                saveQueues.get(i2).setListener((SaveQueueAsync.Listener) null);
                List<SaveQueueAsync> list2 = saveQueues;
                list2.remove(list2.get(i2));
            }
        }
        List<SaveQueueAsync> list3 = saveQueues;
        list3.get(list3.size() - 1).execute();
    }

    public void handleSaveQueueAndPlaySong(Activity activity, List<Song> list, int i) {
        SaveQueueAsync saveQueueAsync = new SaveQueueAsync(activity, list, i);

        saveQueueAsync.setListener(new SaveQueueAsync.Listener() {
            @Override
            public void onPostExecute() {
                if (activity != null && MediaControllerCompat.getMediaController(activity) != null){
                    //Log.e("onPostExecute: ", "saveQueueAsync");
                    //MediaControllerCompat.getMediaController(activity).getTransportControls().sendCustomAction(MusicService.CUSTOM_ACTION_STOP_AND_PLAY, new Bundle());
                    MediaControllerCompat.getMediaController(activity).getTransportControls().stop();
                    MediaControllerCompat.getMediaController(activity).getTransportControls().prepare();
                    MediaControllerCompat.getMediaController(activity).getTransportControls().play();


                }
            }
        });
        saveQueues.add(saveQueueAsync);

        if (saveQueues.size() >= 1) {
            for (int i2 = 0; i2 < saveQueues.size() - 1; i2++) {
                if (saveQueues.get(i2).getStatus() == AsyncTask.Status.RUNNING) {
                    saveQueues.get(i2).cancel(true);
                }
                saveQueues.get(i2).setListener(null);
                List<SaveQueueAsync> list2 = saveQueues;
                list2.remove(list2.get(i2));
            }
        }
        List<SaveQueueAsync> list3 = saveQueues;
        list3.get(list3.size() - 1).execute();

        //MediaControllerCompat.getMediaController(activity).getTransportControls().sendCustomAction(MusicService.CUSTOM_ACTION_STOP_AND_PLAY, new Bundle());

    }


    public static class ShufflePlaylist {
        public int position;
        public List<Song> songs;

        ShufflePlaylist(List<Song> list, int i) {
            this.position = i;
            this.songs = list;
        }
    }

}
