package com.example.stepappv4.ui.Report;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.stepappv4.StepAppOpenHelper;
import com.example.stepappv4.R;
import com.example.stepappv4.databinding.FragmentReportBinding;
import com.example.stepappv4.ui.HelperClass.OpenStreetMapsHelper;

import org.osmdroid.views.MapView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This fragments shows detailed data about the hikes
 * based on the hike id of the list entry we can retrieve the corresponding data from our database
 */

public class ReportFragment extends Fragment {

    private int hikeId, steps;
    private float distance;
    private String name;
    private StepAppOpenHelper myDatabaseHelper;
    private AnyChartView anyChartView;
    private MapView mMap;
    private OpenStreetMapsHelper mapHelper;
    private LinearLayout chartLayout;
    private TextView stepsTV, distanceTV, nameTV, label1, label2;

    private boolean showMap = true;

    private FragmentReportBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mMap = binding.osmmap;
        anyChartView = root.findViewById(R.id.anyChartView);
        chartLayout = root.findViewById(R.id.mapContainer);
        stepsTV = root.findViewById(R.id.steps);
        distanceTV = root.findViewById(R.id.distanceTest);
        nameTV = root.findViewById(R.id.yourhikeheadline);
        label1 = root.findViewById(R.id.textView18);
        label2 = root.findViewById(R.id.textView19);


        Button switchButton = root.findViewById(R.id.toggleMapButton);

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleView();
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("hikeId")) {
            hikeId = bundle.getInt("hikeId");
            Log.d("ID", "ID: " + hikeId);

            myDatabaseHelper = new StepAppOpenHelper(getContext());
            steps = myDatabaseHelper.getStepsDataById(hikeId);
            name = myDatabaseHelper.getNameDataById(hikeId);
            Log.d("Test", "Steps: " + steps);
            Log.d("TAG",String.format(String.valueOf(myDatabaseHelper.getGeoPointsById(hikeId))));

            stepsTV.setText(String.format(String.valueOf(steps)));
            nameTV.setText(name);

            myDatabaseHelper = new StepAppOpenHelper(getContext());

            showMap();
        }

        return root;
    }

    /**
     * Method to toggle between map and chart. Uses showMap as indicator to decide what to show
     */
    private void toggleView() {
        if (showMap) {
            showAltitudeChart();
        } else {
            showMap();
        }
    }

    /**
     * Sets visibility for map to visible and disables the visibility for the chart
     * Initializes map and Polyline to show Hike
     */
    private void showMap() {
        anyChartView.setVisibility(View.GONE);
        mMap.setVisibility(View.VISIBLE);

        mapHelper = new OpenStreetMapsHelper(this.getContext(), mMap, myDatabaseHelper.getGeoPointsById(hikeId));
        Log.d("TAG",String.format(String.valueOf(myDatabaseHelper.getGeoPointsById(hikeId))));
        mapHelper.initMap();
        mapHelper.addPolyline(myDatabaseHelper.getGeoPointsById(hikeId));
        distance = BigDecimal.valueOf(mapHelper.getTotalDistanceInKm())
                .setScale(2, RoundingMode.HALF_DOWN)
                .floatValue();
        distanceTV.setText(String.format(String.valueOf(distance)) +" km");
        Log.d("CHECKVALUE", "Distance2: " + distance);

        showMap = true;
    }

    /**
     * Sets visibility for chart to visible and disables the visibility for the map
     * Gets data for chart from database
     */
    private void showAltitudeChart() {
        mMap.setVisibility(View.GONE);
        chartLayout.setVisibility(View.VISIBLE);
        anyChartView.setVisibility(View.VISIBLE);

        label1.setText("Altitude Gain");
        label2.setText("Altitudeloss");

        stepsTV.setText(String.valueOf(myDatabaseHelper.getTotalAltitudeGained(hikeId)));
        distanceTV.setText(String.valueOf(myDatabaseHelper.getTotalAltitudeLost(hikeId)));

        List<Double> altitudeData = myDatabaseHelper.getAltitudesById(hikeId);

        Cartesian cartesian = createColumnChart(altitudeData);
        anyChartView.setChart(cartesian);

        showMap = false;
    }

    /**
     * Method to draw the chart based on altitude values during the hike
     * @param altitudeData Points that are saved in a given time interval during the hike
     * @return chart
     */
    private Cartesian createColumnChart(List<Double> altitudeData) {
        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        for (int i = 0; i < altitudeData.size(); i++) {
            data.add(new ValueDataEntry(i, altitudeData.get(i)));
        }

        Column column = cartesian.column(data);

        column.fill("#1EB980");
        column.stroke("#1EB980");

        column.tooltip()
                .position(Position.RIGHT_TOP)
                .offsetX(0d)
                .offsetY(5);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        double maxAltitude = Collections.max(altitudeData) + 5;
        double minAltitude = Collections.min(altitudeData) - 5;
        cartesian.yScale().minimum(minAltitude > 0 ? 0 : minAltitude).maximum(maxAltitude < 0 ? 0 : maxAltitude);

        cartesian.yAxis(0).title("Altitude");
        cartesian.xAxis(0).title("Point");
        cartesian.xAxis(0).labels().enabled(false);
        cartesian.background().fill("#00000000");
        cartesian.animation(true);

        return cartesian;
    }
}