package com.example.stepappv4.ui.HelperClass;


import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.stepappv4.R;
import com.example.stepappv4.StepAppOpenHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class HikeService extends Service {

    private PowerManager.WakeLock wakeLock;


    private final Handler handler = new Handler();

    private FusedLocationProviderClient fusedLocationClient;
    private Context context;

    private double longitude;
    private double latitude;
    private  double altitude;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    String.valueOf(1),
                    "HikeService Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TEST", "started intent");
        startForeground(NOTIFICATION_ID, buildNotification());
        int id = intent.getIntExtra("id", 0);
        int seconds = intent.getIntExtra("seconds", 5);
        boolean started = intent.getBooleanExtra("started", false);
        boolean haveBreak = intent.getBooleanExtra("haveBreak", false);
        boolean stopService = intent.getBooleanExtra("stopService", false);
        if (stopService) {
            stopSelf();
            return START_NOT_STICKY;
        } else {
            context = getApplicationContext();
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            StepAppOpenHelper myDatabaseHelper = new StepAppOpenHelper(context);

            // Start the service in the foreground

            acquireWakeLock();

            // Call the sendToDatabase method
            if (started) {
                sendToDatabase(myDatabaseHelper, id, haveBreak, started, seconds);
            }

            return START_STICKY;
        }
    }
    private  void sendToDatabase(StepAppOpenHelper myDatabaseHelper, int id, boolean haveBreak, boolean started, int seconds){
            handler.postDelayed(() -> {
                if (!haveBreak) {
                    getAndHandleLastLocation();
                    Log.d("FunctionLog", "Updated Location");
                    myDatabaseHelper.insertGPSData(longitude, latitude, altitude, id);

                    sendToDatabase(myDatabaseHelper, id, haveBreak, started, seconds);
                }
            }, seconds * 1000L);
        }


    private static final int NOTIFICATION_ID = 1;

    // Build a notification for the foreground service
    private Notification buildNotification() {
        // Implement your notification here
        // ...

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, String.valueOf(1))
                .setContentTitle("HikeService")
                .setContentText("Service is running")
                .setSmallIcon(R.drawable.hikon_background);

        return builder.build();
    }
    // Acquire a WakeLock to keep the device awake
    private void acquireWakeLock() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "HikeService::WakeLock"
            );
            wakeLock.acquire();
        }
    }

    // Release the WakeLock when the service is stopped
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

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
    private void getAndHandleLastLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            Log.d("ServiceGPS", "Latitude: " + location.getLatitude());
                            longitude = location.getLongitude();
                            Log.d("ServiceGPS", "Longitude: " + location.getLongitude());
                            altitude = location.getAltitude();
                            Log.d("ServiceGPS", "Altitude: " + location.getAltitude());
                        } else {
                            Log.e("ServiceGPS", "Last known location is null");
                        }
                    }
                });
    }




}
