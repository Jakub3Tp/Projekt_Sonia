package com.example.sonia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {
    private GridLayout gridRooms;
    private Uri selectedImageUri;
    private ImageView imagePrev;

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
        Button button = findViewById(R.id.add);
        gridRooms = findViewById(R.id.gridRooms);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddroom();
            }
        });
    }
    private void openAddroom(){
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
                    if (name.isEmpty() || selectedImageUri == null){
                        Toast.makeText(this, "Please enter a room name and image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    addRoom(name, selectedImageUri);
                    selectedImageUri = null;
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        if (uri != null){
            selectedImageUri = uri;
            if (imagePrev != null)
                imagePrev.setImageURI(uri);

        }
    });
    private void addRoom(String room, Uri imageUri) {
        LinearLayout newRoom = (LinearLayout) getLayoutInflater().inflate(R.layout.room_card_add, gridRooms, false);
        ImageView image = newRoom.findViewById(R.id.roomImage);
        TextView title = newRoom.findViewById(R.id.roomTitle);

        image.setImageURI(imageUri);
        title.setText(room);
        newRoom.setId(View.generateViewId());

        newRoom.setOnClickListener(view -> {
            Intent intent = new Intent(this, RoomActivity.class);
            intent.putExtra("roomName", room);
            startActivity(intent);
        });
        gridRooms.addView(newRoom);
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