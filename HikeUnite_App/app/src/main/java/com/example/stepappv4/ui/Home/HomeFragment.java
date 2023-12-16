package com.example.stepappv4.ui.Home;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.stepappv4.StepAppOpenHelper;
import com.example.stepappv4.R;
import com.example.stepappv4.databinding.FragmentHomeBinding;
import com.example.stepappv4.ui.HelperClass.GPSHelper;
import com.example.stepappv4.ui.HelperClass.OpenStreetMapsHelper;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.TimeZone;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView stepCountsView, quoteText;
    private CircularProgressIndicator progressBar;

    private MaterialButtonToggleGroup toggleButtonGroup;

    private Sensor accSensor, stepDetectorSensor;
    private SensorManager sensorManager;
    private StepCounterListener sensorListener;

    private ViewSwitcher viewSwitcher;
    private Button startButton,stopButton, endButton;

    private String[] inspirationalQuotes;

    private int id, steps, buttonColor1, buttonColor2;
    private float distance;
    private GPSHelper gpsHelper;
    private StepAppOpenHelper myDatabaseHelper;
    private boolean started;


    private boolean haveBreak;
    private final Handler handler = new Handler();
    private OpenStreetMapsHelper mapsHelper;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


//-----------------------GPS Helper Class---------------------------------
        gpsHelper = new GPSHelper(this.getContext());
//-----------------------Database Helper Class----------------------------
        myDatabaseHelper = new StepAppOpenHelper(this.getContext());
        SQLiteDatabase database = myDatabaseHelper.getWritableDatabase();
        started = false;
        haveBreak = false;
//----------------------------ColorsButton-------------------------------
        buttonColor1 = R.color.md_theme_light_secondaryContainer;
        buttonColor2 = R.color.md_theme_dark_inversePrimary;
//----------------------------Progress Bar-------------------------------
        progressBar = (CircularProgressIndicator) root.findViewById(R.id.progressBar);
        progressBar.setMax(10000);
        progressBar.setProgress(0);
//---------------------------Accelerometer-------------------------------
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
//----------------------------Creating View-------------------------------
        viewSwitcher = root.findViewById(R.id.viewSwitcher);
        startButton = root.findViewById(R.id.start_button);
        stopButton = root.findViewById(R.id.stop_button);
        endButton = root.findViewById(R.id.end_button);
        quoteText = root.findViewById(R.id.quote_text);
        inspirationalQuotes = getResources().getStringArray(R.array.inspirational_quotes);
        stepCountsView = (TextView) root.findViewById(R.id.counter);
        stepCountsView.setText("0");
        setRandomQuote();


        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("DatabaseUpdate", "Start button clicked");
                if (viewSwitcher.getCurrentView() == root.findViewById(R.id.defaultView)) {
                    viewSwitcher.showNext();
                }
                startHike();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (started) {
                    setHaveBreak(true);
                    Log.d("BOOLEAN CHANGED", "HAVE BREAK: " + haveBreak);
                    changeButtonColor(stopButton,buttonColor2);
                    //stopButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_dark_inversePrimary));
                    changeButtonColor(startButton,buttonColor1);
                    //startButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_light_secondaryContainer));

                }


            }
        });

        endButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(viewSwitcher.getCurrentView() == root.findViewById(R.id.progressView)){
                    setRandomQuote();
                    viewSwitcher.showPrevious();
                }
                endHike();
            }
        });


        toggleButtonGroup = (MaterialButtonToggleGroup) root.findViewById(R.id.toggleButtonGroup);
        toggleButtonGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (group.getCheckedButtonId() ==R.id.start_button) {
                    if (accSensor != null)
                    {
                        sensorListener = new StepCounterListener(stepCountsView, progressBar, database);
                        sensorManager.registerListener(sensorListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
                        Toast.makeText(getContext(), R.string.start_text, Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getContext(), R.string.acc_sensor_not_available, Toast.LENGTH_LONG).show();
                    }
                }
                if(group.getCheckedButtonId() == R.id.stop_button){
                    sensorManager.unregisterListener(sensorListener);
                    Toast.makeText(getContext(), R.string.stop_text, Toast.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }

    private void setRandomQuote() {
        if (inspirationalQuotes.length > 0) {
            int randomIndex = new Random().nextInt(inspirationalQuotes.length);
            quoteText.setText(inspirationalQuotes[randomIndex]);
        }
    }

    /**
     * Method is used to create dummy data to test map feature
     */
    private void insertDummyHikeLuganoToBellinzonaWithGPS() {
        // Insert the hike details
        // Define GPS points for a circular path
        myDatabaseHelper.insertGPSData( 45.9897, 8.9164,200, id);
        myDatabaseHelper.insertGPSData( 46.0015, 8.9382,255, id);
        myDatabaseHelper.insertGPSData( 46.0158, 8.9527,343, id);
        myDatabaseHelper.insertGPSData( 46.0276, 8.9164,243, id);
        myDatabaseHelper.insertGPSData( 46.0391, 8.9873,345, id);
        myDatabaseHelper.insertGPSData( 46.1984, 9.0173,255, id);

    }

    /**
     * Setter for is started value
     * If hike is started the sendToDatabase function will start running
     * @param started indicates if a hike is started at the moment.
     */
    public void setStarted(boolean started) {
        this.started = started;
    }

    /**
     * Setter for haveBreak value
     * If haveBreak is true the app will stop sending the location to the database
     * @param haveBreak indicates if user makes a break during the hike
     */
    public void setHaveBreak(boolean haveBreak) {
        this.haveBreak = haveBreak;
    }

    /**
     * Function to send the GPS data to the database in an adjustable time interval
     * Accesses gpsHelper to update the current position and then retrieves the values and inserts them to database
     * ALso checks if user has a break at the moment and stops sending the data if active
     */
    private void sendToDatabase() {
        if (started) {
            handler.postDelayed(() -> {
                if (!haveBreak) {
                    gpsHelper.getAndHandleLastLocation();
                    Log.d("FunctionLog", "Updated Location");
                    myDatabaseHelper.insertGPSData(gpsHelper.getLongitude(), gpsHelper.getLatitude(), gpsHelper.getAltitude(), id);
                    // Check the flag again before scheduling the next call
                    sendToDatabase();
                }
            }, 30000); // 30 seconds delay
        }
    }

    private void startHike() {
        if (!haveBreak) {
            id = myDatabaseHelper.getLastId(myDatabaseHelper.getWritableDatabase()) + 1;
            myDatabaseHelper.insertHikeData(0, 0, "YourHike"+ id);
            //insertDummyHikeLuganoToBellinzonaWithGPS();
            myDatabaseHelper.insertGPSData(gpsHelper.getLongitude(),gpsHelper.getLatitude(), gpsHelper.getAltitude(),id);
            setStarted(true);
            changeButtonColor(startButton, buttonColor2);
            changeButtonColor(stopButton,buttonColor1);
            Log.d("BOOLEAN CHANGED", "started: " + started);
            sendToDatabase();
        } else {
            setHaveBreak(false);
            sendToDatabase();
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
    private void endHike() {
        setStarted(false);
        handler.removeCallbacksAndMessages(null);
        gpsHelper.getAndHandleLastLocation();
        Log.d("FunctionLog", "Saved last Location");
        myDatabaseHelper.insertGPSData(gpsHelper.getLongitude(), gpsHelper.getLatitude(), gpsHelper.getAltitude(), id);
        mapsHelper = new OpenStreetMapsHelper(getContext(),myDatabaseHelper.getGeoPointsById(id));
        distance = mapsHelper.getTotalDistanceInKm();
        myDatabaseHelper.updateHikeData(id,steps, distance );
        changeButtonColor(startButton,buttonColor1);
    }

    /**
     * Method to change color of buttons
     * @param button button you want to change
     * @param color color you want to set
     */
    public void changeButtonColor(Button button, int color){
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), color));
    }

}

