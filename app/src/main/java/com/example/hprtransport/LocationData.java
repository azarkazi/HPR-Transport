package com.example.hprtransport;

import java.io.Serializable;

public class LocationData implements Serializable {
    public double latitude;
    public double longitude;
    public long updatedDateTime;

    public LocationData() {
        // Default constructor required for calls to DataSnapshot.getValue(LocationData.class)
    }

    public LocationData(double latitude, double longitude, long updatedDateTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.updatedDateTime = updatedDateTime;
    }
}
