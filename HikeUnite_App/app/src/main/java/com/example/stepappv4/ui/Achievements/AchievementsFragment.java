package com.example.stepappv4.ui.Achievements;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.stepappv4.R;
import com.example.stepappv4.databinding.FragmentAchievementsBinding;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class AchievementsFragment extends Fragment {

    private LinearProgressIndicator progressBar;

    private FragmentAchievementsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}