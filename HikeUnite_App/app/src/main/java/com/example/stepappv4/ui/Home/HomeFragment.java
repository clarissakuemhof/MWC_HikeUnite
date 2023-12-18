package com.example.stepappv4.ui.Home;

import android.content.Context;
import android.content.Intent;
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
import com.example.stepappv4.ui.HelperClass.HikeHelper;
import com.example.stepappv4.ui.HelperClass.OpenStreetMapsHelper;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.ParseException;
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
    private Button startButton,stopButton, endButton, dummyHike;


    private HikeHelper hikeHelper;




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


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
        dummyHike = root.findViewById(R.id.InsertDummyHike);
        stepCountsView = (TextView) root.findViewById(R.id.counter);
        stepCountsView.setText("0");
        hikeHelper = new HikeHelper(getContext(), startButton, stopButton);
        hikeHelper.setRandomQuote(quoteText);



        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("DatabaseUpdate", "Start button clicked");
                if (viewSwitcher.getCurrentView() == root.findViewById(R.id.defaultView)) {
                    viewSwitcher.showNext();
                }
                hikeHelper.startHike();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                hikeHelper.setBreak();
            }
        });

        endButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(viewSwitcher.getCurrentView() == root.findViewById(R.id.progressView)){
                    hikeHelper.setRandomQuote(quoteText);
                    viewSwitcher.showPrevious();
                }
                hikeHelper.endHike(sensorListener);
            }
        });

        dummyHike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hikeHelper.insertDummyHikeLuganoToBellinzonaWithGPS();
            }
        });


        toggleButtonGroup = (MaterialButtonToggleGroup) root.findViewById(R.id.toggleButtonGroup);
        toggleButtonGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (group.getCheckedButtonId() ==R.id.start_button) {
                    if (accSensor != null)
                    {
                        sensorListener = new StepCounterListener(stepCountsView, progressBar, hikeHelper.getMyDatabaseHelper().getWritableDatabase());
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

}

