package com.example.stepappv4.ui.History;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.example.stepappv4.DataModel;
import com.example.stepappv4.R;
import com.example.stepappv4.StepAppOpenHelper;
import com.example.stepappv4.databinding.FragmentHistoryBinding;

import java.util.ArrayList;



public class HistoryFragment extends Fragment {

    private ArrayList<DataModel> dataModels;
    private ListView listView;
    private SimpleCursorAdapter adapter;

    private FragmentHistoryBinding binding;

    private TextView textViewMonth;
    private ImageButton btnPrevMonth;
    private ImageButton btnNextMonth;
    private int currentMonthIndex = 0;  // Track the current month index
    Calendar calendar = Calendar.getInstance();
    private int currentYear = calendar.get(Calendar.YEAR);

    private StepAppOpenHelper myDatabaseHelper;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize TextView and Buttons
        textViewMonth = root.findViewById(R.id.textView5);
        btnPrevMonth = root.findViewById(R.id.btnPrevMonth);
        btnNextMonth = root.findViewById(R.id.btnNextMonth);
        myDatabaseHelper = new StepAppOpenHelper(this.getContext());



        // Get the current month
        Calendar calendar = Calendar.getInstance();
        currentMonthIndex = calendar.get(Calendar.MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);


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
        createCursor();

        listView.setAdapter(adapter);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                // Pass any necessary data to the ReportFragment using the bundle
                bundle.putString("itemName", dataModels.get(position).getName());
                bundle.putString("itemDate", dataModels.get(position).getDate());
                bundle.putString("itemDuration", dataModels.get(position).getDuration());

                navController.navigate(R.id.action_nav_hist_to_nav_gallery, bundle);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createCursor() {
        // Create a cursor to fetch data from the database
        Cursor cursor = myDatabaseHelper.getHikesForMonth(currentMonthIndex, currentYear);

        // Define the columns from which to fetch data
        String[] columns = {
                StepAppOpenHelper.KEY_NAME, // Alias "id" column as "_id"
                StepAppOpenHelper.KEY_DAY,
                StepAppOpenHelper.KEY_DISTANCE
        };

        // Define the views where data should be placed
        int[] to = {
                R.id.name,
                R.id.version_number,
                R.id.type
        };

        adapter = new SimpleCursorAdapter(
                requireContext(),
                R.layout.list_view_item, // your custom layout for each item
                cursor,
                columns,
                to,
                0
        );
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

        // Refresh the cursor data for the new month
        adapter.swapCursor(myDatabaseHelper.getHikesForMonth(currentMonthIndex, currentYear));

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

        // Refresh the cursor data for the new month
        adapter.swapCursor(myDatabaseHelper.getHikesForMonth(currentMonthIndex, currentYear));

        // Add logic to update the ListView based on the new month if needed
    }

}