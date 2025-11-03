package com.example.projektsonia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
        }

        startActivity(intent);
    }
}