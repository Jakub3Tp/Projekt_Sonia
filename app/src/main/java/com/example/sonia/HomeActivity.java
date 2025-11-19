package com.example.sonia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sonia.data.AppDatabase;
import com.example.sonia.data.RoomEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {

    private GridLayout gridRooms;
    private Uri selectedImageUri;
    private ImageView imagePrev;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        LinearLayout container = findViewById(R.id.main);
        container.post(() -> {
            int count = container.getChildCount();

            for (int i = 0; i < count; i++){
                View item = container.getChildAt(i);

                Animation anim;
                if (i % 2 == 0) {
                    anim = AnimationUtils.loadAnimation(this, R.anim.slide_left);
                } else {
                    anim = AnimationUtils.loadAnimation(this, R.anim.slide_right);
                }
                anim.setStartOffset(i * 250L);
                item.startAnimation(anim);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });

        db = AppDatabase.getInstance(this);
        gridRooms = findViewById(R.id.gridRooms);
        Button button = findViewById(R.id.add);

        button.setOnClickListener(view -> openAddroom());
        loadRoomsFromDatabase();
    }

    private void loadRoomsFromDatabase() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<RoomEntity> rooms = db.roomDao().getAllRooms();

            if (rooms.isEmpty()) {
                RoomEntity living = new RoomEntity("Living Room", "bg_livingroom", true);
                RoomEntity bedroom = new RoomEntity("Bedroom", "bg_bedroom", true);
                RoomEntity kitchen = new RoomEntity("Kitchen", "bg_kitchen", true);
                RoomEntity bathroom = new RoomEntity("Bathroom", "bg_bathroom", true);

                db.roomDao().insertRoom(living);
                db.roomDao().insertRoom(bedroom);
                db.roomDao().insertRoom(kitchen);
                db.roomDao().insertRoom(bathroom);

                rooms = db.roomDao().getAllRooms();
            }

            List<RoomEntity> finalRooms = rooms;
            runOnUiThread(() -> {
                gridRooms.removeAllViews();
                for (RoomEntity room : finalRooms) {
                    addRoomToGrid(room);
                }
            });
        });
    }


    private void openAddroom() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_room, null);

        EditText roomName = dialogView.findViewById(R.id.roomName);
        imagePrev = dialogView.findViewById(R.id.imagePrev);
        Button btnImage = dialogView.findViewById(R.id.btnImage);

        btnImage.setOnClickListener(v -> pickImage.launch("image/*"));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New Room")
                .setView(dialogView)
                .setPositiveButton("Add", (d, which) -> {
                    String name = roomName.getText().toString().trim();
                    if (name.isEmpty() || selectedImageUri == null) {
                        Toast.makeText(this,
                                "Please enter a room name and image",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String savedPath = copyImageToInternalStorage(selectedImageUri);
                    if (savedPath != null) {
                        saveRoomToDatabase(name, savedPath, false);
                    } else {
                        Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
                    }

                    selectedImageUri = null;
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    if (imagePrev != null)
                        imagePrev.setImageURI(uri);
                }
            });

    private String copyImageToInternalStorage(Uri uri) {
        try {
            InputStream input = getContentResolver().openInputStream(uri);
            if (input == null) return null;

            File file = new File(getFilesDir(), "room_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream output = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }

            input.close();
            output.flush();
            output.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveRoomToDatabase(String name, String imageUri, boolean isResource) {
        RoomEntity room = new RoomEntity(name, imageUri, isResource);

        Executors.newSingleThreadExecutor().execute(() -> {
            long id = db.roomDao().insertRoom(room);
            runOnUiThread(() -> {
                if (id > 0) {
                    addRoomToGrid(room);
                    Toast.makeText(this, "Room added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to add room", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void addRoomToGrid(RoomEntity room) {
        LinearLayout newRoom = (LinearLayout)
                getLayoutInflater().inflate(R.layout.room_card_add, gridRooms, false);

        ImageView image = newRoom.findViewById(R.id.roomImage);
        TextView title = newRoom.findViewById(R.id.roomTitle);
        title.setText(room.name);

        if (room.isResource) {
            int resId = getResources().getIdentifier(room.imageUri, "mipmap", getPackageName());
            image.setImageResource(resId);
        } else {
            File file = new File(room.imageUri);
            if (file.exists()) {
                image.setImageURI(Uri.fromFile(file));
            }
        }

        newRoom.setOnClickListener(view -> {
            Intent intent = new Intent(this, RoomActivity.class);
            intent.putExtra("roomName", room.name);
            startActivity(intent);
        });

        gridRooms.addView(newRoom);
    }
}
