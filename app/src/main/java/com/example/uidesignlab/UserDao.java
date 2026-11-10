package com.example.uidesignlab;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import android.database.Cursor;

@Dao
public interface UserDao {

    @Insert
    long insert(UserRecord user);

    @Update
    int update(UserRecord user);

    @Delete
    int delete(UserRecord user);

    @Query("SELECT * FROM UserRecord ORDER BY id DESC")
    List<UserRecord> getAll();

    @Query("DELETE FROM UserRecord")
    int deleteAll();
    @Query("SELECT * FROM UserRecord ORDER BY id DESC")
    Cursor getAllCursor();

    @Query("SELECT * FROM UserRecord WHERE id = :id")
    Cursor getByIdCursor(long id);

    @Query("UPDATE UserRecord SET name = :name WHERE id = :id")
    int updateName(long id, String name);

    @Query("DELETE FROM UserRecord WHERE id = :id")
    int deleteById(long id);
}