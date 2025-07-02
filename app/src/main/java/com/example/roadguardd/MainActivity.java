package com.example.roadguardd;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button driveModeButton;
    Button updateProfileButton;
    Button viewAccidentsButton;
    ImageButton logoutButton; // Exit icon button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        driveModeButton = findViewById(R.id.btn_drive_mode);
        updateProfileButton = findViewById(R.id.btn_update_profile);
        viewAccidentsButton = findViewById(R.id.btn_accident_prone);
        logoutButton = findViewById(R.id.btn_logout); // Top-right icon

        // "Drive Mode" button
        driveModeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TripInfoActivity.class);
            intent.putExtra("ic", getIntent().getStringExtra("ic"));
            startActivity(intent);
        });

        // "Update Profile" button
        updateProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("ic", getIntent().getStringExtra("ic"));
            intent.putExtra("name", getIntent().getStringExtra("name"));
            intent.putExtra("phone", getIntent().getStringExtra("phone"));
            intent.putExtra("password", getIntent().getStringExtra("password"));
            startActivity(intent);
        });

        // "Accident-Prone Areas" button
        viewAccidentsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AccidentProneActivity.class);
            startActivity(intent);
        });

        // Logout icon (top-right)
        logoutButton.setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Log Out")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}
