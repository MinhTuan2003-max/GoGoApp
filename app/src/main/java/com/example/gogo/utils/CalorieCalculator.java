package com.example.gogo.utils;

import com.example.gogo.models.Exercise;

public class CalorieCalculator {
    public static int calculateCaloriesBurned(Exercise exercise, int duration) {
        double energyPerMinute = exercise.getEnergyConsumed() / exercise.getStandardDuration();
        return (int) (energyPerMinute * duration);
    }
}