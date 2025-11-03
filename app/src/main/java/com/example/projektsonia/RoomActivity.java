package com.example.projektsonia;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class RoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_room);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.room), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });

        TextView title = findViewById(R.id.tvRoomName);
        LinearLayout deviceContainer = findViewById(R.id.deviceContainer);
        Button btnBack = findViewById(R.id.btnBack);

        String roomName = getIntent().getStringExtra("roomName");
        title.setText(roomName);

        btnBack.setOnClickListener(v -> finish());

        if (roomName != null) {
            if (roomName.equals("Living Room")) {
                device(deviceContainer, "Lights");
                device(deviceContainer, "Curtains");
            } else if (roomName.equals("Bedroom")) {
                device(deviceContainer, "Lights");
                device(deviceContainer, "TV");
                device(deviceContainer, "Curtains");
            } else if (roomName.equals("Kitchen")) {
                device(deviceContainer, "Lights");
                device(deviceContainer, "Curtains");
                device(deviceContainer, "Induction hob");
            } else if (roomName.equals("Bathroom")) {
                device(deviceContainer, "Lights");
                device(deviceContainer, "Washing machine");
            }
        }
    }

    private void device(LinearLayout container, String deviceName) {
        LinearLayout deviceLayout = new LinearLayout(this);
        deviceLayout.setOrientation(LinearLayout.HORIZONTAL);
        deviceLayout.setGravity(Gravity.CENTER_VERTICAL);
        deviceLayout.setPadding(24, 24, 24, 24);

        deviceLayout.setBackgroundResource(R.drawable.room_in_bg);

        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        layout.setMargins(16, 16, 16, 16);
        deviceLayout.setLayoutParams(layout);

        ImageView icon = new ImageView(this);
        LinearLayout.LayoutParams icons = new LinearLayout.LayoutParams(96, 96);
        icons.setMargins(0, 0, 24, 0);
        icon.setLayoutParams(icons);

        switch (deviceName) {
            case "Lights":
                icon.setImageResource(R.mipmap.lights);
                break;
            case "Curtains":
                icon.setImageResource(R.mipmap.curtains);
                break;
            case "TV":
                icon.setImageResource(R.mipmap.television);
                break;
            case "Induction hob":
                icon.setImageResource(R.mipmap.induction);
                break;
            case "Washing machine":
                icon.setImageResource(R.mipmap.washing);
                break;
        }

        TextView name = new TextView(this);
        name.setText(deviceName);
        name.setTextColor(getColor(android.R.color.white));
        name.setTextSize(18);
        name.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch sw = new Switch(this);
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                deviceLayout.setBackgroundResource(R.drawable.room_in_bg_active);
            } else {
                deviceLayout.setBackgroundResource(R.drawable.room_in_bg);
            }
            String state = isChecked ? "ON" : "OFF";
            Toast.makeText(this, deviceName + " turned " + state, Toast.LENGTH_SHORT).show();

        });

        deviceLayout.addView(icon);
        deviceLayout.addView(name);
        deviceLayout.addView(sw);
        container.addView(deviceLayout);
    }
}