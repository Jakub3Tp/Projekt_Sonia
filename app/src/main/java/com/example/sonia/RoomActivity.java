package com.example.sonia;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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
        Button btnAdd = findViewById(R.id.addDev);
        Button btnRemove = findViewById(R.id.removeDev);

        String roomName = getIntent().getStringExtra("roomName");
        title.setText(roomName);

        btnBack.setOnClickListener(v -> finish());
        btnAdd.setOnClickListener(view -> {
            final String[] availableDevices = {"Lights", "Curtains", "TV", "Induction hob", "Washing machine", "Lock", "Blinds", "AC System", "Security Cameras", "Robot vacuum", "Electric kettle", "Tumble dryer"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add new device");

            final int[] selectedIndex = {-1};

            builder.setSingleChoiceItems(availableDevices, -1, (dialog, which) -> {
                selectedIndex[0] = which;
            });

            builder.setPositiveButton("Add", (dialog, which) ->{
                if (selectedIndex[0] != -1) {
                    String selectedDevice = availableDevices[selectedIndex[0]];
                    device(deviceContainer, selectedDevice);
                    Toast.makeText(this, selectedDevice + " added ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please select device", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        btnRemove.setOnClickListener(v -> {
            int count = deviceContainer.getChildCount();
            if (count == 0) {
                Toast.makeText(this, "No devices to remove", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] devices = new String[count];
            for (int i = 0; i < count; i++) {
                View deviceView = deviceContainer.getChildAt(i);
                TextView devNameView = null;


                LinearLayout deviceRow = (LinearLayout) ((LinearLayout) deviceView).getChildAt(0);
                for (int j = 0; j < deviceRow.getChildCount(); j++) {
                    View innerView = deviceRow.getChildAt(j);
                    if (innerView instanceof TextView) {
                        devNameView = (TextView) innerView;
                        break;
                    }
                }
                devices[i] = devNameView.getText().toString();
            }

            final int[] selectedIndex = {-1};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select device to remove");

            builder.setSingleChoiceItems(devices, -1, (dialog, which) -> {
                selectedIndex[0] = which;
            });

            builder.setPositiveButton("Remove", (dialog, which) -> {
                if (selectedIndex[0] != -1) {
                    int index = selectedIndex[0];
                    deviceContainer.removeViewAt(index);
                    Toast.makeText(this, devices[index] + " removed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please select device", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        if (roomName != null) {
            switch (roomName) {
                case "Living Room":
                    device(deviceContainer, "Lights");
                    device(deviceContainer, "Curtains");
                    device(deviceContainer, "Robot vacuum");
                    break;
                case "Bedroom":
                    device(deviceContainer, "Lights");
                    device(deviceContainer, "TV");
                    device(deviceContainer, "Curtains");
                    break;
                case "Kitchen":
                    device(deviceContainer, "Lights");
                    device(deviceContainer, "Curtains");
                    device(deviceContainer, "Induction hob");
                    device(deviceContainer, "Electric kettle");
                    break;
                case "Bathroom":
                    device(deviceContainer, "Lights");
                    device(deviceContainer, "Washing machine");
                    device(deviceContainer, "Tumble dryer");
                    break;
            }
        }
    }

    private void device(LinearLayout container, String deviceName) {
        LinearLayout deviceLayout = new LinearLayout(this);
        deviceLayout.setOrientation(LinearLayout.VERTICAL);
        deviceLayout.setPadding(24, 24, 24, 24);
        deviceLayout.setBackgroundResource(R.drawable.room_in_bg);

        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setMargins(16, 16, 16, 16);
        deviceLayout.setLayoutParams(layout);

        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

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
            case "Lock":
                icon.setImageResource(R.mipmap.lock);
                break;
            case "Blinds":
                icon.setImageResource(R.mipmap.blinds);
                break;
            case "AC System":
                icon.setImageResource(R.mipmap.air);
                break;
            case "Security Cameras":
                icon.setImageResource(R.mipmap.security);
                break;
            case "Electric kettle":
                icon.setImageResource(R.mipmap.kettle);
                break;
            case "Tumble dryer":
                icon.setImageResource(R.mipmap.dryer);
                break;
            case "Robot vacuum":
                icon.setImageResource(R.mipmap.vacuum);
                break;

        }

        TextView name = new TextView(this);
        name.setText(deviceName);
        name.setTextColor(getColor(android.R.color.white));
        name.setTextSize(18);
        name.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch sw = new Switch(this);
        TextView stateLabel = new TextView(this);
        stateLabel.setText("Off");
        stateLabel.setTextColor(getColor(android.R.color.white));
        stateLabel.setPadding(8, 0, 0, 0);

        if (deviceName.equals("Curtains")) {
            stateLabel.setText("Closed");
        } else {
            stateLabel.setText("Off");
        }

        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String state;
            if (deviceName.equals("Curtains") || deviceName.equals("Lock")) {
                state = isChecked ? "Open" : "Closed";
            } else {
                state = isChecked ? "On" : "Off";
            }

            if (isChecked) {
                deviceLayout.setBackgroundResource(R.drawable.room_in_bg_active);
            } else {
                deviceLayout.setBackgroundResource(R.drawable.room_in_bg);
            }

            stateLabel.setText(state);
            if (deviceName.equals("Curtains") || deviceName.equals("Lock")){
                Toast.makeText(this, deviceName + " " + state, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, deviceName + " turned " + state, Toast.LENGTH_SHORT).show();
            }
        });

        topRow.addView(icon);
        topRow.addView(name);
        topRow.addView(sw);
        topRow.addView(stateLabel);
        deviceLayout.addView(topRow);

        if (deviceName.equals("Lights")) {
            LinearLayout lightSection = new LinearLayout(this);
            lightSection.setOrientation(LinearLayout.VERTICAL);
            lightSection.setPadding(0, 16, 0, 0);

            SeekBar lightSeekBar = new SeekBar(this);
            lightSeekBar.setMax(100);
            lightSeekBar.setProgress(50);

            TextView lightValue = new TextView(this);
            lightValue.setText("Brightness: 50%");
            lightValue.setTextColor(getColor(android.R.color.white));

            lightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    lightValue.setText("Brightness: " + progress + "%");
                    float alpha = 0.5f + (progress / 200f);
                    lightValue.setAlpha(alpha);
                }

                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            lightSection.addView(lightSeekBar);
            lightSection.addView(lightValue);
            deviceLayout.addView(lightSection);
        }

        container.addView(deviceLayout);
    }
}
