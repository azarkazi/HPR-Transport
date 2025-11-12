package com.example.hprtransport;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PersistentService extends Service {

    public static final String CHANNEL_ID = "PersistentServiceChannel";
    private static final String TAG = "PersistentService";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseReference userLocationRef;

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            if (location != null) {
                updateLocationInFirebase(location, "Live");
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service onStartCommand");

        SharedPreferences sharedPreferences = getSharedPreferences("HPRTransportPrefs", MODE_PRIVATE);
        String userPhoneNumber = sharedPreferences.getString("userPhoneNumber", null);

        if (userPhoneNumber == null) {
            Log.e(TAG, "Could not get user phone number. Stopping service.");
            stopSelf();
            return START_NOT_STICKY; // Do not restart if there's no user
        }

        userLocationRef = FirebaseDatabase.getInstance().getReference("users").child(userPhoneNumber).child("location");

        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("HPR Transport")
                .setContentText("Location tracking is active")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        startForeground(1, notification);
        startLocationUpdates();

        return START_STICKY;
    }

    private void startLocationUpdates() {
        if (userLocationRef == null) {
            Log.e(TAG, "Firebase reference is null. Cannot update location.");
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Fine location permission not granted. Cannot start location updates.");
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                Log.d(TAG, "Successfully got last known location.");
                updateLocationInFirebase(location, "LastKnown");
            }
        });

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(15000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        Log.d(TAG, "Requested live location updates.");
    }

    private void updateLocationInFirebase(Location location, String type) {
        long currentTime = System.currentTimeMillis();
        LocationData locationData = new LocationData(location.getLatitude(), location.getLongitude(), currentTime);
        userLocationRef.setValue(locationData).addOnSuccessListener(aVoid -> {
            Log.d(TAG, type + " location updated in Firebase: " + location.getLatitude() + "," + location.getLongitude() + " at " + currentTime);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to write " + type + " location to Firebase", e);
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Persistent Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            getSystemService(NotificationManager.class).createNotificationChannel(serviceChannel);
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
        Log.d(TAG, "Service onDestroy");
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }
}
