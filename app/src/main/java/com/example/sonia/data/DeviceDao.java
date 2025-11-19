package com.example.sonia.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DeviceDao {
    @Insert
    long insertDevice(DeviceEntity device);

    @Update
    void updateDevice(DeviceEntity device);

    @Delete
    void deleteDevice(DeviceEntity device);

    @Query("DELETE FROM devices WHERE roomOwnerId = :roomId")
    void deleteDevicesForRoom(long roomId);

    @Query("SELECT * FROM devices WHERE roomOwnerId = :roomId")
    List<DeviceEntity> getDevicesForRoom(long roomId);

    @Query("DELETE FROM devices WHERE deviceId = :id")
    void deleteById(long id);
}
