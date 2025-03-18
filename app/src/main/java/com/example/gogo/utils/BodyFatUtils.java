package com.example.gogo.utils;

import com.example.gogo.models.User;

import java.util.HashMap;
import java.util.Map;

public class BodyFatUtils {

    public static double calculateBFP(User user) {

        float weight = user.getWeight();
        float height = user.getHeight() / 100;
        int age = user.getAge();
        String gender = user.getGender();

        double bmi = weight / (height * height);

        int genderValue = gender.equalsIgnoreCase("Nam") ? 1 : 0;
        return (1.2 * bmi) + (0.23 * age) - (10.8 * genderValue) - 5.4;
    }

    public static Map<String, Object> evaluateHealthRisk(double bfp, User user) {
        Map<String, Object> result = new HashMap<>();
        int age = user.getAge();
        String gender = user.getGender();

        double bfpLow, bfpHigh;
        if (gender.equalsIgnoreCase("Nam")) {
            if (age >= 18 && age <= 39) {
                bfpLow = 8;
                bfpHigh = 19;
            } else if (age <= 59) {
                bfpLow = 11;
                bfpHigh = 21;
            } else {
                bfpLow = 13;
                bfpHigh = 24;
            }
        } else {
            if (age >= 18 && age <= 39) {
                bfpLow = 21;
                bfpHigh = 32;
            } else if (age <= 59) {
                bfpLow = 23;
                bfpHigh = 33;
            } else {
                bfpLow = 24;
                bfpHigh = 35;
            }
        }

        if (bfp >= bfpLow && bfp <= bfpHigh) {
            result.put("status", "normal");
            result.put("message", "Tỷ lệ mỡ cơ thể của bạn ở mức bình thường. Tiếp tục duy trì lối sống lành mạnh!");
        } else if (bfp > bfpHigh) {
            // BFP cao (béo phì)
            result.put("status", "high");
            result.put("message", "Tỷ lệ mỡ cơ thể cao! Nguy cơ mắc bệnh:");
            Map<String, Float> risks = new HashMap<>();
            risks.put("Bệnh tim mạch", 40f);
            risks.put("Tiểu đường loại 2", 30f);
            risks.put("Huyết áp cao", 20f);
            risks.put("Bệnh khác", 10f);
            result.put("risks", risks);
        } else {
            result.put("status", "low");
            result.put("message", "Tỷ lệ mỡ cơ thể thấp! Nguy cơ sức khỏe:");
            Map<String, Float> risks = new HashMap<>();
            risks.put("Suy giảm miễn dịch", 50f);
            risks.put("Rối loạn nội tiết", 30f);
            risks.put("Vấn đề khác", 20f);
            result.put("risks", risks);
        }

        return result;
    }
}