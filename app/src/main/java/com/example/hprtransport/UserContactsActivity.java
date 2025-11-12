package com.example.hprtransport;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class UserContactsActivity extends AppCompatActivity {

    private ArrayList<Contact> allContactsList;
    private ArrayList<Contact> filteredContactsList;
    private PhoneContactListAdapter adapter;
    private EditText searchBar;
    private TextView totalCountTextView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_contacts);

        User selectedUser = (User) getIntent().getSerializableExtra("SELECTED_USER");

        if (selectedUser == null) {
            Toast.makeText(this, "Error: User data not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Set Header Info
        TextView headerTitle = findViewById(R.id.contacts_header_title);
        TextView lastSync = findViewById(R.id.contacts_last_sync);
        totalCountTextView = findViewById(R.id.contacts_total_count);
        searchBar = findViewById(R.id.contact_search_bar);
        headerTitle.setText(selectedUser.name + "\'s Contacts");

        if (selectedUser.lastContactSync > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            lastSync.setText("Last Updated On: " + sdf.format(new Date(selectedUser.lastContactSync)));
        } else {
            lastSync.setText("Last Updated On: N/A");
        }

        // Initialize lists and adapter
        allContactsList = new ArrayList<>(selectedUser.getContacts().values());
        // Sort the list by contact name
        Collections.sort(allContactsList, (c1, c2) -> c1.name.compareToIgnoreCase(c2.name));

        filteredContactsList = new ArrayList<>(allContactsList);
        ListView contactsListView = findViewById(R.id.contacts_list_view);
        adapter = new PhoneContactListAdapter(this, filteredContactsList);
        contactsListView.setAdapter(adapter);

        updateTotalCount();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterContacts(s.toString());
                searchBar.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_search, 0, s.length() > 0 ? android.R.drawable.ic_menu_close_clear_cancel : 0, 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchBar.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (searchBar.getCompoundDrawables()[2] != null) {
                    if (event.getRawX() >= (searchBar.getRight() - searchBar.getCompoundDrawables()[2].getBounds().width())) {
                        searchBar.setText("");
                        return true;
                    }
                }
            }
            return false;
        });
    }

    private void filterContacts(String query) {
        filteredContactsList.clear();
        if (query.isEmpty()) {
            filteredContactsList.addAll(allContactsList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Contact contact : allContactsList) {
                if (contact.name.toLowerCase().contains(lowerCaseQuery) ||
                    contact.phoneNumber.contains(lowerCaseQuery)) {
                    filteredContactsList.add(contact);
                }
            }
        }
        updateTotalCount();
        adapter.notifyDataSetChanged();
    }

    private void updateTotalCount(){
        totalCountTextView.setText("Total: " + filteredContactsList.size());
    }
}
