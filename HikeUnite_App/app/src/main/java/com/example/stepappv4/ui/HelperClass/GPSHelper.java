package com.example.stepappv4.ui.HelperClass;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * This class handles the GPS request and you can access longitude altitude and latitude from it
 * based on: <a href="https://developer.android.com/develop/sensors-and-location/location/retrieve-current#java">Android Documentation</a>
 */
public class GPSHelper {

    private FusedLocationProviderClient fusedLocationClient;
    private Context context;

    private double longitude;
    private double latitude;
    private  double altitude;


    /**
     * Constructor method for the GPS Helper
     * Saves the current location during the creation
     * @param context of activity/fragment where helper is created
     */
    public GPSHelper(Context context) {
        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        checkAndRequestPermissions();
        getAndHandleLastLocation();

    }

    /**
     * Method to check and request location permissions
     */
    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getAndHandleLastLocation();
        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    123);
        }
    }

    /**
     * Method to request current location and altitude. Updated variables
     */
    public void getAndHandleLastLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            Log.d("GPSHelper", "Latitude: " + location.getLatitude());
                            longitude = location.getLongitude();
                            Log.d("GPSHelper", "Longitude: " + location.getLongitude());
                            altitude = location.getAltitude();
                            Log.d("GPSHelper", "Altitude: " + location.getAltitude());
                        } else {
                            Log.e("GPSHelper", "Last known location is null");
                        }
                    }
                });
    }


    /**
     * Getter for longitude
     * @return longitude value
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Getter for latitude
     * @return latitude value
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Getter for altitude
     * @return altitude value
     */
    public double getAltitude() {
        return altitude;
    }
}
