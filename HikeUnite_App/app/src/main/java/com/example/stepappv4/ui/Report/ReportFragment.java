package com.example.stepappv4.ui.Report;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.anychart.enums.Anchor;

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
import com.example.stepappv4.ui.GPS.OpenStreetMapsHelper;

import org.osmdroid.views.MapView;

import java.util.List;

public class ReportFragment extends Fragment {

    private int hikeId;
    private StepAppOpenHelper myDatabaseHelper;
    private AnyChartView anyChartView;
    private MapView mMap;
    private OpenStreetMapsHelper mapHelper;
    private LinearLayout chartLayout;
    private boolean showMap = true; // Flag to track whether to show the map or altitude chart

    private FragmentReportBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mMap = binding.osmmap;
        anyChartView = root.findViewById(R.id.anyChartView);
        chartLayout = root.findViewById(R.id.mapContainer); // Add this line


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

            // Initially show the map
            showMap();
        }

        return root;
    }

    private void toggleView() {
        if (showMap) {
            // If currently showing the map, switch to the altitude chart
            showAltitudeChart();
        } else {
            // If currently showing the altitude chart, switch to the map
            showMap();
        }
    }

    private void showMap() {
        anyChartView.setVisibility(View.GONE);
        mMap.setVisibility(View.VISIBLE);

        mapHelper = new OpenStreetMapsHelper(this.getContext(), mMap, myDatabaseHelper.getGeoPointsById(hikeId));
        mapHelper.initMap();
        mapHelper.addPolyline();

        showMap = true;
    }

    private void showAltitudeChart() {
        mMap.setVisibility(View.GONE);
        chartLayout.setVisibility(View.VISIBLE);
        anyChartView.setVisibility(View.VISIBLE);

        List<Double> altitudeData = myDatabaseHelper.getAltitudesById(hikeId);

        Cartesian cartesian = createColumnChart(altitudeData);
        anyChartView.setChart(cartesian);

        showMap = false;
    }

    private Cartesian createColumnChart(List<Double> altitudeData) {
        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new java.util.ArrayList<>();
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
        cartesian.yScale().minimum(0);

        cartesian.yAxis(0).title("Altitude");
        cartesian.xAxis(0).title("Point");
        cartesian.background().fill("#00000000");
        cartesian.animation(true);

        return cartesian;
    }
}