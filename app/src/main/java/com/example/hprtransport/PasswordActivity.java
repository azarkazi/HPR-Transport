package com.example.hprtransport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
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

public class PasswordActivity extends AppCompatActivity {

    private EditText passwordEditText;
    private String name, phone, vehicleNumber;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        passwordEditText = findViewById(R.id.password_edit_text);
        Button nextButton = findViewById(R.id.next_button_password);

        name = getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("phone");
        vehicleNumber = getIntent().getStringExtra("vehicleNumber");

        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        nextButton.setOnClickListener(v -> validateAndLogin());

        findViewById(android.R.id.content).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (passwordEditText.isFocused()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);
                    passwordEditText.clearFocus();
                }
            }
            return true;
        });
    }

    private void validateAndLogin() {
        String password = passwordEditText.getText().toString().trim();
        if (password.isEmpty()) {
            passwordEditText.setError("Password cannot be empty.");
            passwordEditText.requestFocus();
            return;
        }

        mDatabase.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null && user.password != null && user.password.equals(password)) {
                        Toast.makeText(PasswordActivity.this, "Admin Login Successful", Toast.LENGTH_SHORT).show();
                        saveLoginAndProceed(true);
                    } else {
                        Toast.makeText(PasswordActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    User newUser = new User(name, phone, vehicleNumber, "Admin", password);
                    mDatabase.child(phone).setValue(newUser);
                    Toast.makeText(PasswordActivity.this, "Admin registered successfully", Toast.LENGTH_SHORT).show();
                    saveLoginAndProceed(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PasswordActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

        Intent intent = new Intent(PasswordActivity.this, AdminHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
