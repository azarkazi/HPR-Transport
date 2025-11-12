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

import androidx.appcompat.app.AppCompatActivity;

public class NameActivity extends AppCompatActivity {

    private EditText nameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("HPRTransportPrefs", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            String userRole = sharedPreferences.getString("userRole", "User");
            if (userRole.equals("Admin")) {
                startActivity(new Intent(this, AdminHomeActivity.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
            finish();
            return;
        }

        setContentView(R.layout.activity_name);

        nameEditText = findViewById(R.id.name_edit_text);
        Button nextButton = findViewById(R.id.next_button_name);

        nextButton.setOnClickListener(v -> validateAndProceed());

        findViewById(android.R.id.content).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (nameEditText.isFocused()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(nameEditText.getWindowToken(), 0);
                    nameEditText.clearFocus();
                }
            }
            return true;
        });
    }

    private void validateAndProceed() {
        String name = nameEditText.getText().toString().trim();

        if (name.isEmpty()) {
            nameEditText.setError("Name cannot be empty.");
            nameEditText.requestFocus();
            return;
        }

        if (!name.matches("[a-zA-Z ]+")) { // Allow letters and spaces
            nameEditText.setError("Please enter only alphabetic characters.");
            nameEditText.requestFocus();
            return;
        }

        Intent intent = new Intent(NameActivity.this, PhoneActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }
}
