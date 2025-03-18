package com.example.gogo.models;

public class DietPlan {
    private int dietId;
    private int userId;
    private String planName;
    private boolean isCompleted;
    private String description;
    private String createdAt;

    public DietPlan(int dietId, int userId, String planName, boolean isCompleted, String description, String createdAt) {
        this.dietId = dietId;
        this.userId = userId;
        this.planName = planName;
        this.isCompleted = isCompleted;
        this.description = description;
        this.createdAt = createdAt;
    }

    public int getDietId() {
        return dietId;
    }

    public int getUserId() {
        return userId;
    }

    public String getPlanName() {
        return planName;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setDietId(int dietId) {
        this.dietId = dietId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}