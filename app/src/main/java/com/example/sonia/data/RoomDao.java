package com.example.sonia.data;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface RoomDao {
    @Insert
    long insertRoom(RoomEntity room);

    @Query("SELECT * FROM rooms")
    List<RoomEntity> getAllRooms();

    @Query("SELECT * FROM rooms WHERE name = :name LIMIT 1")
    RoomEntity getRoomByName(String name);

    @Transaction
    @Query("SELECT * FROM rooms WHERE roomId = :id")
    RoomWithDevices getRoomWithDevices(long id);
}


