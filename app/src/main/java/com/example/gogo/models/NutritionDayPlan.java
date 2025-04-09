package com.example.gogo.models;

import java.util.List;

public class NutritionDayPlan {
    private int dayNumber;
    private List<String> breakfast;
    private List<String> lunch;
    private List<String> dinner;
    private boolean isCompleted;
    private int totalCalories;

    public NutritionDayPlan(int dayNumber, List<String> breakfast, List<String> lunch, List<String> dinner, boolean isCompleted) {
        this.dayNumber = dayNumber;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.isCompleted = isCompleted;
        this.totalCalories = 0;
    }

    public int getDayNumber() { return dayNumber; }
    public List<String> getBreakfast() { return breakfast; }
    public List<String> getLunch() { return lunch; }
    public List<String> getDinner() { return dinner; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { this.isCompleted = completed; }
    public int getTotalCalories() { return totalCalories; }
    public void addCalories(int calories) { this.totalCalories += calories; }
}