package com.example.hprtransport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VehicleActivity extends AppCompatActivity {

    private EditText vehicleEditText;
    private String name, phone;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        vehicleEditText = findViewById(R.id.vehicle_edit_text);
        Button loginButton = findViewById(R.id.login_button_vehicle);

        name = getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("phone");

        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        loginButton.setOnClickListener(v -> validateAndLogin());

        findViewById(android.R.id.content).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (vehicleEditText.isFocused()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(vehicleEditText.getWindowToken(), 0);
                    vehicleEditText.clearFocus();
                }
            }
            return true;
        });
    }

    private void validateAndLogin() {
        final String vehicleNumber = vehicleEditText.getText().toString().trim();

        if (vehicleNumber.isEmpty()) {
            vehicleEditText.setError("Vehicle number cannot be empty.");
            vehicleEditText.requestFocus();
            return;
        }

        if (!vehicleNumber.matches("[a-zA-Z0-9]+")) { // Alphanumeric only
            vehicleEditText.setError("Please enter only letters and numbers.");
            vehicleEditText.requestFocus();
            return;
        }

        if (phone.equals("9876543210") && vehicleNumber.equals("AA12AA1212")) {
            Intent intent = new Intent(VehicleActivity.this, PasswordActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("phone", phone);
            intent.putExtra("vehicleNumber", vehicleNumber);
            startActivity(intent);
            return;
        }

        mDatabase.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mDatabase.child(phone).child("createdOrUpdated").setValue(System.currentTimeMillis()); // Update timestamp
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null && user.vehicleNumber.equals(vehicleNumber)) {
                        if (!user.name.equals(name)) {
                            mDatabase.child(phone).child("name").setValue(name);
                            Toast.makeText(VehicleActivity.this, "Name updated.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VehicleActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(VehicleActivity.this, "This phone number is already registered with a different vehicle.", Toast.LENGTH_LONG).show();
                        return;
                    }
                } else {
                    User newUser = new User(name, phone, vehicleNumber, "User");
                    mDatabase.child(phone).setValue(newUser);
                    Toast.makeText(VehicleActivity.this, "New user registered.", Toast.LENGTH_SHORT).show();
                }

                saveLoginAndProceed(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(VehicleActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLoginAndProceed(boolean isAdmin) {
        SharedPreferences sharedPreferences = getSharedPreferences("HPRTransportPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("userPhoneNumber", phone);
        editor.putString("userRole", isAdmin ? "Admin" : "User");
        editor.apply();

        Intent intent = new Intent(VehicleActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
