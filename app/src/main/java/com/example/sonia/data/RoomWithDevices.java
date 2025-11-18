package com.example.sonia.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class RoomWithDevices {
    @Embedded
    public RoomEntity room;

    @Relation(parentColumn = "roomId", entityColumn = "roomOwnerId")
    public List<DeviceEntity> devices;
}
