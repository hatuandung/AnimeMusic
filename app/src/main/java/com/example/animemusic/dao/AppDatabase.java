package com.example.animemusic.dao;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.animemusic.models.Playlist;
import com.example.animemusic.models.PlaylistSong;
import com.example.animemusic.models.Song;

@Database(entities = {Song.class, Playlist.class, PlaylistSong.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase appDatabase;

    public static AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(
                    context,
                    AppDatabase.class,
                    "database"
            ).addCallback(sRoomDatabaseCallback).allowMainThreadQueries().build();
        }
        return appDatabase;

    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Playlist playlist = new Playlist();
                    playlist.setId(1);
                    playlist.setName("Favorite");
                    playlist.setThumbnailUrl(null);
                    AppDatabase.getInstance(null).getPlaylistDao().insert(playlist);
                }
            }).start();
        }
    };

    public abstract SongDao getSongDao();

    public abstract PlaylistDao getPlaylistDao();

    public abstract PlaylistSongDao getPlaylistSongDao();


}
