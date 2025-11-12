package com.example.hprtransport;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ListView;
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

public class AdminHomeActivity extends AppCompatActivity {

    private ListView userListView;
    private EditText searchBar;
    private DatabaseReference mDatabase;
    private List<User> allUsersList;
    private List<User> filteredUsersList;
    private UserListAdapter adapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        userListView = findViewById(R.id.user_list_view);
        searchBar = findViewById(R.id.search_bar);
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        allUsersList = new ArrayList<>();
        filteredUsersList = new ArrayList<>();

        adapter = new UserListAdapter(this, filteredUsersList);
        userListView.setAdapter(adapter);

        loadUsersFromFirebase();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
                searchBar.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_search, 0, s.length() > 0 ? android.R.drawable.ic_menu_close_clear_cancel : 0, 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchBar.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (searchBar.getRight() - searchBar.getCompoundDrawables()[2].getBounds().width())) {
                    searchBar.setText("");
                    return true;
                }
            }
            return false;
        });

        userListView.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = filteredUsersList.get(position);
            Intent intent = new Intent(AdminHomeActivity.this, UserDetailsActivity.class);
            intent.putExtra("SELECTED_USER", selectedUser);
            startActivity(intent);
        });
    }

    private void loadUsersFromFirebase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allUsersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && "User".equals(user.flag)) {
                        allUsersList.add(user);
                    }
                }
                filterUsers(searchBar.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminHomeActivity.this, "Failed to load users: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filterUsers(String query) {
        filteredUsersList.clear();
        if (query.isEmpty()) {
            filteredUsersList.addAll(allUsersList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (User user : allUsersList) {
                if (user.name.toLowerCase().contains(lowerCaseQuery) ||
                    user.phoneNumber.contains(lowerCaseQuery) ||
                    user.vehicleNumber.toLowerCase().contains(lowerCaseQuery)) {
                    filteredUsersList.add(user);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
