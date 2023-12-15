package com.example.stepappv4.ui.GPS;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.stepappv4.R;
import com.example.stepappv4.StepAppOpenHelper;
import com.example.stepappv4.databinding.FragmentGpstestBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class GPSTestFragment extends Fragment  {

    private ImageView icon;
    private MapView mMap;
    private OpenStreetMapsHelper mapHelper;

    private FusedLocationProviderClient fusedLocationClient;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private TextView latInfoTV, longInfoTV, altInfoTV, distInfoTV;
    private Button refreshLocation;

    private GPSHelper gpsHelper;

    private StepAppOpenHelper myDatabaseHelper;

    // Assuming you have a method to get a list of GeoPoints representing your route
    private List<GeoPoint> getHalfUSRoutePoints() {
        List<GeoPoint> routePoints = new ArrayList<>();

        // Starting point
        routePoints.add(new GeoPoint(37.7749, -122.4194)); // San Francisco, CA

        // Route points covering roughly half the size of the US
        routePoints.add(new GeoPoint(39.9526, -75.1652)); // Philadelphia, PA
        routePoints.add(new GeoPoint(40.7128, -74.0060)); // New York, NY
        routePoints.add(new GeoPoint(41.8781, -87.6298)); // Chicago, IL
        routePoints.add(new GeoPoint(29.7604, -95.3698)); // Houston, TX
        routePoints.add(new GeoPoint(32.7767, -96.7970)); // Dallas, TX
        routePoints.add(new GeoPoint(34.0522, -118.2437)); // Los Angeles, CA

        // Ending point
        routePoints.add(new GeoPoint(34.0522, -118.2437)); // Los Angeles, CA

        return routePoints;
    }

    private FragmentGpstestBinding binding;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);

        binding = FragmentGpstestBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        myDatabaseHelper = new StepAppOpenHelper(this.getContext());

        mMap = binding.osmmap;
        mapHelper = new OpenStreetMapsHelper(this.getContext(), mMap, myDatabaseHelper.getGeoPointsById(6));

        gpsHelper = new GPSHelper(this.getContext());

        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        latInfoTV = root.findViewById(R.id.latInfo);
        longInfoTV = root.findViewById(R.id.longInfo);
        altInfoTV = root.findViewById(R.id.altInfo);
        distInfoTV = root.findViewById(R.id.distance);
        refreshLocation = root.findViewById(R.id.refreshLocation);



        // if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
        //  ActivityCompat.requestPermissions(requireActivity(),
        //          new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
        //          MY_PERMISSIONS_REQUEST_LOCATION);

            // The onRequestPermissionsResult method will be called asynchronously
            // to handle the user's response to the permission request.

        // } else {
        //    updateLocation();
        // }

        refreshLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                double latitude = gpsHelper.getLatitude();
                double longitude = gpsHelper.getLongitude();
                double altitude = gpsHelper.getAltitude();
                float distance = mapHelper.getTotalDistanceInKm();

                // Update UI or perform actions with latitude and longitude
                String latitudeText = Double.toString(latitude);
                String longitudeText = Double.toString(longitude);
                String altitudeText = Double.toString(altitude);
                String distanceText = distance + " kilometers";


                latInfoTV.setText(latitudeText);
                longInfoTV.setText(longitudeText);
                altInfoTV.setText(altitudeText);
                distInfoTV.setText(distanceText);
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapHelper.initMap();
        //mapHelper.addPolyline();
    }

    /**
    private void updateLocation() {
        // Get last known location
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            double altitude = location.getAltitude();
                            float distance = mapHelper.getTotalDistanceInKm();

                            // Update UI or perform actions with latitude and longitude
                            String latitudeText = Double.toString(latitude);
                            String longitudeText = Double.toString(longitude);
                            String altitudeText = Double.toString(altitude);
                            String distanceText = distance + " kilometers";


                            latInfoTV.setText(latitudeText);
                            longInfoTV.setText(longitudeText);
                            altInfoTV.setText(altitudeText);
                            distInfoTV.setText(distanceText);

                        } else {
                            latInfoTV.setText("Lat not available");
                            longInfoTV.setText("Long not available");
                            altInfoTV.setText("Alt not available");
                        }
                    }
                });
    }
     */




}