package com.example.stepappv4.ui.Home;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.stepappv4.R;
import com.example.stepappv4.databinding.FragmentHomeBinding;
import com.example.stepappv4.ui.HelperClass.HikeHelper;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;

/**
 * Home fragment has the main View of our app. Here the user can see an inspirational quote and also start, stop and end hikes.
 * It also display the step count during hikes
 */
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
        hikeHelper = new HikeHelper(getContext(), startButton, stopButton, sensorManager);
        hikeHelper.setRandomQuote(quoteText);



        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("DatabaseUpdate", "Start button clicked");
                if (viewSwitcher.getCurrentView() == root.findViewById(R.id.defaultView)) {
                    viewSwitcher.showNext();
                }
                hikeHelper.startHike(accSensor, stepCountsView, progressBar);
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
                if(!hikeHelper.isHaveBreak()){
                    if(viewSwitcher.getCurrentView() == root.findViewById(R.id.progressView)) {
                        hikeHelper.setRandomQuote(quoteText);
                        viewSwitcher.showPrevious();
                    }
                }
                hikeHelper.endHike();
                stepCountsView.setText("0");
                progressBar.setProgress(0);
            }
        });

        dummyHike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hikeHelper.insertDummyHikeLuganoToBellinzonaWithGPS(1);
                hikeHelper.insertDummyHikeLuganoToBellinzonaWithGPS(2);
                hikeHelper.insertDummyHikeLuganoToBellinzonaWithGPS(3);
                hikeHelper.insertDummyHikeLuganoToBellinzonaWithGPS(4);
                hikeHelper.insertDummyHikeLuganoToBellinzonaWithGPS(5);
                hikeHelper.insertDummyHikeLuganoToBellinzonaWithGPS(6);
                hikeHelper.insertDummyHikeLuganoToBellinzonaWithGPS(7);


            }
        });


        toggleButtonGroup = (MaterialButtonToggleGroup) root.findViewById(R.id.toggleButtonGroup);
        toggleButtonGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (group.getCheckedButtonId() ==R.id.start_button) {
                    if (accSensor != null)
                    {
                        //sensorListener = new StepCounterListener(stepCountsView, progressBar, hikeHelper.getMyDatabaseHelper().getWritableDatabase());
                        //sensorManager.registerListener(sensorListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
                        Toast.makeText(getContext(), R.string.start_text, Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getContext(), R.string.acc_sensor_not_available, Toast.LENGTH_LONG).show();
                    }
                }
                if(group.getCheckedButtonId() == R.id.stop_button){
                    sensorManager.unregisterListener(sensorListener);
                    //Toast.makeText(getContext(), R.string.stop_text, Toast.LENGTH_LONG).show();
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

