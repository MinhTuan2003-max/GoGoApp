package com.example.gogo.models;

import java.util.List;

public class Food {
    private int foodId;
    private String name;
    private List<Nutrition> nutritionList;

    public Food() {
    }

    public Food(int foodId, String name, List<Nutrition> nutritionList) {
        this.foodId = foodId;
        this.name = name;
        this.nutritionList = nutritionList;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Nutrition> getNutritionList() {
        return nutritionList;
    }

    public void setNutritionList(List<Nutrition> nutritionList) {
        this.nutritionList = nutritionList;
    }
}
