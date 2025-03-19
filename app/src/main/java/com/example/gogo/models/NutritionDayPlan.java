package com.example.gogo.models;

import java.util.List;

public class NutritionDayPlan {
        private String day;
        private List<String> breakfast;
        private List<String> lunch;
        private List<String> dinner;
        private boolean completed;

    public NutritionDayPlan(String day, boolean completed, List<String> dinner, List<String> lunch, List<String> breakfast) {
        this.day = day;
        this.completed = completed;
        this.dinner = dinner;
        this.lunch = lunch;
        this.breakfast = breakfast;
    }


    public NutritionDayPlan() {
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<String> getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(List<String> breakfast) {
        this.breakfast = breakfast;
    }

    public List<String> getLunch() {
        return lunch;
    }

    public void setLunch(List<String> lunch) {
        this.lunch = lunch;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public List<String> getDinner() {
        return dinner;
    }

    public void setDinner(List<String> dinner) {
        this.dinner = dinner;
    }
}
