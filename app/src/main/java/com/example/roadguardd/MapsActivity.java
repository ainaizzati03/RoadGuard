package com.example.roadguardd;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;
    public static final int LOCATION_REQUEST_CODE = 100;
    FusedLocationProviderClient fusedLocationProviderClient;

    Button sosButton, reportAccidentButton, endDriveButton;

    String userIC, tripID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Get data from intent
        userIC = getIntent().getStringExtra("userIC");
        tripID = getIntent().getStringExtra("tripID");

        // Map setup
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(MapsActivity.this);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Buttons
        sosButton = findViewById(R.id.sosButton);
        reportAccidentButton = findViewById(R.id.reportAccidentButton);
        endDriveButton = findViewById(R.id.endDriveButton);

        checkLocationPermission();

        sosButton.setOnClickListener(v -> sendSOS());

        reportAccidentButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapsActivity.this, ReportActivity.class);
            intent.putExtra("userIC", userIC);
            intent.putExtra("tripID", tripID);
            startActivity(intent);
        });

        endDriveButton.setOnClickListener(v -> {
            new AlertDialog.Builder(MapsActivity.this)
                    .setTitle("Are you sure to End Drive?")
                    .setMessage("RoadGuard will stop tracking your GPS location.")
                    .setCancelable(false)
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .show();
        });

    }

    private void sendSOS() {
        new AlertDialog.Builder(MapsActivity.this)
                .setTitle("Are you sure to send SOS?")
                .setMessage("Your GPS location and Trip Information will be sent to MERS999.")
                .setCancelable(false)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Send", (dialog, which) -> {
                    double klccLat = 3.1579;
                    double klccLon = 101.7123;

                    FirebaseDatabase.getInstance().getReference("TripInformation")
                            .child(userIC)
                            .child(tripID)
                            .child("SOS")
                            .setValue(new LatLng(klccLat, klccLon))
                            .addOnSuccessListener(unused -> {
                                new AlertDialog.Builder(MapsActivity.this)
                                        .setTitle("SOS Received!")
                                        .setMessage("Help is on the way.")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", (dialog2, which2) -> dialog2.dismiss())
                                        .show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(MapsActivity.this, "Failed to send SOS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .show();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
    }

    private void showKLCC() {
        double lat = 3.1579;
        double lon = 101.7123;
        LatLng klcc = new LatLng(lat, lon);

        if (map != null) {
            map.clear();
            map.addMarker(new MarkerOptions().position(klcc).title("Petronas Twin Towers"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(klcc, 17));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
                showKLCC();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            showKLCC();
        } else {
            checkLocationPermission();
        }

        map.getUiSettings().setMyLocationButtonEnabled(true);
    }
}