package com.example.gogo.models;

public class Exercise {
    private int exerciseID;
    private String exerciseName;
    private String category;
    private String description;
    private double energyConsumed;
    private String energyUnit;
    private int standardDuration;
    private String difficultyLevel;
    private String equipmentRequired;
    private String createdAt;

    public Exercise(int exerciseID, String exerciseName, String category, String description,
                    double energyConsumed, String energyUnit, int standardDuration,
                    String difficultyLevel, String equipmentRequired, String createdAt) {
        this.exerciseID = exerciseID;
        this.exerciseName = exerciseName;
        this.category = category;
        this.description = description;
        this.energyConsumed = energyConsumed;
        this.energyUnit = energyUnit;
        this.standardDuration = standardDuration;
        this.difficultyLevel = difficultyLevel;
        this.equipmentRequired = equipmentRequired;
        this.createdAt = createdAt;
    }

    // Getters
    public int getExerciseID() { return exerciseID; }
    public String getExerciseName() { return exerciseName; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public double getEnergyConsumed() { return energyConsumed; }
    public String getEnergyUnit() { return energyUnit; }
    public int getStandardDuration() { return standardDuration; }
    public String getDifficultyLevel() { return difficultyLevel; }
    public String getEquipmentRequired() { return equipmentRequired; }
    public String getCreatedAt() { return createdAt; }
}