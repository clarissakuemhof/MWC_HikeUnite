package com.example.stepappv4.ui.GPS;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.util.Log;

import com.example.stepappv4.R;

import org.osmdroid.api.IGeoPoint;
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

import java.util.ArrayList;
import java.util.List;

public class OpenStreetMapsHelper implements MapListener {

    private MapView mMap;
    private IGeoPoint centerPoint;
    private MyLocationNewOverlay mMyLocationOverlay;
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

        initMap();
        addPolyline();

    }

    private void initMap() {
        mMyLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(mMap.getContext()), mMap);
        mMap.getOverlays().add(mMyLocationOverlay);
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

    public float calculateDistance(Location newLocation) {
        float totalDistanceInKm = 0;
        if (lastLocation != null) {
            float[] distance = new float[1];
            for (int i = 0; i < getHalfUSRoutePoints().size() - 1; i++) {
                Location.distanceBetween(
                        getHalfUSRoutePoints().get(i).getLatitude(), getHalfUSRoutePoints().get(i).getLongitude(),
                        getHalfUSRoutePoints().get(i + 1).getLatitude(), getHalfUSRoutePoints().get(i + 1).getLongitude(),
                        distance
                );
                totalDistance += distance[0];
            }

            // Convert totalDistance from meters to kilometers
            totalDistanceInKm = totalDistance / 1000.0f;

            // Update the UI or perform actions with the total distance in kilometers
            String distanceText = String.format("%.2f kilometers", totalDistanceInKm);

        }

        lastLocation = newLocation;
        return totalDistanceInKm;
    }



    public void addPolyline() {

        Polyline polyline = new Polyline();
        polyline.setPoints(hikeRoute);
        polyline.getOutlinePaint().set(red);
        polyline.isVisible();

        mMap.getOverlayManager().add(polyline);
        mMap.invalidate();
    }

    private List<GeoPoint> getHalfUSRoutePoints() {
        List<GeoPoint> routePoints = new ArrayList<>();
        // Your implementation for obtaining route points
        return routePoints;
    }


}
