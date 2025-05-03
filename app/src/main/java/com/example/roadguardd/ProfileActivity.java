package com.example.roadguardd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    TextView profileIC, profileName, profilePhone, profilePassword;
    Button updateProfile;
    String currentUserIC; // to store IC for reloading later

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileIC = findViewById(R.id.profileIC);
        profileName = findViewById(R.id.profileName);
        profilePhone = findViewById(R.id.profilePhone);
        profilePassword = findViewById(R.id.profilePassword);
        updateProfile = findViewById(R.id.updateButton);

        showUserData(); // shows initial data passed via Intent

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passUserData(); // go to update screen
            }
        });
    }

    public void showUserData() {
        Intent intent = getIntent();

        currentUserIC = intent.getStringExtra("ic"); // store for later use
        String nameUser = intent.getStringExtra("name");
        String phoneUser = intent.getStringExtra("phone");
        String passwordUser = intent.getStringExtra("password");

        profileIC.setText(currentUserIC);
        profileName.setText(nameUser);
        profilePhone.setText(phoneUser);
        profilePassword.setText(passwordUser);
    }

    public void passUserData() {
        String userIC = profileIC.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("ic").equalTo(userIC);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String icFromDB = userSnapshot.child("ic").getValue(String.class);
                        String nameFromDB = userSnapshot.child("name").getValue(String.class);
                        String phoneFromDB = userSnapshot.child("phone").getValue(String.class);
                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);

                        Intent intent = new Intent(ProfileActivity.this, UpdateProfileActivity.class);
                        intent.putExtra("ic", icFromDB);
                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("phone", phoneFromDB);
                        intent.putExtra("password", passwordFromDB);

                        startActivity(intent);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Optional: handle error
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUserData(); // reload fresh data after returning
    }

    private void refreshUserData() {
        if (currentUserIC == null) return;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("ic").equalTo(currentUserIC);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String name = userSnapshot.child("name").getValue(String.class);
                        String phone = userSnapshot.child("phone").getValue(String.class);
                        String password = userSnapshot.child("password").getValue(String.class);

                        profileName.setText(name);
                        profilePhone.setText(phone);
                        profilePassword.setText(password);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Optional: handle error
            }
        });
    }
}
