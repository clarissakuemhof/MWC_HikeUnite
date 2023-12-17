package com.example.stepappv4.ui.Home;

import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class StepCounterListener implements SensorEventListener {

    private long lastSensorUpdate = 0;
    public static int accStepCounter = 0;
    ArrayList<Integer> accSeries = new ArrayList<Integer>();
    ArrayList<String> timestampsSeries = new ArrayList<String>();
    private double accMag = 0;
    // Everything in double since it's more precise (32-bit FP < 64-bit FP)
    private double smoothAccMag = 0;
    // the value requires imperative testing
    // value is between 0 and 1
    private double alpha = 0.8;
    private int lastAddedIndex = 1;
    int stepThreshold = 4;
    TextView stepCountsView;
    CircularProgressIndicator progressBar;
    private SQLiteDatabase database;
    private String timestamp;
    private String day;
    private String hour;
    private long lastStepTime = 0;

    public StepCounterListener(TextView stepCountsView, CircularProgressIndicator progressBar, SQLiteDatabase database) {
        this.stepCountsView = stepCountsView;
        this.database = database;
        this.progressBar = progressBar;
    }

    public int getAccStepCounter() {
        return accStepCounter;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_LINEAR_ACCELERATION:

                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                long currentTimeInMilliSecond = System.currentTimeMillis();

                long timeInMillis = currentTimeInMilliSecond + (sensorEvent.timestamp - SystemClock.elapsedRealtimeNanos()) / 1000000;

                // Convert the timestamp to date
                SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                jdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
                String sensorEventDate = jdf.format(timeInMillis);

                if ((currentTimeInMilliSecond - lastSensorUpdate) > 1000) {
                    lastSensorUpdate = currentTimeInMilliSecond;
                    String sensorRawValues = "  x = " + String.valueOf(x) + "  y = " + String.valueOf(y) + "  z = " + String.valueOf(z);
                    //Log.d("Acc. Event", "last sensor update at " + String.valueOf(sensorEventDate) + sensorRawValues);
                }

                // Calculate the acceleration vector
                accMag = Math.sqrt(x * x + y * y + z * z);

                accSeries.add((int) accMag);
                // Add a low-pass filter to smooth and to reduce noise
                // alpha = smoothing factor
                // (1- alpha) = weight assigned for the smoothAccMAg
                smoothAccMag = alpha * accMag + (1 - alpha) * smoothAccMag;


                accSeries.add((int) smoothAccMag);
                // Get the date, the day and the hour
                timestamp = sensorEventDate;
                day = sensorEventDate.substring(0, 10);
                hour = sensorEventDate.substring(11, 13);
                //Log.d("SensorEventTimestampInMilliSecond", timestamp);
                timestampsSeries.add(timestamp);
                peakDetection();

                break;

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    private void peakDetection() {

        int windowSize = 20;
        long timeConstraintMillis = 500;
        /* Peak detection algorithm derived from: A Step Counter Service for Java-Enabled Devices Using a Built-In Accelerometer Mladenov et al.
         */
        int currentSize = accSeries.size(); // get the length of the series
        if (currentSize - lastAddedIndex < windowSize) { // if the segment is smaller than the processing window size skip it
            return;
        }

        List<Integer> valuesInWindow = accSeries.subList(lastAddedIndex, currentSize);
        lastAddedIndex = currentSize;

        for (int i = 1; i < valuesInWindow.size() - 1; i++) {
            int forwardSlope = valuesInWindow.get(i + 1) - valuesInWindow.get(i);
            int downwardSlope = valuesInWindow.get(i) - valuesInWindow.get(i - 1);

            if (forwardSlope < 0 && downwardSlope > 0 && valuesInWindow.get(i) > stepThreshold) {
                long currentTime = System.currentTimeMillis();
                // Log.d("Clock", "currentTime - lastStepTime: " + (currentTime - lastStepTime));

                if (currentTime - lastStepTime > timeConstraintMillis) {
                    accStepCounter += 1;
                    lastStepTime = currentTime;
                    Log.d("ACC STEPS: ", String.valueOf(accStepCounter));
                    stepCountsView.setText(String.valueOf(accStepCounter));
                    progressBar.setProgress(accStepCounter);
                }
            }
        }
    }

}
