package com.example.stepappv4.ui.History;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.stepappv4.DataModel;
import com.example.stepappv4.R;
import com.example.stepappv4.databinding.FragmentHistoryBinding;
import com.example.stepappv4.ui.CustomAdapter;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;



public class HistoryFragment extends Fragment {

    private ArrayList<DataModel> dataModels;
    private ListView listView;
    private CustomAdapter adapter;

    private FragmentHistoryBinding binding;

    private TextView textViewMonth;
    private ImageButton btnPrevMonth;
    private ImageButton btnNextMonth;
    private int currentMonthIndex = 0;  // Track the current month index
    Calendar calendar = Calendar.getInstance();
    private int currentYear = calendar.get(Calendar.YEAR);


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize TextView and Buttons
        textViewMonth = root.findViewById(R.id.textView5);
        btnPrevMonth = root.findViewById(R.id.btnPrevMonth);
        btnNextMonth = root.findViewById(R.id.btnNextMonth);

        // Get the current month
        Calendar calendar = Calendar.getInstance();
        currentMonthIndex = calendar.get(Calendar.MONTH);

        // Set initial month
        updateMonth();

        // Add your ListView initialization and population logic here

        // Set click listeners for navigation buttons
        btnPrevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPreviousMonth();
            }
        });

        btnNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNextMonth();
            }
        });
        listView = root.findViewById(R.id.list_view_history);
        dataModels = new ArrayList<>();

        // List Entries
        dataModels.add(new DataModel("My Hike", "22.11.2023", "min", "230"));
        dataModels.add(new DataModel("My Hike2", "23.11.2023", "min", "230"));


        adapter = new CustomAdapter(dataModels, requireContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataModel dataModel = dataModels.get(position);

                Snackbar.make(view, dataModel.getName() + "\n"  + " Duration: " + dataModel.getFeature() + " " + dataModel.getVersion_number(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateMonth() {
        // Get the current month and year based on currentMonthIndex
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, currentMonthIndex);
        String currentMonth = new DateFormatSymbols(Locale.getDefault()).getMonths()[currentMonthIndex];

        // Update the TextView with the current month and year
        textViewMonth.setText(String.format(Locale.getDefault(), "%s %d", currentMonth, currentYear));
    }

    private void navigateToPreviousMonth() {
        // Update currentMonthIndex and refresh the UI
        currentMonthIndex = (currentMonthIndex - 1 + 12) % 12;  // Ensure the index wraps around

        // Check if we need to decrement the year as well
        if (currentMonthIndex == 11) {  // If moving from January to December
            currentYear--;
        }

        updateMonth();
        // Add logic to update the ListView based on the new month if needed
    }

    private void navigateToNextMonth() {
        // Update currentMonthIndex and refresh the UI
        currentMonthIndex = (currentMonthIndex + 1) % 12;  // Ensure the index wraps around

        // Check if we need to increment the year as well
        if (currentMonthIndex == 0) {  // If moving from December to January
            currentYear++;
        }

        updateMonth();
        // Add logic to update the ListView based on the new month if needed
    }
}