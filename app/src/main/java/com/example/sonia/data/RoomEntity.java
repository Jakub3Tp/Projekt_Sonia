package com.example.sonia.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "rooms")
public class RoomEntity {
    @PrimaryKey(autoGenerate = true)
    public long roomId;

    public String name;

    public RoomEntity(String name) {
        this.name = name;
    }
}
