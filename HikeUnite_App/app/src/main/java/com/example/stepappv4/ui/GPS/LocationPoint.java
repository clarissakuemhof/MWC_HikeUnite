package com.example.stepappv4.ui.GPS;

public class LocationPoint {

    private double longitude;
    private double altitude;

    public LocationPoint(double longitude, double altitude) {
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }
}
