package com.example.stepappv4.ui.Achievements;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.stepappv4.R;
import com.example.stepappv4.databinding.FragmentAchievementsBinding;
import com.example.stepappv4.databinding.FragmentDetailsBinding;
import com.google.android.material.progressindicator.LinearProgressIndicator;


public class AchievementsDetailsFragment extends Fragment {

    private LinearProgressIndicator progressBar;
    ImageView icon;

    private FragmentDetailsBinding binding;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();




        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}