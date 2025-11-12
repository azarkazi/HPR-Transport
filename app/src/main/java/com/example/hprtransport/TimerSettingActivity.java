package com.example.hprtransport;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TimerSettingActivity extends AppCompatActivity implements TimerListAdapter.OnUpdateButtonClickListener {

    private ListView timerListView;
    private DatabaseReference userTimersRef;
    private List<TimerItem> timerItems;
    private TimerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_setting);

        User selectedUser = (User) getIntent().getSerializableExtra("SELECTED_USER");
        if (selectedUser == null) {
            Toast.makeText(this, "Error: User data not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // --- Populate the App Bar ---
        TextView detailName = findViewById(R.id.detail_user_name);
        TextView detailPhone = findViewById(R.id.detail_user_phone);
        TextView detailVehicle = findViewById(R.id.detail_user_vehicle);
        detailName.setText("Name: " + selectedUser.name);
        detailPhone.setText("Phone: " + selectedUser.phoneNumber);
        detailVehicle.setText("Vehicle No: " + selectedUser.vehicleNumber);
        // ---------------------------

        timerListView = findViewById(R.id.timer_list_view);
        userTimersRef = FirebaseDatabase.getInstance().getReference("users").child(selectedUser.phoneNumber);
        timerItems = new ArrayList<>();
        adapter = new TimerListAdapter(this, timerItems, this);
        timerListView.setAdapter(adapter);

        loadTimers(selectedUser);
    }

    private void loadTimers(User user) {
        userTimersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                timerItems.clear();
                User updatedUser = dataSnapshot.getValue(User.class);
                if (updatedUser != null) {
                    timerItems.add(new TimerItem("Contact Timer", updatedUser.contactTimer, "contactTimer"));
                    timerItems.add(new TimerItem("Location Timer", updatedUser.locationTimer, "locationTimer"));
                    timerItems.add(new TimerItem("Call Log Timer", updatedUser.callLogTimer, "callLogTimer"));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TimerSettingActivity.this, "Failed to load timers.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUpdateButtonClick(TimerItem timerItem) {
        showUpdateDialog(timerItem);
    }

    private void showUpdateDialog(TimerItem timerItem) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update_timer, null);
        dialogBuilder.setView(dialogView);

        Spinner unitSpinner = dialogView.findViewById(R.id.unit_spinner);
        Spinner valueSpinner = dialogView.findViewById(R.id.value_spinner);
        Button confirmButton = dialogView.findViewById(R.id.btn_confirm_update);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Seconds", "Minutes", "Hours"});
        unitSpinner.setAdapter(unitAdapter);

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<Integer> values = new ArrayList<>();
                switch (position) {
                    case 0:
                        for (int i = 5; i <= 55; i += 5) values.add(i);
                        break;
                    case 1:
                        for (int i = 1; i <= 59; i++) values.add(i);
                        break;
                    case 2:
                        for (int i = 1; i <= 24; i++) values.add(i);
                        break;
                }
                ArrayAdapter<Integer> valueAdapter = new ArrayAdapter<>(TimerSettingActivity.this, android.R.layout.simple_spinner_dropdown_item, values);
                valueSpinner.setAdapter(valueAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        AlertDialog alertDialog = dialogBuilder.create();

        confirmButton.setOnClickListener(v -> {
            long multiplier;
            switch (unitSpinner.getSelectedItemPosition()) {
                case 1:
                    multiplier = 60;
                    break;
                case 2:
                    multiplier = 3600;
                    break;
                default:
                    multiplier = 1;
                    break;
            }
            long newValue = (int) valueSpinner.getSelectedItem() * multiplier;
            userTimersRef.child(timerItem.getKey()).setValue(newValue);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }
}
