package com.example.stepappv4.ui.Achievements;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.app.Dialog;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.stepappv4.R;
import com.example.stepappv4.StepAppOpenHelper;
import com.example.stepappv4.databinding.FragmentAchievementsBinding;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;


public class AchievementsFragment extends Fragment {

    private FragmentAchievementsBinding binding;
    private StepAppOpenHelper myDatabaseHelper;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myDatabaseHelper = new StepAppOpenHelper(getContext());

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);

        binding = FragmentAchievementsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        GridView gridView = root.findViewById(R.id.gridView);

        List<Archievement> gridItems = getGridItems();

        GridAdapter gridAdapter = new GridAdapter(getContext(), gridItems);
        gridView.setAdapter(gridAdapter);

        return root;
    }

    private List<Archievement> getGridItems() {
        List<Archievement> items = new ArrayList<>();
        items.add(new Archievement(10,myDatabaseHelper.getCountOfHikes(), "Do 10 Hikes",1) );
        items.add(new Archievement(25,myDatabaseHelper.getCountOfHikes(), "Do 25 Hikes",2) );
        items.add(new Archievement(50,myDatabaseHelper.getCountOfHikes(), "Do 50 Hikes",3) );
        items.add(new Archievement(25, (int) myDatabaseHelper.getTotalDistance(), "Do 10 Hikes",1));
        items.add(new Archievement(50,(int) myDatabaseHelper.getTotalDistance(), "Do 25 Hikes",2));
        items.add(new Archievement(100,(int) myDatabaseHelper.getTotalDistance(), "Do 50 Hikes",3));
        items.add(new Archievement(50000, (int) myDatabaseHelper.getTotalDistance(), "Do 10 Hikes",1));
        items.add(new Archievement(100000,(int) myDatabaseHelper.getTotalDistance(), "Do 25 Hikes",2));
        items.add(new Archievement(150000,(int) myDatabaseHelper.getTotalDistance(), "Do 50 Hikes",3));
        return items;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}