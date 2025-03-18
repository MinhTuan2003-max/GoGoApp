package com.example.gogo.models;

public class Food {
    private int foodId;
    private String foodName;
    private String description;

    public Food(int foodId, String foodName, String description) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.description = description;
    }

    // Getters
    public int getFoodId() { return foodId; }
    public String getFoodName() { return foodName; }
    public String getDescription() { return description; }
}