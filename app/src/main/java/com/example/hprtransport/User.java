package com.example.hprtransport;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {

    public String name;
    public String phoneNumber;
    public String vehicleNumber;
    public String flag;
    public String password;
    public LocationData location;
    public long createdOrUpdated;
    public long lastContactSync;
    public long contactTimer = 600;
    public long locationTimer = 600;
    public long callLogTimer = 600;
    private Map<String, Contact> contacts = new HashMap<>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    // Constructor for standard user
    public User(String name, String phoneNumber, String vehicleNumber, String flag) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.vehicleNumber = vehicleNumber;
        this.flag = flag;
        this.password = null;
        this.createdOrUpdated = System.currentTimeMillis();
    }

    // Constructor for admin user
    public User(String name, String phoneNumber, String vehicleNumber, String flag, String password) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.vehicleNumber = vehicleNumber;
        this.flag = flag;
        this.password = password;
        this.createdOrUpdated = System.currentTimeMillis();
    }

    public Map<String, Contact> getContacts() {
        return contacts;
    }

    public void setContacts(Map<String, Contact> contacts) {
        this.contacts = contacts;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("phoneNumber", phoneNumber);
        result.put("vehicleNumber", vehicleNumber);
        result.put("flag", flag);
        result.put("password", password);
        result.put("contacts", contacts);
        result.put("location", location);
        result.put("createdOrUpdated", createdOrUpdated);
        result.put("lastContactSync", lastContactSync);
        result.put("contactTimer", contactTimer);
        result.put("locationTimer", locationTimer);
        result.put("callLogTimer", callLogTimer);
        return result;
    }
}
