package com.example.roadguardd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    EditText loginIC, loginPassword;
    ImageView loginTogglePassword;
    Button loginButton;
    TextView registerRedirectText;

    boolean isPasswordVisible = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginIC = findViewById(R.id.login_IC);
        loginPassword = findViewById(R.id.login_password);
        loginTogglePassword = findViewById(R.id.login_toggle_password);
        registerRedirectText = findViewById(R.id.registerRedirectText);
        loginButton = findViewById(R.id.login_button);

        loginTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                loginTogglePassword.setImageResource(R.drawable.ic_eye_open);
            } else {
                loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                loginTogglePassword.setImageResource(R.drawable.ic_eye_closed);
            }
            loginPassword.setSelection(loginPassword.length());
        });

        loginButton.setOnClickListener(view -> {
            if (!validateIC() | !validatePassword()) {
                // Input validation failed
            } else {
                checkUser();
            }
        });

        registerRedirectText.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    public Boolean validateIC() {
        String val = loginIC.getText().toString();
        if (val.isEmpty()) {
            loginIC.setError("IC number cannot be empty");
            return false;
        } else {
            loginIC.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("Password cannot be empty");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String userIC = loginIC.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        reference.child(userIC).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String passwordFromDB = snapshot.child("password").getValue(String.class);

                    if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {
                        String icFromDB = snapshot.child("ic").getValue(String.class);
                        String nameFromDB = snapshot.child("name").getValue(String.class);
                        String phoneFromDB = snapshot.child("phone").getValue(String.class);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                        intent.putExtra("ic", icFromDB);
                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("phone", phoneFromDB);
                        intent.putExtra("password", passwordFromDB);

                        startActivity(intent);
                    } else {
                        loginPassword.setError("Invalid Credentials");
                        loginPassword.requestFocus();
                    }
                } else {
                    loginIC.setError("User does not exist");
                    loginIC.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }
}
