package com.example.animemusic.asyncTasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.animemusic.dao.AppDatabase;
import com.example.animemusic.models.PlaylistSong;

import java.lang.ref.WeakReference;

public class GetPlaylistSong extends AsyncTask<Void, Void, PlaylistSong> {
    private GetPlaylistSongListener listener;
    private PlaylistSong playlistSong;
    private final WeakReference<Activity> weakActivity;

    @Override
    protected PlaylistSong doInBackground(Void... voids) {
        return AppDatabase.getInstance(weakActivity.get()).getPlaylistSongDao().get(playlistSong.getSongId(), playlistSong.getPlaylistId());
    }

    @Override
    protected void onPostExecute(PlaylistSong playlistSong) {
        super.onPostExecute(playlistSong);
        GetPlaylistSongListener getPlaylistSongListener = this.listener;
        if (getPlaylistSongListener != null) {
            getPlaylistSongListener.onComplete(playlistSong);
        }
    }

    public interface GetPlaylistSongListener {
        void onComplete(PlaylistSong playlistSong);
    }

    public GetPlaylistSong(Activity activity, PlaylistSong playlistSong2, GetPlaylistSongListener getPlaylistSongListener) {
        this.playlistSong = playlistSong2;
        this.listener = getPlaylistSongListener;
        this.weakActivity = new WeakReference<>(activity);
    }

}