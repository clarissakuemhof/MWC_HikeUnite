package com.example.stepappv4.ui.Achievements;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stepappv4.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

public class GridAdapter extends BaseAdapter {

    private Context mContext;
    private List<Archievement> mData;

    public GridAdapter(Context context, List<Archievement> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.archievements_gridview_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.gridItemImage);
        TextView goalTV = convertView.findViewById(R.id.gridItemText);
        TextView progressTV = convertView.findViewById(R.id.gridItemText1);
        LinearProgressIndicator progressIndicator = convertView.findViewById(R.id.achieveProgress3);

        Archievement item = mData.get(position);

        if (!item.isReachedMax()){
            imageView.setImageResource(R.drawable.baseline_stars_24);
        }else {
            switch (item.getLevel()){
                case 1:
                    imageView.setImageResource(R.drawable.baseline_stars_24_achieved_bronze);
                    Log.d("DEBUG", "Level: " + item.getLevel() + " Bronze");
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.baseline_stars_24_achieved_silver);
                    Log.d("DEBUG", "Level: " + item.getLevel() + " Silver");
                    break;
                case 3:
                    imageView.setImageResource(R.drawable.baseline_stars_24_achieved);
                    Log.d("DEBUG", "Level: " + item.getLevel() + " Gold");
                    break;
            }
        }

        goalTV.setText(item.getGoal());
        progressTV.setText(item.getYourProgress());
        progressIndicator.setMax(item.getProgressBarMax());
        progressIndicator.setProgress(item.getProgress());

        return convertView;
    }
}