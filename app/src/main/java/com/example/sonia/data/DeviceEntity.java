package com.example.sonia.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "devices",
        foreignKeys = @ForeignKey(entity = RoomEntity.class,
                parentColumns = "roomId",
                childColumns = "roomOwnerId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("roomOwnerId")})
public class DeviceEntity {
    @PrimaryKey(autoGenerate = true)
    public long deviceId;

    public long roomOwnerId;

    public String name;
    public String type;

    public boolean isOn;

    public int brightness;
    public int temperature;
    public int position;

    public DeviceEntity(long roomOwnerId, String name, String type) {
        this.roomOwnerId = roomOwnerId;
        this.name = name;
        this.type = type;
        this.isOn = false;
        this.brightness = 50;
        this.temperature = 22;
        this.position = 0;
    }
}


