package com.example.stepappv4.ui.Home;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.stepappv4.R;
import com.example.stepappv4.StepAppOpenHelper;
import com.example.stepappv4.databinding.FragmentHomeBinding;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class StepCountingService extends Service {

    private FragmentHomeBinding binding;

    private final IBinder binder = new StepCountingBinder();
    private SensorManager sensorManager;
    private Sensor accSensor;
    private StepCounterListener sensorListener;
    private CircularProgressIndicator progressBar;
    private TextView stepCountsView;

    public class StepCountingBinder extends Binder{
        StepCountingService getService(){
            return StepCountingService.this;
        }
    }


    public void onCreate(){
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        StepAppOpenHelper stepAppOpenHelper = new StepAppOpenHelper(this);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        SQLiteDatabase database = stepAppOpenHelper.getWritableDatabase();
        stepCountsView = (TextView) root.findViewById(R.id.counter);
        progressBar = (CircularProgressIndicator) root.findViewById(R.id.progressBar);
        sensorListener = new StepCounterListener(stepCountsView, progressBar, database);

        if(accSensor != null){
            sensorManager.registerListener(sensorListener, accSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId){
        if(accSensor != null) {
            sensorManager.registerListener(sensorListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        return START_STICKY;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        // Unregister the sensor listener when the service is destroyed
        if (sensorManager != null && sensorListener != null) {
            sensorManager.unregisterListener(sensorListener);
        }
    }


    @Override
    public IBinder onBind(Intent intent){
        return binder;
    }
}
