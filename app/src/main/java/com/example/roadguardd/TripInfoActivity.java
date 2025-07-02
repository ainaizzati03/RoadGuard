package com.example.roadguardd;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TripInfoActivity extends AppCompatActivity {

    EditText vehicleBrand, vehicleReg, vehicleColor, noPass,
            seniorCitizen, adult, children, infant, addDetail;
    Button saveButton;
    String userIC;

    private String tripID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_info);

        userIC = getIntent().getStringExtra("ic");

        vehicleBrand = findViewById(R.id.vehicleBrand);
        vehicleReg = findViewById(R.id.vehicleReg);
        vehicleColor = findViewById(R.id.vehicleColor);
        noPass = findViewById(R.id.noPass);
        seniorCitizen = findViewById(R.id.seniorCitizen);
        adult = findViewById(R.id.adult);
        children = findViewById(R.id.children);
        infant = findViewById(R.id.infant);
        addDetail = findViewById(R.id.addDetail);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(vehicleBrand.getText()) || TextUtils.isEmpty(vehicleReg.getText())) {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
                return;
            }
            saveTripData();
        });
    }

    private void saveTripData() {
        AlertDialog progressDialog = new AlertDialog.Builder(this)
                .setView(R.layout.progress_layout)
                .setCancelable(false)
                .create();
        progressDialog.show();

        try {
            DataClass tripData = new DataClass(
                    vehicleBrand.getText().toString(),
                    vehicleReg.getText().toString(),
                    vehicleColor.getText().toString(),
                    Integer.parseInt(noPass.getText().toString()),
                    Integer.parseInt(seniorCitizen.getText().toString()),
                    Integer.parseInt(adult.getText().toString()),
                    Integer.parseInt(children.getText().toString()),
                    Integer.parseInt(infant.getText().toString()),
                    addDetail.getText().toString()
            );

            // Create readable tripID like TRIP_20250608_153045
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            tripID = "TRIP_" + timestamp;

            FirebaseDatabase.getInstance().getReference("TripInformation")
                    .child(userIC)
                    .child(tripID)
                    .setValue(tripData)
                    .addOnSuccessListener(unused -> {
                        progressDialog.dismiss();
                        showLocationConsentDialog();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } catch (NumberFormatException e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLocationConsentDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Location Access")
                .setMessage("Allow RoadGuard to track your location during the trip?")
                .setCancelable(false)
                .setPositiveButton("Allow", (dialog, which) -> {
                    Intent intent = new Intent(TripInfoActivity.this, MapsActivity.class);
                    intent.putExtra("userIC", userIC);
                    intent.putExtra("tripID", tripID);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Deny", (dialog, which) -> {
                    Toast.makeText(this, "Location access denied. Cannot proceed.", Toast.LENGTH_LONG).show();
                })
                .show();
    }
}
