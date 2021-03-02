package com.example.animemusic.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.animemusic.models.Song;

import java.util.List;
@Dao
public interface SongDao {
    @Query("SELECT * FROM Song")
    List<Song> getAll();

    @Query("SELECT Song.* FROM Song INNER JOIN PlaylistSong ON Song.id = PlaylistSong.songId INNER JOIN Playlist ON Playlist.id = PlaylistSong.playlistId WHERE Playlist.id LIKE :id LIMIT :limit OFFSET :offset")
    List<Song> getByPlaylistId(int id, int limit, int offset);


    @Query("SELECT * FROM Song WHERE id LIKE :id")
    List<Song> getSongById(String id);

    @Insert
    void insert(Song... songs);

    @Update
    void update(Song... song);

    @Delete
    void delete(Song... song);

    @Query("DELETE FROM Song")
    void deleteAll();
}
