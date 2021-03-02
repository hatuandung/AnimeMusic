package com.example.animemusic.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.animemusic.models.Song;
import com.example.animemusic.service.MusicLibrary;
import com.example.animemusic.utils.SharedPrefHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SaveQueueAsync extends AsyncTask<Void, Void, Void> {
    private Listener listener;
    private int position;
    private List<Song> songs;
    private final WeakReference<Context> weakActivity;

    @Override
    protected Void doInBackground(Void... voids) {
        Context context = (Context) weakActivity.get();
        SharedPrefHelper.getInstance(context).saveQueue(songs);
        SharedPrefHelper.getInstance(context).saveQueueIndex(position);
        MusicLibrary.setQueueItems(new ArrayList());
        if (context == null) {
            return null;
        }
//        for (Song createAndPutMediaMetadata : this.songs) {
//            MusicLibrary.createAndPutMediaMetadata(createAndPutMediaMetadata);
//        }
        for (int i = 0; i < songs.size(); i++) {
            MusicLibrary.createAndPutMediaMetadata(songs.get(i));
        }
        return null;
    }


    public interface Listener {
        void onPostExecute();
    }

    public SaveQueueAsync(Context context, List<Song> list, int i) {
        this.songs = list;
        this.position = i;
        this.weakActivity = new WeakReference<>(context);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Listener listener2 = this.listener;
        if (listener2 != null) {
            listener2.onPostExecute();
        }
    }

}
