package com.example.animemusic.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.animemusic.models.Playlist;

import java.util.List;

@Dao
public interface PlaylistDao {
    @Query("SELECT * FROM Playlist")
    List<Playlist> getAll();

    @Query("DELETE FROM Playlist")
    void deleteAll();

    @Insert
    void insert(Playlist... playlist);

    @Update
    void update(Playlist... playlist);

    @Delete
    void delete(Playlist... playlist);

    @Query("SELECT * FROM Playlist WHERE id LIKE :id")
    Playlist getById(int id);

    @Query("DELETE FROM Playlist WHERE id LIKE :id")
    void deleteById(int id);

    @Query("SELECT * FROM Playlist WHERE id NOT IN (1)")
    List<Playlist> getAllExceptFavorite();

    @Query("SELECT songsCount FROM Playlist WHERE id LIKE :id")
    int getSongCount(int id);

    @Query("UPDATE playlist SET songsCount = :count WHERE id LIKE :id")
    void updateCountById(int count , int id);


}
