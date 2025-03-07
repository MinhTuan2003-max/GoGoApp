package com.example.gogo.models;

public class SleepRecord {
    private int id;
    private String date;        // Định dạng: "dd/MM/yyyy"
    private String sleepTime;   // Định dạng: "HH:mm"
    private String wakeUpTime;  // Định dạng: "HH:mm"
    private float sleepHours;   // Số giờ ngủ

    public SleepRecord(int id, String date, String sleepTime, String wakeUpTime, float sleepHours) {
        this.id = id;
        this.date = date;
        this.sleepTime = sleepTime;
        this.wakeUpTime = wakeUpTime;
        this.sleepHours = sleepHours;
    }

    // Getters và Setters
    public int getId() { return id; }
    public String getDate() { return date; }
    public String getSleepTime() { return sleepTime; }
    public String getWakeUpTime() { return wakeUpTime; }
    public float getSleepHours() { return sleepHours; }
}