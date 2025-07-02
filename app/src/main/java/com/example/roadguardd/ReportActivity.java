package com.example.roadguardd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReportActivity extends FragmentActivity {

    Spinner levelInjury;
    EditText numVictim, numVehicle, otherDetail;
    Button cancelButton, sendButton;

    String userIC, tripID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);

        userIC = getIntent().getStringExtra("userIC");
        tripID = getIntent().getStringExtra("tripID");

        levelInjury = findViewById(R.id.levelInjury);
        numVictim = findViewById(R.id.numVictim);
        numVehicle = findViewById(R.id.numVehicle);
        otherDetail = findViewById(R.id.otherDetail);
        cancelButton = findViewById(R.id.cancelButton);
        sendButton = findViewById(R.id.sendButton);

        String[] injuryLevels = {"Select injury level", "Mild", "Severe", "Very Severe"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, injuryLevels);
        levelInjury.setAdapter(adapter);

        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReportActivity.this, MapsActivity.class);
            intent.putExtra("userIC", userIC);
            intent.putExtra("tripID", tripID);
            startActivity(intent);
            finish();
        });

        sendButton.setOnClickListener(v -> {
            String injuryLevel = levelInjury.getSelectedItem().toString();
            String victimCount = numVictim.getText().toString().trim();
            String vehicleCount = numVehicle.getText().toString().trim();
            String otherInfo = otherDetail.getText().toString().trim();

            if (injuryLevel.equals("Select injury level") || victimCount.isEmpty() || vehicleCount.isEmpty() || otherInfo.isEmpty()) {
                new AlertDialog.Builder(ReportActivity.this)
                        .setTitle("Incomplete Form")
                        .setMessage("Please fill in all fields before submitting.")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
                return;
            }

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGPSEnabled) {
                new AlertDialog.Builder(ReportActivity.this)
                        .setTitle("Location Required")
                        .setMessage("Please enable location services to send a report.")
                        .setCancelable(false)
                        .setPositiveButton("Go to Settings", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
                return;
            }

            double lat = 5.7636;
            double lon = 102.4113;
            String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());


            Map<String, Object> reportData = new HashMap<>();
            reportData.put("latitude", lat);
            reportData.put("longitude", lon);
            reportData.put("injuryLevel", injuryLevel);
            reportData.put("numVictim", victimCount);
            reportData.put("numVehicle", vehicleCount);
            reportData.put("otherDetail", otherInfo);
            reportData.put("timestamp", timestamp);

            FirebaseDatabase.getInstance().getReference("ReportAccident")
                    .child(userIC)
                    .child(tripID)
                    .setValue(reportData)
                    .addOnSuccessListener(unused -> {
                        new AlertDialog.Builder(ReportActivity.this)
                                .setTitle("Report Received")
                                .setMessage("Help is on the way.")
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialog, which) -> {
                                    Intent intent = new Intent(ReportActivity.this, MapsActivity.class);
                                    intent.putExtra("userIC", userIC);
                                    intent.putExtra("tripID", tripID);
                                    startActivity(intent);
                                    finish();
                                })
                                .show();
                    });
        });
    }
}