class  StepCounterListener implements SensorEventListener{

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
    int stepThreshold = 6;
    TextView stepCountsView;
    CircularProgressIndicator progressBar;
    private SQLiteDatabase database;
    private String timestamp;
    private String day;
    private String hour;


    public StepCounterListener(TextView stepCountsView, CircularProgressIndicator progressBar,  SQLiteDatabase database)
    {
        this.stepCountsView = stepCountsView;
        this.database = database;
        this.progressBar = progressBar;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType())
        {
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

                if ((currentTimeInMilliSecond - lastSensorUpdate) > 1000)
                {
                    lastSensorUpdate = currentTimeInMilliSecond;
                    String sensorRawValues = "  x = "+ String.valueOf(x) +"  y = "+ String.valueOf(y) +"  z = "+ String.valueOf(z);
                    //Log.d("Acc. Event", "last sensor update at " + String.valueOf(sensorEventDate) + sensorRawValues);
                }

                // Calculate the acceleration vector
                accMag = Math.sqrt(x*x+y*y+z*z);

                accSeries.add((int) accMag);
                // Add a low-pass filter to smooth and to reduce noise
                // alpha = smoothing factor
                // (1- alpha) = weight assigned for the smoothAccMAg
                smoothAccMag = alpha * accMag + (1- alpha) * smoothAccMag;


                accSeries.add((int) smoothAccMag);
                // Get the date, the day and the hour
                timestamp = sensorEventDate;
                day = sensorEventDate.substring(0,10);
                hour = sensorEventDate.substring(11,13);
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
        /* Peak detection algorithm derived from: A Step Counter Service for Java-Enabled Devices Using a Built-In Accelerometer Mladenov et al.
         */
        int currentSize = accSeries.size(); // get the length of the series
        if (currentSize - lastAddedIndex < windowSize) { // if the segment is smaller than the processing window size skip it
            return;
        }

        List<Integer> valuesInWindow = accSeries.subList(lastAddedIndex,currentSize);
        List<String> timePointList = timestampsSeries.subList(lastAddedIndex,currentSize);
        lastAddedIndex = currentSize;

        for (int i = 1; i < valuesInWindow.size()-1; i++) {
            int forwardSlope = valuesInWindow.get(i + 1) - valuesInWindow.get(i);
            int downwardSlope = valuesInWindow.get(i) - valuesInWindow.get(i - 1);

            if (forwardSlope < 0 && downwardSlope > 0 && valuesInWindow.get(i) > stepThreshold) {
                accStepCounter += 1;
                Log.d("ACC STEPS: ", String.valueOf(accStepCounter));
                stepCountsView.setText(String.valueOf(accStepCounter));
                progressBar.setProgress(accStepCounter);



            }
        }
    }
}


