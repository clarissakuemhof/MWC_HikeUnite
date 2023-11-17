package com.example.stepappv4.ui.Day;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.ColorLong;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.Calendar;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.stepappv4.StepAppOpenHelper;
import com.example.stepappv4.databinding.FragmentDayBinding;
import com.example.stepappv4.R;




public class DayFragment extends Fragment {

    AnyChartView anyChartView;

    Date cDate = new Date();
    String current_time = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

    private FragmentDayBinding binding;
    public Map<String, Integer> stepsByDay = null;

    public int todaySteps = 0;
    TextView numStepsTextView;

    public Map<Integer, Integer> stepsByHour = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Create column chart
        anyChartView = root.findViewById(R.id.dayBarChart);
        anyChartView.setProgressBar(root.findViewById(R.id.loadingBar2));

        Cartesian cartesian = createColumnChart();
        anyChartView.setBackgroundColor("#00000000");
        anyChartView.setChart(cartesian);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public Cartesian createColumnChart() {
        // Retrieve steps by date for the last week
        Map<String, Integer> stepsByDate = StepAppOpenHelper.loadStepsByDateForLastWeek(getContext());

        // Create a list to hold data entries for the chart
        List<DataEntry> data = new ArrayList<>();

        // Populate the data list with steps by date
        for (Map.Entry<String, Integer> entry : stepsByDate.entrySet()) {
            data.add(new ValueDataEntry(entry.getKey(), entry.getValue()));
        }

        // Create a column chart
        Cartesian cartesian = AnyChart.column();

        // Add the data to the column chart
        Column column = cartesian.column(data);

        // Customize chart properties
        column.fill("#1EB980");
        column.stroke("#1EB980");
        cartesian.animation(true);

        // Set chart title and axes
        cartesian.xAxis(0).title("Day");
        cartesian.yAxis(0).title("Number of Steps");

        column.tooltip()
                .titleFormat("At day: {%X}")
                .format("{%Value} Steps")
                .anchor(Anchor.RIGHT_BOTTOM);

        return cartesian;
    }



}
