package com.example.hprtransport;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserDetailsActivity extends AppCompatActivity {

    private User selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        selectedUser = (User) getIntent().getSerializableExtra("SELECTED_USER");

        if (selectedUser == null) {
            Toast.makeText(this, "Error: User data not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        TextView detailName = findViewById(R.id.detail_user_name);
        TextView detailPhone = findViewById(R.id.detail_user_phone);
        TextView detailVehicle = findViewById(R.id.detail_user_vehicle);

        detailName.setText("Name: " + selectedUser.name);
        detailPhone.setText("Phone: " + selectedUser.phoneNumber);
        detailVehicle.setText("Vehicle No: " + selectedUser.vehicleNumber);

        Button btnTimerSetting = findViewById(R.id.btn_timer_setting);
        Button btnShowContacts = findViewById(R.id.btn_show_contacts);
        Button btnShowLocation = findViewById(R.id.btn_show_location);

        btnTimerSetting.setOnClickListener(v -> {
            Intent intent = new Intent(UserDetailsActivity.this, TimerSettingActivity.class);
            intent.putExtra("SELECTED_USER", selectedUser);
            startActivity(intent);
        });

        btnShowContacts.setOnClickListener(v -> {
            if (selectedUser.getContacts() == null || selectedUser.getContacts().isEmpty()) {
                Toast.makeText(this, "This user has no synced contacts.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(UserDetailsActivity.this, UserContactsActivity.class);
            intent.putExtra("SELECTED_USER", selectedUser);
            startActivity(intent);
        });

        btnShowLocation.setOnClickListener(v -> {
            if (selectedUser.location == null) {
                Toast.makeText(this, "Location data is not available for this user.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(UserDetailsActivity.this, UserLocationActivity.class);
            intent.putExtra("SELECTED_USER", selectedUser);
            startActivity(intent);
        });
    }
}
