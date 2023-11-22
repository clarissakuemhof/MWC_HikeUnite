package com.example.stepappv4;

public class DataModel {

    String name;
    String date;
    String duration;

    public DataModel(String name, String date, String duration ) {
        this.name=name;
        this.date=date;
        this.duration=duration;

    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }


    public String getDuration() {
        return duration;
    }

}