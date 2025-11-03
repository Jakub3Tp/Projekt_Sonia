package com.example.sonia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });
    }
    public void openRoom(View view){
        Intent intent = new Intent(this, RoomActivity.class);

        int id = view.getId();
        if (id == R.id.bedroomCard) {
            intent.putExtra("roomName", "Bedroom");
        } else if (id == R.id.livingCard) {
            intent.putExtra("roomName", "Living Room");
        }else if (id == R.id.kitchenCard) {
            intent.putExtra("roomName", "Kitchen");
        } else if (id == R.id.bathroomCard) {
            intent.putExtra("roomName", "Bathroom");
        }

        startActivity(intent);
    }
}