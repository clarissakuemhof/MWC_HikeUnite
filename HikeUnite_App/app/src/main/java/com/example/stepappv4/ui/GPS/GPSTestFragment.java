package com.example.stepappv4.ui.GPS;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Paint;
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
import android.graphics.Color;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.stepappv4.R;
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
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;



public class GPSTestFragment extends Fragment implements MapListener, GpsStatus.Listener {


    private ImageView icon;

    private MapView mMap;
    private IMapController controller;
    private MyLocationNewOverlay mMyLocationOverlay;

    private FusedLocationProviderClient fusedLocationClient;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private TextView latInfoTV, longInfoTV;
    private Button refreshLocation;

    private Paint red = new Paint();







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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        latInfoTV = root.findViewById(R.id.latInfo);
        longInfoTV = root.findViewById(R.id.longInfo);
        refreshLocation = root.findViewById(R.id.refreshLocation);

        red.setColor(Color.RED);
        red.setStrokeWidth(20);


        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

            // The onRequestPermissionsResult method will be called asynchronously
            // to handle the user's response to the permission request.

        } else {
            updateLocation();
        }

        refreshLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocation();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMap();
        addPolyline();
    }

    private void updateLocation() {
        // Get last known location
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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

                            // Update UI or perform actions with latitude and longitude
                            String latitudeText = Double.toString(latitude);
                            String longitudeText = Double.toString(longitude);

                            latInfoTV.setText(latitudeText);
                            longInfoTV.setText(longitudeText);
                        } else {
                            latInfoTV.setText("Lat not available");
                            longInfoTV.setText("Long not available");
                        }
                    }
                });

    }

    private void initMap() {
        Configuration.getInstance().load(
                requireActivity().getApplicationContext(),
                requireActivity().getSharedPreferences(getString(R.string.app_name), requireActivity().MODE_PRIVATE)
        );

        mMap = binding.osmmap;
        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.setMultiTouchControls(true);

        mMyLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()), mMap);
        controller = mMap.getController();

        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableFollowLocation();
        mMyLocationOverlay.setDrawAccuracyEnabled(true);
        mMyLocationOverlay.runOnFirstFix(() -> requireActivity().runOnUiThread(() -> {
            controller.setCenter(mMyLocationOverlay.getMyLocation());
            controller.animateTo(mMyLocationOverlay.getMyLocation());
        }));

        controller.setZoom(6.0);

        Log.e("TAG", "onCreate:in " + controller.zoomIn());
        Log.e("TAG", "onCreate: out  " + controller.zoomOut());

        mMap.getOverlays().add(mMyLocationOverlay);
        mMap.addMapListener(this);
    }

    @Override
    public boolean onScroll(ScrollEvent event) {
        Log.e("TAG", "onCreate:la " + event.getSource().getMapCenter().getLatitude());
        Log.e("TAG", "onCreate:lo " + event.getSource().getMapCenter().getLongitude());
        return true;
    }

    @Override
    public boolean onZoom(ZoomEvent event) {
        Log.e("TAG", "onZoom zoom level: " + event.getZoomLevel() + "   source:  " + event.getSource());
        return false;
    }

    @Override
    public void onGpsStatusChanged(int event) {
        // Your implementation for GPS status change
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addPolyline() {
        List<GeoPoint> dummyRoutePoints = getHalfUSRoutePoints();

        Polyline polyline = new Polyline();
        polyline.setPoints(dummyRoutePoints);
        polyline.getOutlinePaint().set(red);
        polyline.isVisible();

        mMap.getOverlayManager().add(polyline);
        mMap.invalidate();
    }
}