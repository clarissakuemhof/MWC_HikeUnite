package com.example.stepappv4.ui.GPS;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.util.Log;

import com.example.stepappv4.R;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.util.List;

/**
 * This is the class to handle all methods needed to initialize the maps and to draw the route based on the saved GeoPoints
 * based on:
 * <a href="https://medium.com/@mr.appbuilder/how-to-integrate-and-work-with-open-street-map-osm-in-an-android-app-kotlin-564b38590bfe">Open Street Map Tutorial</a>
 * <a href="https://osmdroid.github.io/osmdroid/javadocAll/org/osmdroid/views/overlay/PolyOverlayWithIW.html#getOutlinePaint--">Documentation for Route Overlay</a>
 */
public class OpenStreetMapsHelper implements MapListener {

    private MapView mMap;
    private IGeoPoint centerPoint;
    private MyLocationNewOverlay mMyLocationOverlay;

    private IMapController controller;


    private float totalDistanceInKm = 0.0f;
    private float totalDistance = 0.0f;
    private Location lastLocation;

    private List<GeoPoint> hikeRoute;

    private Paint red = new Paint();

    public OpenStreetMapsHelper(Context context, MapView mapView, List<GeoPoint> hikeRoute) {
        Configuration.getInstance().load(
                context,
                context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
        );

        mMap = mapView;
        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.setMultiTouchControls(true);
        mMap.addMapListener(this);
        this.hikeRoute = hikeRoute;

        red.setColor(Color.RED);
        red.setStrokeWidth(20);

        calculateDistance();
        initMap();
        addPolyline();

    }

    public void initMap() {
        Configuration.getInstance().load(
                mMap.getContext().getApplicationContext(),
                mMap.getContext().getSharedPreferences("MapApp", mMap.getContext().MODE_PRIVATE)
        );

        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.setMultiTouchControls(true);

        mMyLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(mMap.getContext()), mMap);
        controller = mMap.getController();

        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableFollowLocation();
        mMyLocationOverlay.setDrawAccuracyEnabled(true);
        mMyLocationOverlay.runOnFirstFix(() -> ((Activity) mMap.getContext()).runOnUiThread(() -> {
            controller.setCenter(mMyLocationOverlay.getMyLocation());
            controller.animateTo(mMyLocationOverlay.getMyLocation());
        }));

        controller.setZoom(6.0);

        mMap.getOverlays().add(mMyLocationOverlay);
    }

    // You can add more map-related methods here

    public MapView getMapView() {
        return mMap;
    }

    public IMapController getMapController() {
        return controller;
    }

    public MyLocationNewOverlay getMyLocationOverlay() {
        return mMyLocationOverlay;
    }


    public void onResume() {
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableFollowLocation();
        mMyLocationOverlay.setDrawAccuracyEnabled(true);
        mMyLocationOverlay.runOnFirstFix(() -> {
            centerPoint = mMyLocationOverlay.getMyLocation();
            mMap.getController().setCenter(centerPoint);
            mMap.getController().animateTo(centerPoint);
        });
    }

    public void onPause() {
        mMyLocationOverlay.disableMyLocation();
        mMyLocationOverlay.disableFollowLocation();
    }

    public void onDestroy() {
        mMap.getOverlays().remove(mMyLocationOverlay);
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

    public void updateLocation(Location newLocation) {
        // Your implementation for updating UI or performing actions with the new location
    }

    public void calculateDistance() {
            float[] distance = new float[1];
            for (int i = 0; i < hikeRoute.size() - 1; i++) {
                Location.distanceBetween(
                        hikeRoute.get(i).getLatitude(), hikeRoute.get(i).getLongitude(),
                        hikeRoute.get(i + 1).getLatitude(), hikeRoute.get(i + 1).getLongitude(),
                        distance
                );
                totalDistance += distance[0];
            }

            // Convert totalDistance from meters to kilometers
            totalDistanceInKm = totalDistance / 1000.0f;

    }



    public void addPolyline() {

        Polyline polyline = new Polyline();
        polyline.setPoints(hikeRoute);
        polyline.getOutlinePaint().set(red);
        polyline.isVisible();

        mMap.getOverlayManager().add(polyline);
        mMap.invalidate();
    }

    public float getTotalDistanceInKm() {
        return totalDistanceInKm;
    }



}
