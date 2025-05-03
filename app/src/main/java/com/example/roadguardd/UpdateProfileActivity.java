package com.example.roadguardd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateProfileActivity extends AppCompatActivity {

    EditText updateIC, updateName, updatePhone, updatePassword;
    Button saveButton;
    String ICUser, nameUser, phoneUser, passwordUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        reference = FirebaseDatabase.getInstance().getReference("users");

        updateIC = findViewById(R.id.updateIC);
        updateName = findViewById(R.id.updateName);
        updatePhone = findViewById(R.id.updatePhone);
        updatePassword = findViewById(R.id.updatePassword);
        saveButton = findViewById(R.id.saveButton);

        showData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNameChanged() || isPhoneChanged() || isPasswordChanged()) {
                    Toast.makeText(UpdateProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "No Changes Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isNameChanged(){
        if (!nameUser.equals(updateName.getText().toString())){
            reference.child(ICUser).child("name").setValue(updateName.getText().toString());
            nameUser = updateName.getText().toString();
            return true;
        } else{
            return false;
        }
    }

    public boolean isPhoneChanged(){
        if (!phoneUser.equals(updatePhone.getText().toString())){
            reference.child(ICUser).child("name").setValue(updatePhone.getText().toString());
            phoneUser = updatePhone.getText().toString();
            return true;
        } else{
            return false;
        }
    }

    public boolean isPasswordChanged(){
        if (!passwordUser.equals(updatePassword.getText().toString())){
            reference.child(ICUser).child("password").setValue(updatePassword.getText().toString());
            passwordUser = updatePassword.getText().toString();
            return true;
        } else{
            return false;
        }
    }

    public void showData(){
        Intent intent = getIntent();

        ICUser = intent.getStringExtra("ic");
        nameUser = intent.getStringExtra("name");
        phoneUser = intent.getStringExtra("phone");
        passwordUser = intent.getStringExtra("password");

        updateIC.setText(ICUser);
        updateName.setText(nameUser);
        updatePhone.setText(phoneUser);
        updatePassword.setText(passwordUser);
    }
}