package com.example.roadguardd;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Marker userMarker;
    public static final int LOCATION_REQUEST_CODE = 100;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    Button sosButton, reportAccidentButton, endDriveButton;
    String userIC, tripID;

    private boolean hasShownReportResponse = false;
    private boolean hasShownSOSResponse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        userIC = getIntent().getStringExtra("userIC");
        tripID = getIntent().getStringExtra("tripID");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
            new AlertDialog.Builder(this)
                    .setTitle("End Drive?")
                    .setMessage("Stop tracking GPS?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        stopLocationUpdates();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        listenForSOSRespond();
        listenForReportRespond();

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null || map == null) return;

                Location location = locationResult.getLastLocation();
                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                if (userMarker == null) {
                    userMarker = map.addMarker(new MarkerOptions()
                            .position(userLatLng)
                            .title("You are here")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 17));
                } else {
                    userMarker.setPosition(userLatLng);
                    map.animateCamera(CameraUpdateFactory.newLatLng(userLatLng));
                }
            }
        };
    }

    private void sendSOS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TripInformation")
                        .child(userIC)
                        .child(tripID)
                        .child("SOS");

                ref.child("latitude").setValue(lat);
                ref.child("longitude").setValue(lon);
                ref.child("timestamp").setValue(timestamp)
                        .addOnSuccessListener(unused -> {
                            new AlertDialog.Builder(this)
                                    .setTitle("SOS Sent!")
                                    .setMessage("Help is on the way.")
                                    .setPositiveButton("OK", null)
                                    .show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Could not get location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listenForSOSRespond() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TripInformation")
                .child(userIC).child(tripID).child("SOS").child("respond");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && !hasShownSOSResponse) {
                    hasShownSOSResponse = true;
                    String msg = snapshot.getValue(String.class);
                    new AlertDialog.Builder(MapsActivity.this)
                            .setTitle("SOS Response")
                            .setMessage(msg)
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MapsActivity.this, "Error loading SOS response", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listenForReportRespond() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ReportAccident")
                .child(userIC).child(tripID).child("response");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && !hasShownReportResponse) {
                    hasShownReportResponse = true;
                    String msg = snapshot.getValue(String.class);
                    new AlertDialog.Builder(MapsActivity.this)
                            .setTitle("Report Response")
                            .setMessage(msg)
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MapsActivity.this, "Error loading report response", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            startLocationUpdates();
        } else {
            checkLocationPermission();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onMapReady(map);
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
}
