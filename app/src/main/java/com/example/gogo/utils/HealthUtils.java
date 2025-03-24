package com.example.gogo.utils;

public class HealthUtils {
    public static float calculateBMI(float heightCm, float weightKg) {
        float heightM = heightCm / 100;
        return weightKg / (heightM * heightM);
    }

    public static float calculateBMR(float heightCm, float weightKg, int age, String gender) {
        float bmr;
        if (gender.equalsIgnoreCase("Nam")) {
            bmr = (10 * weightKg) + (6.25f * heightCm) - (5 * age) + 5;
        } else {
            bmr = (10 * weightKg) + (6.25f * heightCm) - (5 * age) - 161;
        }
        return bmr;
    }

    public static float calculateTDEE(float bmr, String activityLevel) {
        float multiplier;
        switch (activityLevel.toLowerCase()) {
            case "sedentary":
                multiplier = 1.2f;
                break;
            case "light":
                multiplier = 1.375f;
                break;
            case "moderate":
                multiplier = 1.55f;
                break;
            case "active":
                multiplier = 1.725f;
                break;
            case "very active":
                multiplier = 1.9f;
                break;
            default: // default
                multiplier = 1.2f;
                break;
        }
        return bmr * multiplier;
    }

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
