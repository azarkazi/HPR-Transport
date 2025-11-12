package com.example.hprtransport;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class ContactSyncService extends Service {

    private Timer timer;
    private DatabaseReference mDatabase;
    private static final String TAG = "ContactSyncService";

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        timer = new Timer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                syncContacts();
            }
        }, 0, 6 * 60 * 60 * 1000);

        return START_STICKY;
    }

    @SuppressLint("Range")
    private void syncContacts() {
        SharedPreferences sharedPreferences = getSharedPreferences("HPRTransportPrefs", MODE_PRIVATE);
        String userPhoneNumber = sharedPreferences.getString("userPhoneNumber", null);

        if (userPhoneNumber == null) {
            Log.e(TAG, "User phone number not found. Cannot sync contacts.");
            return;
        }

        DatabaseReference userRef = mDatabase.child("users").child(userPhoneNumber);
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
            Log.d(TAG, "Contact sync completed for user: " + userPhoneNumber);

            // After sync is complete, update the timestamp
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
