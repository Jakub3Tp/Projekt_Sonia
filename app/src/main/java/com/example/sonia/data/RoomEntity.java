package com.example.sonia.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "rooms")
public class RoomEntity {
    @PrimaryKey(autoGenerate = true)
    public long roomId;

    public String name;
    public String imageUri;   // путь к файлу или название ресурса
    public boolean isResource = false; // true если ресурс из mipmap

    // Конструктор для Room
    public RoomEntity(String name, String imageUri, boolean isResource) {
        this.name = name;
        this.imageUri = imageUri;
        this.isResource = isResource;
    }

    // Удобный конструктор для галерейных картинок
    @Ignore
    public RoomEntity(String name, String imageUri) {
        this.name = name;
        this.imageUri = imageUri;
        this.isResource = false;
    }
}
