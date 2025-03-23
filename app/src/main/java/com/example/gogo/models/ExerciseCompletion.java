package com.example.gogo.models;

public class ExerciseCompletion {
    private int completionID;
    private int planID;
    private int userID;
    private int exerciseID;
    private int caloriesBurned;
    private String completedAt;

    public ExerciseCompletion(int completionID, int planID, int userID, int exerciseID, int caloriesBurned, String completedAt) {
        this.completionID = completionID;
        this.planID = planID;
        this.userID = userID;
        this.exerciseID = exerciseID;
        this.caloriesBurned = caloriesBurned;
        this.completedAt = completedAt;
    }

    // Getters
    public int getCompletionID() { return completionID; }
    public int getPlanID() { return planID; }
    public int getUserID() { return userID; }
    public int getExerciseID() { return exerciseID; }
    public int getCaloriesBurned() { return caloriesBurned; }
    public String getCompletedAt() { return completedAt; }
}