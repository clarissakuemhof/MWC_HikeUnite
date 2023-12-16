package com.example.stepappv4.ui.Achievements;

import android.media.Image;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

public class Archievement {

    private int progressBarMax;
    private int progress;
    private boolean reachedMax;
    private String goal;
    private String yourProgress;
    private int level;


    public Archievement(int progressBarMax, int progress, String goal, int level){
        this.progress = progress;
        this.progressBarMax = progressBarMax;
        this.goal = goal;
        this.reachedMax = false;
        this.level = level;
        this.yourProgress = progress + " out of " + progressBarMax;
        checkProgress();
    }



    public int getProgressBarMax() {
        return progressBarMax;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isReachedMax() {
        return reachedMax;
    }

    public String getGoal() {
        return goal;
    }

    public void setReachedMax(boolean reachedMax) {
        this.reachedMax = reachedMax;
    }

    public String getYourProgress() {
        return yourProgress;
    }

    private void checkProgress(){
        if (progress >= progressBarMax){
            setReachedMax(true);
        }
    }

    public int getLevel() {
        Log.d("DEBUG", "Level: " + level);
        return level;
    }



}
