package com.example.stepappv4.ui.HelperClass;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.util.Log;

import com.example.stepappv4.R;


import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.List;

/**
 * This is the class to handle all methods needed to initialize the maps and to draw the route based on the saved GeoPoints
 * based on:
 * <a href="https://medium.com/@mr.appbuilder/how-to-integrate-and-work-with-open-street-map-osm-in-an-android-app-kotlin-564b38590bfe">Open Street Map Tutorial</a>
 * <a href="https://osmdroid.github.io/osmdroid/javadocAll/org/osmdroid/views/overlay/PolyOverlayWithIW.html#getOutlinePaint--">Documentation for Route Overlay</a>
 */
public class OpenStreetMapsHelper implements MapListener {

    private MapView mMap;
    private IMapController controller;
    private float totalDistanceInKm = 0.0f;
    private float totalDistance = 0.0f;
    private List<GeoPoint> hikeRoute;
    private Paint red = new Paint();

    /**
     * Constructor for map helper with map view. Used to display route and map in hike reports
     * @param context context of fragment/activity
     * @param mapView map view to show map & route
     * @param hikeRoute hike route consisting of list of geo points representing the hike
     */
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

    }

    /**
     * Constructor method for OpenStreetMapsHelper when no map representation is provided
     * Can be used to access distance of hikes
     * @param context context of fragment/activity
     * @param hikeRoute hike route consisting of list of geo points representing the hike
     */
    public OpenStreetMapsHelper(Context context, List<GeoPoint> hikeRoute) {
        Configuration.getInstance().load(
                context,
                context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
        );

        this.hikeRoute = hikeRoute;
        calculateDistance();

    }

    /**
     * Method to initialize ma and add boundingbox around the hike route
     * Zooms in to boundingbox and centers it
     */
    public void initMap() {
        Configuration.getInstance().load(
                mMap.getContext().getApplicationContext(),
                mMap.getContext().getSharedPreferences("MapApp", mMap.getContext().MODE_PRIVATE)
        );

        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.setMultiTouchControls(true);

        controller = mMap.getController();
        BoundingBox boundingBox = getBoundingBox(hikeRoute);

        GeoPoint centerPoint = new GeoPoint(boundingBox.getCenterLatitude(),boundingBox.getCenterLongitude());

        controller.setCenter(centerPoint);
        controller.animateTo(centerPoint);

        controller.setZoom(11.0);

    }


    @Override
    public boolean onScroll(ScrollEvent event) {
        //Log.e("TAG", "onCreate:la " + event.getSource().getMapCenter().getLatitude());
        //Log.e("TAG", "onCreate:lo " + event.getSource().getMapCenter().getLongitude());
        return true;
    }

    @Override
    public boolean onZoom(ZoomEvent event) {
        Log.e("TAG", "onZoom zoom level: " + event.getZoomLevel() + "   source:  " + event.getSource());
        return false;
    }


    /**
     * This function is used to calculate between the geopoints and returns it in kilometers
     * It sums up the distances between the GeoPoints in the hikeRoute
     */
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

    /**
     * This method adds the Polyline overlay to the map to visualize the hike
     */
    public void addPolyline(List<GeoPoint> hikeRoute) {
        if (hikeRoute != null && hikeRoute.size() > 1) {
            //for (GeoPoint geoPoint : hikeRoute) {
            //    Log.d("GeoPoint", "Latitude: " + geoPoint.getLatitude() + ", Longitude: " + geoPoint.getLongitude() + ", Altitude: " + geoPoint.getAltitude());
            //}


            Polyline polyline = new Polyline();
            polyline.setPoints(hikeRoute);
            polyline.setGeodesic(true); // Add this line to ensure a great circle path
            red.setColor(Color.RED);
            polyline.getOutlinePaint().set(red);
            polyline.getOutlinePaint().setStrokeCap(Paint.Cap.ROUND);
            mMap.getOverlayManager().add(polyline);
            Log.d("TEST", "Polyline added...");
            mMap.invalidate();
        } else {
            Log.d("TEST", "Invalid hikeRoute for Polyline");
        }
    }

    /**
     * Getter for totalDistanceInKm
     * @return totalDistanceInKm
     */
    public float getTotalDistanceInKm() {
        return totalDistanceInKm;
    }

    /**
     * Creates BoundingBox with min and max values for Latitudes and Longitudes in hike route
     * @param points List with GeoPoints during the hike
     * @return BoundingBox
     */
    private BoundingBox getBoundingBox(List<GeoPoint> points) {
        double minLat = Double.MAX_VALUE;
        double maxLat = Double.MIN_VALUE;
        double minLon = Double.MAX_VALUE;
        double maxLon = Double.MIN_VALUE;

        for (GeoPoint point : points) {
            double latitude = point.getLatitude();
            double longitude = point.getLongitude();

            minLat = Math.min(minLat, latitude);
            maxLat = Math.max(maxLat, latitude);
            minLon = Math.min(minLon, longitude);
            maxLon = Math.max(maxLon, longitude);
        }

        return new BoundingBox(maxLat, maxLon, minLat, minLon);
    }
}





