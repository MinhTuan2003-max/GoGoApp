package com.example.gogo.utils;

public class HealthUtils {
    // Calculate BMI: weight (kg) / (height (m) * height (m))
    public static float calculateBMI(float heightCm, float weightKg) {
        float heightM = heightCm / 100; // Convert cm to meters
        return weightKg / (heightM * heightM);
    }

    // Calculate BMR using Mifflin-St Jeor Equation
    public static float calculateBMR(float heightCm, float weightKg, int age, String gender) {
        float bmr;
        if (gender.equalsIgnoreCase("Nam")) {
            // For men: BMR = 10 * weight (kg) + 6.25 * height (cm) - 5 * age + 5
            bmr = (10 * weightKg) + (6.25f * heightCm) - (5 * age) + 5;
        } else {
            // For women: BMR = 10 * weight (kg) + 6.25 * height (cm) - 5 * age - 161
            bmr = (10 * weightKg) + (6.25f * heightCm) - (5 * age) - 161;
        }
        return bmr;
    }

    // Calculate TDEE based on BMR and activity level
    public static float calculateTDEE(float bmr, String activityLevel) {
        float multiplier;
        switch (activityLevel.toLowerCase()) {
            case "sedentary": // Little or no exercise
                multiplier = 1.2f;
                break;
            case "light": // Light exercise/sports 1-3 days/week
                multiplier = 1.375f;
                break;
            case "moderate": // Moderate exercise/sports 3-5 days/week
                multiplier = 1.55f;
                break;
            case "active": // Hard exercise/sports 6-7 days/week
                multiplier = 1.725f;
                break;
            case "very active": // Very hard exercise/sports & physical job
                multiplier = 1.9f;
                break;
            default:
                multiplier = 1.2f; // Default to sedentary if unknown
                break;
        }
        return bmr * multiplier;
    }

    // Get BMI category based on WHO Asia-Pacific standards
    public static String getBMICategory(float bmi) {
        if (bmi < 18.5) {
            return "Thiếu cân";
        } else if (bmi < 23) {
            return "Bình thường";
        } else if (bmi < 25) {
            return "Thừa cân";
        } else if (bmi < 30) {
            return "Béo phì độ I";
        } else {
            return "Béo phì độ II";
        }
    }
}
