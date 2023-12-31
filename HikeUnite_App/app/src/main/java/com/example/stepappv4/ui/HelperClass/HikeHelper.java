package com.example.stepappv4.ui.HelperClass;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.stepappv4.MainActivity;
import com.example.stepappv4.R;
import com.example.stepappv4.StepAppOpenHelper;
import com.example.stepappv4.ui.Home.HomeFragment;
import com.example.stepappv4.ui.Home.StepCounterListener;

import java.util.Random;

public class HikeHelper {


    private StepAppOpenHelper myDatabaseHelper;
    private GPSHelper myGPSHelper;
    private int id, buttonColor1, buttonColor2;
    private boolean started, haveBreak;
    private OpenStreetMapsHelper mapsHelper;
    private Context context;
    private final Handler handler = new Handler();
    private Button startButton, stopButton;
    private String[] inspirationalQuotes;

    /**
     * Constructor class for HikeHelper
     * Initiates all needed variables and requests permission for gps service
     *
     * @param activeContext context of activity where helper is constructed
     * @param startButton start button in home fragment
     * @param stopButton stop button in the home fragment
     */
    public  HikeHelper(Context activeContext, Button startButton, Button stopButton){
        this.context = activeContext;
        myGPSHelper = new GPSHelper(context);
        myGPSHelper.getAndHandleLastLocation();
        myDatabaseHelper = new StepAppOpenHelper(context);
        setStarted(false);
        setHaveBreak(false);
        setButtonColor1(R.color.md_theme_light_secondaryContainer);
        setButtonColor2(R.color.md_theme_dark_inversePrimary);
        this.startButton = startButton;
        this.stopButton = stopButton;
        inspirationalQuotes = context.getResources().getStringArray(R.array.inspirational_quotes);
    }


    /**
     * Method to handle start hike button logic
     * Creates a new hike in the database and then starts the call for the background service
     * Also checks for Location permission to ensure that gps works
     * Different logic for start new hike and start hike after break
     */
    public void startHike() {
        if (!haveBreak && !started) {
            id = myDatabaseHelper.getLastId(myDatabaseHelper.getWritableDatabase()) + 1;
            //insertDummyHikeLuganoToBellinzonaWithGPS();
            myGPSHelper.checkAndRequestPermissions();
            myDatabaseHelper.insertHikeData();
            setStarted(true);
            changeButtonColor(startButton, buttonColor2);
            changeButtonColor(stopButton,buttonColor1);
            Log.d("BOOLEAN CHANGED", "started: " + started);
            //setNotifications();
            sendToDatabase(120,false);
        } else if (haveBreak && started){
            setHaveBreak(false);
            sendToDatabase(120, false);
            changeButtonColor(startButton, buttonColor2);
            changeButtonColor(stopButton,buttonColor1);
            Log.d("BOOLEAN CHANGED", "haveBreak: " + haveBreak);
        }
    }

