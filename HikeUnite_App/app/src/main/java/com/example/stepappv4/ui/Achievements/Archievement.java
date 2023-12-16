package com.example.stepappv4.ui.Achievements;

import com.google.android.material.progressindicator.LinearProgressIndicator;

public class Archievement {
    private LinearProgressIndicator progressBar;
    private int progressBarMax;
    private int progress;
    private boolean reachedMax;

    private String goal;

    public Archievement(LinearProgressIndicator progressBar, int progressBarMax, int progress, String goal){
        this.progressBar = progressBar;
        this.progress = progress;
        this.progressBarMax = progressBarMax;
        this.goal = goal;
        this.reachedMax = false;
    }
}
