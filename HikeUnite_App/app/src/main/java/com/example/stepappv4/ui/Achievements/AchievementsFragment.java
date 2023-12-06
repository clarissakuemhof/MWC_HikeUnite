package com.example.stepappv4.ui.Achievements;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.app.Dialog;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.stepappv4.R;
import com.example.stepappv4.databinding.FragmentAchievementsBinding;
import com.google.android.material.progressindicator.LinearProgressIndicator;


public class AchievementsFragment extends Fragment {

    private LinearProgressIndicator progressBar;
    ImageView icon;

    private FragmentAchievementsBinding binding;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);

        binding = FragmentAchievementsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        progressBar = (LinearProgressIndicator) root.findViewById(R.id.achieveProgress);
        progressBar.setMax(5000);
        progressBar.setProgress(342);

        progressBar = (LinearProgressIndicator) root.findViewById(R.id.achieveProgress2);
        progressBar.setMax(10);
        progressBar.setProgress(3);

        progressBar = (LinearProgressIndicator) root.findViewById(R.id.achieveProgress3);
        progressBar.setMax(100);
        progressBar.setProgress(32);

        progressBar = (LinearProgressIndicator) root.findViewById(R.id.achieveProgress4);
        progressBar.setMax(5000);
        progressBar.setProgress(2756);

        progressBar = (LinearProgressIndicator) root.findViewById(R.id.achieveProgress5);
        progressBar.setMax(100);
        progressBar.setProgress(89);

        progressBar = (LinearProgressIndicator) root.findViewById(R.id.achieveProgress6);
        progressBar.setMax(100);
        progressBar.setProgress(67);

        progressBar = (LinearProgressIndicator) root.findViewById(R.id.achieveProgress7);
        progressBar.setMax(5000);
        progressBar.setProgress(4565);

        icon = root.findViewById(R.id.challenge1);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("badgeNumber", 1);
                navController.navigate(R.id.action_nav_achievements_to_nav_details, bundle);
            }
        });

        icon = root.findViewById(R.id.challenge4);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("badgeNumber", 4);
                navController.navigate(R.id.action_nav_achievements_to_nav_details, bundle);
            }
        });

        icon = root.findViewById(R.id.challenge3);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("badgeNumber", 3);
                navController.navigate(R.id.action_nav_achievements_to_nav_details, bundle);
            }
        });

        icon = root.findViewById(R.id.challenge2);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("badgeNumber", 2);
                navController.navigate(R.id.action_nav_achievements_to_nav_details, bundle);
            }
        });

        icon = root.findViewById(R.id.challenge6);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("badgeNumber", 6);
                navController.navigate(R.id.action_nav_achievements_to_nav_details, bundle);
            }
        });

        icon = root.findViewById(R.id.challenge8);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("badgeNumber", 8);
                navController.navigate(R.id.action_nav_achievements_to_nav_details, bundle);
            }
        });

        icon = root.findViewById(R.id.challenge9);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("badgeNumber", 9);
                navController.navigate(R.id.action_nav_achievements_to_nav_details, bundle);
            }
        });
        icon = root.findViewById(R.id.challenge7);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("badgeNumber", 7);
                navController.navigate(R.id.action_nav_achievements_to_nav_details, bundle);
            }
        });

        icon = root.findViewById(R.id.challenge5);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("badgeNumber", 5);
                navController.navigate(R.id.action_nav_achievements_to_nav_details, bundle);
            }
        });

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}