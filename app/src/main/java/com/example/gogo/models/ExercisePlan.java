package com.example.gogo.models;

public class ExercisePlan {
    private int planID;
    private int userID;
    private int exerciseID;
    private int duration;
    private int caloriesBurned;
    private String planName; // Đổi từ planGroup thành planName
    private String createdAt;

    public ExercisePlan(int planID, int userID, int exerciseID, int duration, int caloriesBurned, String planName, String createdAt) {
        this.planID = planID;
        this.userID = userID;
        this.exerciseID = exerciseID;
        this.duration = duration;
        this.caloriesBurned = caloriesBurned;
        this.planName = planName;
        this.createdAt = createdAt;
    }

    // Getters
    public int getPlanID() { return planID; }
    public int getUserID() { return userID; }
    public int getExerciseID() { return exerciseID; }
    public int getDuration() { return duration; }
    public int getCaloriesBurned() { return caloriesBurned; }
    public String getPlanName() { return planName; }
    public String getCreatedAt() { return createdAt; }
}