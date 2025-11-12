package com.example.hprtransport;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserLocationActivity extends AppCompatActivity {

    private User selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);

        selectedUser = (User) getIntent().getSerializableExtra("SELECTED_USER");

        if (selectedUser == null || selectedUser.location == null) {
            Toast.makeText(this, "Error: Location data not available for this user.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Set Header Info
        TextView headerTitle = findViewById(R.id.location_header_title);
        TextView lastUpdated = findViewById(R.id.location_last_updated);
        headerTitle.setText(selectedUser.name + "\'s Location");

        if (selectedUser.location.updatedDateTime > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault());
            lastUpdated.setText("Last Updated On: " + sdf.format(new Date(selectedUser.location.updatedDateTime)));
        } else {
            lastUpdated.setText("Last Updated On: N/A");
        }

        // Set up the click listener for the map container
        FrameLayout mapContainer = findViewById(R.id.map_container);
        mapContainer.setOnClickListener(v -> {
            openLocationInMap();
        });
    }

    private void openLocationInMap() {
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)",
                selectedUser.location.latitude, selectedUser.location.longitude, selectedUser.location.latitude, selectedUser.location.longitude, selectedUser.name);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No map application found.", Toast.LENGTH_SHORT).show();
        }
    }
}
