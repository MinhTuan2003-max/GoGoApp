package com.example.gogo.respository;

import android.content.Context;
import android.database.Cursor;

import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.User;
import com.example.gogo.utils.HealthUtils;

public class HealthRepository {

    private DatabaseHelper dbHelper;

    public HealthRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Save health index data
    public boolean saveHealthIndex(String googleId) {
        User user = dbHelper.getUserByGoogleId(googleId);
        if (user != null && user.getHeight() > 0 && user.getWeight() > 0 && user.getAge() > 0 && user.getGender() != null) {
            float bmi = HealthUtils.calculateBMI(user.getHeight(), user.getWeight());
            float bmr = HealthUtils.calculateBMR(user.getHeight(), user.getWeight(), user.getAge(), user.getGender());
            return dbHelper.insertHealthIndex(user.getUserId(), bmi, bmr);
        }
        return false;
    }

    // Get latest health index data
    public HealthData getLatestHealthIndex(int userId) {
        Cursor cursor = dbHelper.getLatestHealthIndex(userId);
        HealthData healthData = null;
        if (cursor != null && cursor.moveToFirst()) {
            float bmi = cursor.getFloat(cursor.getColumnIndexOrThrow("BMI"));
            float bmr = cursor.getFloat(cursor.getColumnIndexOrThrow("BMR"));
            healthData = new HealthData(bmi, bmr);
            cursor.close();
        }
        return healthData;
    }

    // Get user data
    public User getUserData(String googleId) {
        return dbHelper.getUserByGoogleId(googleId);
    }

    // Data class for HealthIndex
    public static class HealthData {
        public float bmi;
        public float bmr;

        public HealthData(float bmi, float bmr) {
            this.bmi = bmi;
            this.bmr = bmr;
        }
    }
}