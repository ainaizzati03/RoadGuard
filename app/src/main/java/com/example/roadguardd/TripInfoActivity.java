package com.example.roadguardd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

public class TripInfoActivity extends AppCompatActivity {

    EditText vehicleBrand, vehicleReg, vehicleColor, noPass,
            seniorCitizen, adult, children, infant, addDetail;
    Button saveButton;
    String userIC; // Will store the IC from intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_info);

        // Get the IC number from intent
        userIC = getIntent().getStringExtra("ic");

        // Initialize UI
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

        // Save button logic
        saveButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(vehicleBrand.getText()) || TextUtils.isEmpty(vehicleReg.getText())) {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            uploadData();
        });
    }

    private void uploadData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TripInfoActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

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

            // Generate a unique trip ID
            String tripID = FirebaseDatabase.getInstance()
                    .getReference("TripInformation")
                    .child(userIC)
                    .push()
                    .getKey();

            if (tripID == null) {
                dialog.dismiss();
                Toast.makeText(this, "Failed to create trip ID", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseDatabase.getInstance().getReference("TripInformation")
                    .child(userIC)
                    .child(tripID)
                    .setValue(tripData)
                    .addOnSuccessListener(unused -> {
                        dialog.dismiss();
                        Toast.makeText(TripInfoActivity.this, "Trip Info Saved", Toast.LENGTH_SHORT).show();

                        // Pass userIC and tripID to MapsActivity
                        Intent intent = new Intent(TripInfoActivity.this, MapsActivity.class);
                        intent.putExtra("userIC", userIC);
                        intent.putExtra("tripID", tripID);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        dialog.dismiss();
                        Toast.makeText(TripInfoActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } catch (NumberFormatException e) {
            dialog.dismiss();
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

}
