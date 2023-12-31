package com.example.stepappv4.ui.Achievements;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.stepappv4.R;
import com.example.stepappv4.StepAppOpenHelper;
import com.example.stepappv4.databinding.FragmentAchievementsBinding;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that displays achievements. Simple fragment with a grid view that displays achievements
 */
public class AchievementsFragment extends Fragment {

    private FragmentAchievementsBinding binding;
    private StepAppOpenHelper myDatabaseHelper;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myDatabaseHelper = new StepAppOpenHelper(getContext());

        binding = FragmentAchievementsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        GridView gridView = root.findViewById(R.id.gridView);

        List<Achievement> gridItems = getGridItems();

        GridAdapter gridAdapter = new GridAdapter(getContext(), gridItems);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(), "Move on, nothing to see here", Toast.LENGTH_SHORT).show();

            }
        });

        return root;
    }

    /**
     * This method is used to create our achievements
     * it takes the data from the database to display the progress
     *
     * @return list with possible achievements
     */
    private List<Achievement> getGridItems() {
        List<Achievement> items = new ArrayList<>();
        items.add(new Achievement(10,myDatabaseHelper.getCountOfHikes(), "Do 10 Hikes",1) );
        items.add(new Achievement(25,myDatabaseHelper.getCountOfHikes(), "Do 25 Hikes",2) );
        items.add(new Achievement(50,myDatabaseHelper.getCountOfHikes(), "Do 50 Hikes",3) );
        items.add(new Achievement(25, (int) myDatabaseHelper.getTotalDistance(), "Go 25 km",1));
        items.add(new Achievement(50,(int) myDatabaseHelper.getTotalDistance(), "Go 50 km",2));
        items.add(new Achievement(100,(int) myDatabaseHelper.getTotalDistance(), "Go 100 km",3));
        items.add(new Achievement(50000, (int) myDatabaseHelper.getTotalSteps(), "Go 50k Steps",1));
        items.add(new Achievement(100000,(int) myDatabaseHelper.getTotalSteps(), "Go 100k Steps",2));
        items.add(new Achievement(150000,(int) myDatabaseHelper.getTotalSteps(), "Go 150k Steps",3));
        return items;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}