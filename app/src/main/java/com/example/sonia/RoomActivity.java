package com.example.sonia;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sonia.data.AppDatabase;
import com.example.sonia.data.DeviceEntity;
import com.example.sonia.data.RoomEntity;
import com.example.sonia.data.RoomWithDevices;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class RoomActivity extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_room);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.room), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });

        db = AppDatabase.getInstance(this);

        Executors.newSingleThreadExecutor().execute(() -> {
            List<RoomEntity> rooms = db.roomDao().getAllRooms();
            if (rooms == null || rooms.size() == 0) {
                db.roomDao().insertRoom(new RoomEntity("Living Room"));
                db.roomDao().insertRoom(new RoomEntity("Bedroom"));
                db.roomDao().insertRoom(new RoomEntity("Kitchen"));
                db.roomDao().insertRoom(new RoomEntity("Bathroom"));
            }
        });

        TextView title = findViewById(R.id.tvRoomName);
        LinearLayout deviceContainer = findViewById(R.id.deviceContainer);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnAdd = findViewById(R.id.addDev);
        Button btnRemove = findViewById(R.id.removeDev);

        String roomName = getIntent().getStringExtra("roomName");
        title.setText(roomName);

        btnBack.setOnClickListener(v -> finish());

        loadDevicesForRoom(roomName, deviceContainer);

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
                    Executors.newSingleThreadExecutor().execute(() -> {
                        RoomEntity room = db.roomDao().getRoomByName(roomName);
                        if (room != null) {
                            DeviceEntity e = new DeviceEntity(room.roomId, selectedDevice, selectedDevice);
                            long id = db.deviceDao().insertDevice(e);
                            e.deviceId = id;
                            runOnUiThread(() -> {
                                addDeviceView(deviceContainer, e);
                                Toast.makeText(this, selectedDevice + " added ", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
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
            final long[] deviceIds = new long[count];
            for (int i = 0; i < count; i++) {
                View deviceView = deviceContainer.getChildAt(i);
                Object tag = deviceView.getTag();
                if (tag instanceof Long) {
                    deviceIds[i] = (Long) tag;
                } else {
                    deviceIds[i] = -1;
                }

                TextView devNameView = null;
                LinearLayout deviceRow = (LinearLayout) ((LinearLayout) deviceView).getChildAt(0);
                for (int j = 0; j < deviceRow.getChildCount(); j++) {
                    View innerView = deviceRow.getChildAt(j);
                    if (innerView instanceof TextView) {
                        devNameView = (TextView) innerView;
                        break;
                    }
                }
                devices[i] = devNameView != null ? devNameView.getText().toString() : ("Device " + (i+1));
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
                    long removeId = deviceIds[index];
                    Executors.newSingleThreadExecutor().execute(() -> {
                        if (removeId != -1) {
                            db.deviceDao().deleteById(removeId);
                        }
                        runOnUiThread(() -> {
                            deviceContainer.removeViewAt(index);
                            Toast.makeText(this, devices[index] + " removed", Toast.LENGTH_SHORT).show();
                        });
                    });
                } else {
                    Toast.makeText(this, "Please select device", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

    }

    private void loadDevicesForRoom(String roomName, LinearLayout deviceContainer) {
        deviceContainer.removeAllViews();

        Executors.newSingleThreadExecutor().execute(() -> {
            RoomEntity room = db.roomDao().getRoomByName(roomName);
            if (room == null) return;

            List<com.example.sonia.data.DeviceEntity> devices = db.deviceDao().getDevicesForRoom(room.roomId);
            runOnUiThread(() -> {
                if (devices != null) {
                    for (com.example.sonia.data.DeviceEntity d : devices) {
                        addDeviceView(deviceContainer, d);
                    }
                }
            });
        });
    }

    private void addDeviceView(LinearLayout container, com.example.sonia.data.DeviceEntity deviceEntity) {
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

        deviceLayout.setTag(deviceEntity.deviceId);

        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        ImageView icon = new ImageView(this);
        LinearLayout.LayoutParams icons = new LinearLayout.LayoutParams(96, 96);
        icons.setMargins(0, 0, 24, 0);
        icon.setLayoutParams(icons);

        String deviceName = deviceEntity.name;

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
            default:
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
        stateLabel.setTextColor(getColor(android.R.color.white));
        stateLabel.setPadding(8, 0, 0, 0);

        if (deviceName.equals("Curtains")) {
            stateLabel.setText(deviceEntity.isOn ? "Open" : "Closed");
        } else {
            stateLabel.setText(deviceEntity.isOn ? "On" : "Off");
        }
        sw.setChecked(deviceEntity.isOn);
        if (deviceEntity.isOn) {
            deviceLayout.setBackgroundResource(R.drawable.room_in_bg_active);
        } else {
            deviceLayout.setBackgroundResource(R.drawable.room_in_bg);
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

            if (deviceName.equals("Lights")) {
                View lightSection = deviceLayout.findViewWithTag("lightSection");
                if (lightSection != null) lightSection.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            } else if (deviceName.equals("AC System")) {
                View acSection = deviceLayout.findViewWithTag("acSection");
                if (acSection != null) acSection.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }

            deviceEntity.isOn = isChecked;
            Executors.newSingleThreadExecutor().execute(() -> db.deviceDao().updateDevice(deviceEntity));

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
            lightSection.setTag("lightSection");

            SeekBar lightSeekBar = new SeekBar(this);
            lightSeekBar.setMax(100);
            lightSeekBar.setProgress(deviceEntity.brightness);

            TextView lightValue = new TextView(this);
            lightValue.setText("Brightness: " + deviceEntity.brightness + "%");
            lightValue.setTextColor(getColor(android.R.color.white));

            lightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    lightValue.setText("Brightness: " + progress + "%");
                    float alpha = 0.5f + (progress / 200f);
                    lightValue.setAlpha(alpha);
                    deviceEntity.brightness = progress;
                    Executors.newSingleThreadExecutor().execute(() -> db.deviceDao().updateDevice(deviceEntity));
                }

                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            lightSection.addView(lightSeekBar);
            lightSection.addView(lightValue);
            lightSection.setVisibility(deviceEntity.isOn ? View.VISIBLE : View.GONE);
            deviceLayout.addView(lightSection);
        }

        if (deviceName.equals("AC System")) {
            LinearLayout acSection = new LinearLayout(this);
            acSection.setOrientation(LinearLayout.VERTICAL);
            acSection.setPadding(0, 16, 0, 0);
            acSection.setTag("acSection");

            Spinner spinner = new Spinner(this);
            spinner.setBackgroundResource(R.drawable.room_in_bg_active);

            List<String> temp = new ArrayList<>();
            int initialPos = 0;
            for (int i = 16; i <= 30; i++){
                temp.add(i + "°C");
                if (i == deviceEntity.temperature) initialPos = i - 16;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    temp
            );

            spinner.setAdapter(adapter);
            spinner.setSelection(initialPos);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                boolean first = true;
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (first) { first = false; return; }
                    String selectedTemp = temp.get(i);
                    int t = 16 + i;
                    deviceEntity.temperature = t;
                    Executors.newSingleThreadExecutor().execute(() -> db.deviceDao().updateDevice(deviceEntity));
                    Toast.makeText(RoomActivity.this, "Temperature set to " + selectedTemp, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {}
            });

            TextView acCheck = new TextView(this);
            acCheck.setText("Choose temperature");
            acCheck.setTextColor(getColor(android.R.color.white));

            acSection.addView(spinner);
            acSection.addView(acCheck);
            acSection.setVisibility(deviceEntity.isOn ? View.VISIBLE : View.GONE);
            deviceLayout.addView(acSection);
        }

        if (deviceName.equals("Security Cameras")){
            LinearLayout cameraSection = new LinearLayout(this);
            cameraSection.setOrientation(LinearLayout.VERTICAL);
            cameraSection.setPadding(0, 16, 0, 0);

            Button button = new Button(this);
            button.setText("Check footage");
            button.setTextColor(Color.WHITE);
            button.setBackgroundResource(R.drawable.room_in_bg_active);

            TextView cameraCheck = new TextView(this);
            cameraCheck.setText("Camera saves recorded footage every 24 hours or until you turn it off.");
            cameraCheck.setTextColor(getColor(android.R.color.white));

            button.setOnClickListener(view ->  {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("No footage in test build of the app")
                        .setTitle("Recorded Footage");
                builder.show();
            });

            cameraSection.addView(button);
            cameraSection.addView(cameraCheck);
            deviceLayout.addView(cameraSection);
        }

        if (deviceName.equals("Washing machine")){
            LinearLayout cameraSection = new LinearLayout(this);
            cameraSection.setOrientation(LinearLayout.VERTICAL);
            cameraSection.setPadding(0, 16, 0, 0);

            Button button = new Button(this);
            button.setText("Check footage");
            button.setTextColor(Color.WHITE);
            button.setBackgroundResource(R.drawable.room_in_bg_active);

            TextView cameraCheck = new TextView(this);
            cameraCheck.setText("Camera saves recorded footage every 24 hours or until you turn it off.");
            cameraCheck.setTextColor(getColor(android.R.color.white));

            button.setOnClickListener(view ->  {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("No footage in test build of the app")
                        .setTitle("Recorded Footage");
                builder.show();
            });

            cameraSection.addView(button);
            cameraSection.addView(cameraCheck);
            deviceLayout.addView(cameraSection);
        }

        container.addView(deviceLayout);
        Animation anim;
        if (container.getChildCount() % 2 == 0) {
            anim = AnimationUtils.loadAnimation(this, R.anim.slide_left);
        } else {
            anim = AnimationUtils.loadAnimation(this, R.anim.slide_right);
        }
        anim.setStartOffset(container.getChildCount() * 200L);
        deviceLayout.startAnimation(anim);
    }
}