    /**
     * This functions handles the end of a hike.
     * Removes any pending callbacks for the handler and therefore stops calls to database
     * Saves a last geopoint in the database
     * calculates distance
     * updates steps and distance in database
     */
    public void endHike(StepCounterListener sensorListener) {
        setStarted(false);
        handler.removeCallbacksAndMessages(null);
        myGPSHelper.getAndHandleLastLocation();
        Log.d("FunctionLog", "Saved last Location");
        myDatabaseHelper.insertGPSData(myGPSHelper.getLongitude(), myGPSHelper.getLatitude(), myGPSHelper.getAltitude(), id);

        mapsHelper = new OpenStreetMapsHelper(context, myDatabaseHelper.getGeoPointsById(id));
        myDatabaseHelper.updateHikeDistance(id,mapsHelper.getTotalDistanceInKm());

        Log.d("TEST", "sendToDatabase");
        sendToDatabase(0, true);


        changeButtonColor(startButton,buttonColor1);
        changeButtonColor(stopButton,buttonColor1);
        if (sensorListener != null){
            myDatabaseHelper.updateHikeData(id, sensorListener.getAccStepCounter() );
            Log.d("Steps", "Step count: " + sensorListener.getAccStepCounter() );
            System.out.println(sensorListener.getAccStepCounter());
        } else {
            Log.e("WARNING", "No SensorListener active");
            Toast.makeText(context, "No active hike", Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * Function to send the GPS data to the database in an adjustable time interval
     * Accesses gpsHelper to update the current position and then retrieves the values and inserts them to database
     * ALso checks if user has a break at the moment and stops sending the data if active
     */
    private void sendToDatabase(int seconds, boolean stopService) {
        Log.d("TEST", "sendUpdateToDatabase");
        Intent serviceIntent = new Intent(context, HikeService.class);
        serviceIntent.putExtra("id", id);
        serviceIntent.putExtra("seconds", seconds);
        serviceIntent.putExtra("started", started);
        serviceIntent.putExtra("haveBreak", haveBreak);
        if (stopService) {
            // Add an extra to indicate that the service should be stopped
            serviceIntent.putExtra("stopService", true);
        }

        ContextCompat.startForegroundService(context, serviceIntent);

    }



    /**
     * Method to change color of buttons
     *
     * @param button button you want to change
     * @param color color you want to set
     */
    public void changeButtonColor(Button button, int color){
        button.setBackgroundColor(ContextCompat.getColor(context, color));
    }

    /**
     * Method to handle the functionality of the break button
     * interrupts the database background service
     * changes button color
     */
    public void setBreak(){
        if (started) {
            setHaveBreak(true);
            Log.d("BOOLEAN CHANGED", "HAVE BREAK: " + haveBreak);
            changeButtonColor(stopButton,buttonColor2);
            //stopButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_dark_inversePrimary));
            changeButtonColor(startButton,buttonColor1);
            Log.d("TEST", "sendToDatabase");
            sendToDatabase(0, true);
            //startButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_light_secondaryContainer));

        }
    }

    /**
     * Class to create achievements
     * no sources needed because its just a simple class and i know how to code this
     */
    public void insertDummyHikeLuganoToBellinzonaWithGPS() {
        id = myDatabaseHelper.getLastId(myDatabaseHelper.getWritableDatabase()) + 1;
        myDatabaseHelper.insertHikeData();
        // Insert the hike details
        // Define GPS points for a circular path
        myDatabaseHelper.insertGPSData( 8.9164, 45.9897,200, id);
        myDatabaseHelper.insertGPSData( 8.9382, 46.0015,255, id);
        myDatabaseHelper.insertGPSData( 8.9527, 46.0158,343, id);
        myDatabaseHelper.insertGPSData( 8.9164, 46.0276,243, id);
        myDatabaseHelper.insertGPSData( 8.9873, 46.0391,100, id);
        myDatabaseHelper.insertGPSData( 9.0173, 46.1984,255, id);

        mapsHelper = new OpenStreetMapsHelper(context, myDatabaseHelper.getGeoPointsById(id));
        myDatabaseHelper.updateHikeDistance(id,mapsHelper.getTotalDistanceInKm());

    }



    /**
     * Retrieves the instance of the StepAppOpenHelper used by this HikeHelper instance.
     *
     * @return An instance of the StepAppOpenHelper class.
     */
    public StepAppOpenHelper getMyDatabaseHelper() {
        return myDatabaseHelper;
    }

    /**
     * Gets the current hike ID associated with this HikeHelper instance.
     *
     * @return An integer representing the current hike ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the hike ID for this HikeHelper instance.
     *
     * @param id The ID to be set for the hike.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the color for the first button used in the UI.
     *
     * @param buttonColor1 The color resource ID for the first button.
     */
    public void setButtonColor1(int buttonColor1) {
        this.buttonColor1 = buttonColor1;
    }

    /**
     * Sets the color for the second button used in the UI.
     *
     * @param buttonColor2 The color resource ID for the second button.
     */
    public void setButtonColor2(int buttonColor2) {
        this.buttonColor2 = buttonColor2;
    }

    /**
     * Sets the status of whether the hike has started or not.
     *
     * @param started true if the hike has started, false otherwise.
     */
    public void setStarted(boolean started) {
        this.started = started;
    }

    /**
     * Sets the status of whether the hike is currently on a break.
     *
     * @param haveBreak true if the hike is on a break, false otherwise.
     */
    public void setHaveBreak(boolean haveBreak) {
        this.haveBreak = haveBreak;
    }

    /**
     * Sets a random inspirational quote to the provided TextView.
     *
     * @param quoteText The TextView to which the random quote will be set.
     */
    public void setRandomQuote(TextView quoteText) {
        if (inspirationalQuotes.length > 0) {
            int randomIndex = new Random().nextInt(inspirationalQuotes.length);
            quoteText.setText(inspirationalQuotes[randomIndex]);
        }
    }




}
