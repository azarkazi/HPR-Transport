package com.example.hprtransport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * @deprecated This activity is no longer in use. It has been replaced by the multi-step
 * registration flow (NameActivity, PhoneActivity, VehicleActivity).
 */
@Deprecated
public class LoginActivity extends AppCompatActivity {

    private EditText name, phoneNumber, vehicleNumber;
    private Button loginButton;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        name = findViewById(R.id.name);
        phoneNumber = findViewById(R.id.phone_number);
        vehicleNumber = findViewById(R.id.vehicle_number);
        loginButton = findViewById(R.id.login_button);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = name.getText().toString().trim();
                String phoneStr = phoneNumber.getText().toString().trim();
                String vehicleStr = vehicleNumber.getText().toString().trim();

                if (nameStr.isEmpty() || phoneStr.isEmpty() || vehicleStr.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new user object with the default flag
                User user = new User(nameStr, phoneStr, vehicleStr, "user");

                // Save the user to the database
                mDatabase.child("users").child(phoneStr).setValue(user);

                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                // Start MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
