package com.example.gogo.models;

public class ExerciseCompletion {
    private int completionID;
    private int planID;
    private int userID;
    private int exerciseID;
    private int caloriesBurned;
    private String dateCompleted;
    private int duration;

    public ExerciseCompletion(int completionID, int planID, int userID, int exerciseID, int caloriesBurned, String dateCompleted, int duration) {
        this.completionID = completionID;
        this.planID = planID;
        this.userID = userID;
        this.exerciseID = exerciseID;
        this.caloriesBurned = caloriesBurned;
        this.dateCompleted = dateCompleted;
        this.duration = duration;
    }

    public int getCompletionID() {
        return completionID;
    }

    public void setCompletionID(int completionID) {
        this.completionID = completionID;
    }

    public int getPlanID() {
        return planID;
    }

    public void setPlanID(int planID) {
        this.planID = planID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getExerciseID() {
        return exerciseID;
    }

    public void setExerciseID(int exerciseID) {
        this.exerciseID = exerciseID;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public String getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(String dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}