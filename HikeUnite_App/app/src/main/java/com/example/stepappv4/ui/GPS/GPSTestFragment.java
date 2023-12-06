package com.example.stepappv4.ui.GPS;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.stepappv4.R;
import com.example.stepappv4.databinding.FragmentAchievementsBinding;
import com.example.stepappv4.databinding.FragmentDetailsBinding;
import com.example.stepappv4.databinding.FragmentGpstestBinding;
import com.google.android.material.progressindicator.LinearProgressIndicator;


public class GPSTestFragment extends Fragment {


    ImageView icon;

    private FragmentGpstestBinding binding;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);

        binding = FragmentGpstestBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}