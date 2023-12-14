package com.example.stepappv4.ui.Achievements;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

        //binding = FragmentDetailsBinding.inflate(inflater, container, false);
        //View root = binding.getRoot();

        View view = inflater.inflate(R.layout.fragment_details, container, false);

        ImageView detailsImageView = view.findViewById(R.id.detailsImageView);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView detailsTextView = view.findViewById(R.id.detailsTextView);

        Bundle arguments = getArguments();
        if (arguments != null) {
            int badgeNumber = arguments.getInt("badgeNumber", 1);

            // Dynamically set details based on badge number
            //int badgeImageResource = getBadgeImageResource(badgeNumber);
            //detailsImageView.setImageResource(badgeImageResource);

            String badgeTitle = getBadgeTitle(badgeNumber);
            titleTextView.setText(badgeTitle);

            String badgeDetails = getBadgeDetails(badgeNumber);
            detailsTextView.setText(badgeDetails);
        }
        return view;
    }

    private String getBadgeTitle(int badgeNumber) {
        switch (badgeNumber) {
            case 1:
                return "100 Hikes";
            case 2:
                return "100 Kilometers";
            case 3:
                return "5000 Height meters";
            case 4:
                return "10 Lakes";
            case 5:
                return "5000 Height meters";
            case 6:
                return "100 Kilometers";
            case 7:
                return "100 Hikes";
            case 8:
                return "5000 Height meters";
            case 9:
                return "100 Kilometers";
            default:
                return "Default Title";
        }
    }

    private String getBadgeDetails(int badgeNumber) {
        switch (badgeNumber) {
            case 1:
                return "You already achieved this challenge! Congratulations!";
            case 2:
                return "You already achieved this challenge! Congratulations!";
            case 3:
                return "You receive this achievement when " +
                        "you reached 5000 height meters in total. " +
                        "So far you reached 342 height meters.";
            case 4:
                return "You receive this achievement when " +
                        "you have visited a total of ten lakes on your " +
                        "hikes. So far you have done it 3 times.";
            case 5:
                return "You receive this achievement when " +
                        "you reached 5000 height meters in total. " +
                        "So far you reached 4565 height meters.";
            case 6:
                return "You receive this achievement when " +
                        "you hiked 100 kilometers in total. " +
                        "So far you reached 32 kilometers.";
            case 7:
                return "You receive this achievement when " +
                        "you did 100 hikes and recorded it with the HikeUnite App. " +
                        "So far you hiked 89 times.";
            case 8:
                return "You receive this achievement when " +
                        "you reached 5000 height meters in total. " +
                        "So far you reached 2756 height meters.";
            case 9:
                return "You receive this achievement when " +
                        "you hiked 100 kilometers in total. " +
                        "So far you reached 67 kilometers.";
            default:
                return "Default details";
        }
    }

    /*
    private int getBadgeImageResource(int badgeNumber) {
        // Implement logic to get the image resource for the badge
        // You can use a switch statement, if-else, or any other logic
        // Return the resource ID for the corresponding badge image
        // For example:
        switch (badgeNumber) {
            case 1:
                return R.drawable.badge1_image;
            case 2:
                return R.drawable.badge2_image;
            // Add cases for other badge numbers
            default:
                return R.drawable.default_image;
        }
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}