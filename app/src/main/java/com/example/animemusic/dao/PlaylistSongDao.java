package com.example.animemusic.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.animemusic.models.PlaylistSong;

import java.util.List;

@Dao
public interface PlaylistSongDao {

    @Query("SELECT * FROM playlistsong WHERE songId LIKE :songId AND playlistId LIKE :playlistId")
    PlaylistSong get(String songId, int playlistId);


    @Query("SELECT * FROM PlaylistSong")
    List<PlaylistSong> getAll();

    @Insert
    void insert(PlaylistSong playlistSong);

    @Delete
    void delete(PlaylistSong playlistSong);

    @Update
    void update(PlaylistSong playlistSong);

    @Query("DELETE FROM PlaylistSong WHERE playlistId LIKE :id")
    void deleteAllByPlaylistId(int id);


}
