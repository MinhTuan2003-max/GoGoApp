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
    private User user;
    private String planName;
    private boolean isCompleted;
    private String description;
    private String createdAt;

    public DietPlan(int dietId, User user, String planName, boolean isCompleted, String description, String createdAt) {
        this.dietId = dietId;
        this.user = user;
        this.planName = planName;
        this.isCompleted = isCompleted;
        this.description = description;
        this.createdAt = createdAt;
    }

    public int getDietId() {
        return dietId;
    }

    public void setDietId(int dietId) {
        this.dietId = dietId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
