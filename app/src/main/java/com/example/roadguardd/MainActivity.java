package com.example.roadguardd;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button driveModeButton;
    Button updateProfileButton;  // Declare the Update Profile button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        driveModeButton = findViewById(R.id.btn_drive_mode);
        updateProfileButton = findViewById(R.id.btn_update_profile);  // Initialize the Update Profile button

        // Set onClickListener for the "Drive Mode" button
        driveModeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TripInfoActivity.class);
            startActivity(intent);
        });

        // Set onClickListener for the "Update Profile" button
        updateProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);  // Intent to navigate to UpdateAccountActivity
            startActivity(intent);
        });
    }
}
