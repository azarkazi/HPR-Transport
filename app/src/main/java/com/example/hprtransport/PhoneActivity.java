package com.example.hprtransport;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class PhoneActivity extends AppCompatActivity {

    private EditText phoneEditText;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        phoneEditText = findViewById(R.id.phone_edit_text);
        Button nextButton = findViewById(R.id.next_button_phone);

        name = getIntent().getStringExtra("name");

        nextButton.setOnClickListener(v -> validateAndProceed());

        findViewById(android.R.id.content).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (phoneEditText.isFocused()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(phoneEditText.getWindowToken(), 0);
                    phoneEditText.clearFocus();
                }
            }
            return true;
        });
    }

    private void validateAndProceed() {
        String phone = phoneEditText.getText().toString().trim();

        if (phone.isEmpty()) {
            phoneEditText.setError("Phone number cannot be empty.");
            phoneEditText.requestFocus();
            return;
        }

        if (!phone.matches("\\d{10}")) { // Must be exactly 10 digits
            phoneEditText.setError("Please enter a valid 10-digit phone number.");
            phoneEditText.requestFocus();
            return;
        }

        Intent intent = new Intent(PhoneActivity.this, VehicleActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("phone", phone);
        startActivity(intent);
    }
}
