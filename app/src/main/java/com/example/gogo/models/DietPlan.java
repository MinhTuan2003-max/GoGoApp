//package com.example.gogo.models;
//
//public class DietPlan {
//    private int dietId;
//    private int userId;
//    private String planName;
//    private boolean isCompleted;
//    private String description;
//    private String createdAt;
//
//    public DietPlan(int dietId, int userId, String planName, boolean isCompleted, String description, String createdAt) {
//        this.dietId = dietId;
//        this.userId = userId;
//        this.planName = planName;
//        this.isCompleted = isCompleted;
//        this.description = description;
//        this.createdAt = createdAt;
//    }
//
//    public int getDietId() {
//        return dietId;
//    }
//
//    public String getPlanName() {
//        return planName;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//}
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

    public String getPlanName() {
        return planName;
    }

    public String getDescription() {
        return description;
    }
}
