package com.example.stepappv4.ui.Achievements;

import android.util.Log;

/**
 * Class to create achievements
 * no sources needed because its just a simple class and i know how to code this
 */
public class Archievement {

    private int progressBarMax;
    private int progress;
    private boolean reachedMax;
    private String goal;
    private String yourProgress;
    private int level;

    /**
     * Constructor for achievement
     *
     * @param progressBarMax Maximum value for progressbar
     * @param progress progress in the progress bar
     * @param goal String with goal of achievement
     * @param level level of achievement (bronze, silver, gold)
     */
    public Archievement(int progressBarMax, int progress, String goal, int level){
        this.progress = progress;
        this.progressBarMax = progressBarMax;
        this.goal = goal;
        this.reachedMax = false;
        this.level = level;
        this.yourProgress = progress + " out of " + progressBarMax;
        checkProgress();
    }



    /**
     * Gets the maximum value for the progress bar.
     *
     * @return The maximum value for the progress bar.
     */
    public int getProgressBarMax() {
        return progressBarMax;
    }

    /**
     * Gets the current progress value.
     *
     * @return The current progress value.
     */
    public int getProgress() {
        return progress;
    }

    /**
     * Checks if the progress has reached the maximum value.
     *
     * @return True if the progress has reached the maximum value, false otherwise.
     */
    public boolean isReachedMax() {
        return reachedMax;
    }

    /**
     * Gets the goal associated with the progress.
     *
     * @return The goal associated with the progress.
     */
    public String getGoal() {
        return goal;
    }

    /**
     * Sets the flag indicating whether the progress has reached the maximum value.
     *
     * @param reachedMax True if the progress has reached the maximum value, false otherwise.
     */
    public void setReachedMax(boolean reachedMax) {
        this.reachedMax = reachedMax;
    }

    /**
     * Gets a string representation of your progress.
     *
     * @return A string representation of your progress.
     */
    public String getYourProgress() {
        return yourProgress;
    }

    /**
     * Checks the progress and sets the flag if it has reached the maximum value.
     */
    private void checkProgress() {
        if (progress >= progressBarMax) {
            setReachedMax(true);
        }
    }

    /**
     * Gets the current level.
     *
     * @return The current level.
     */
    public int getLevel() {
        Log.d("DEBUG", "Level: " + level);
        return level;
    }



}
