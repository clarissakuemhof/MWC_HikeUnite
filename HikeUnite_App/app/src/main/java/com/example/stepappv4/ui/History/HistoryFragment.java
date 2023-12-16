package com.example.stepappv4.ui.History;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.example.stepappv4.R;
import com.example.stepappv4.StepAppOpenHelper;
import com.example.stepappv4.databinding.FragmentHistoryBinding;
import com.example.stepappv4.ui.HelperClass.OpenStreetMapsHelper;


public class HistoryFragment extends Fragment {

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
        textViewMonth = root.findViewById(R.id.yourhikeheadline);
        btnPrevMonth = root.findViewById(R.id.btnPrevMonth);
        btnNextMonth = root.findViewById(R.id.btnNextMonth);
        myDatabaseHelper = new StepAppOpenHelper(this.getContext());



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
        createCursor();

        listView.setAdapter(adapter);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the cursor at the clicked position
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                int hikeId;
                int test = position;
                Log.d("TEST", "POSITION: " + test );

                int idColumnIndex = cursor.getColumnIndex("_id");
                if (idColumnIndex != -1) {
                    hikeId = cursor.getInt(idColumnIndex);

                    // Create a bundle and navigate only if hikeId is valid
                    if (hikeId >= 0) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("hikeId", hikeId);

                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.action_nav_hist_to_nav_gallery, bundle);
                    } else {
                        // Handle the case where the ID is invalid
                        Log.e("HistoryFragment", "Invalid hikeId: " + hikeId);
                        Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where the _id column is not found in the cursor
                    Log.e("HistoryFragment", "_id column not found in the cursor");
                    Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show();
                }
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

        // Log the contents of the cursor
        DatabaseUtils.dumpCursor(cursor);

        // Define the columns from which to fetch data
        String[] columns = {
                StepAppOpenHelper.KEY_NAME,
                //StepAppOpenHelper.KEY_DAY,
                //StepAppOpenHelper.KEY_DISTANCE
        };

        int[] to = {
                R.id.name,
                //R.id.version_number,
                //R.id.type
        };

        adapter = new SimpleCursorAdapter(
                requireContext(),
                R.layout.list_view_item,
                cursor,
                columns,
                to,
                0
        ) {
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                super.bindView(view, context, cursor);

                int hikeIdIndex = cursor.getColumnIndex("_id");
                int hikeId = (hikeIdIndex != -1) ? cursor.getInt(hikeIdIndex) : -1;

                OpenStreetMapsHelper mapsHelper = new OpenStreetMapsHelper(getContext(), myDatabaseHelper.getGeoPointsById(hikeId));

                Float distance = BigDecimal.valueOf(mapsHelper.getTotalDistanceInKm())
                        .setScale(2, RoundingMode.HALF_DOWN)
                        .floatValue();
                String distanceS = String.valueOf(distance) + " km";
                String duration = (hikeId != -1) ? myDatabaseHelper.calculateDuration(hikeId) + " hrs" : "N/A";

                TextView textViewDuration = view.findViewById(R.id.durationTextView);
                TextView textViewDistance = view.findViewById(R.id.distanceTextView);
                if (textViewDuration != null) {
                    textViewDistance.setText(distanceS);
                } else {
                    Log.e("bindView", "TextView for distance not found in the layout");
                }
                if (textViewDuration != null) {
                    textViewDuration.setText(duration);
                } else {
                    Log.e("bindView", "TextView for duration not found in the layout");
                }
            }
        };
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