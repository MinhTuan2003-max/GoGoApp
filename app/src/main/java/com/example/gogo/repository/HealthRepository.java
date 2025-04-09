package com.example.gogo.repository;

import android.content.Context;
import android.util.Log;

import com.example.gogo.database.AccountDAO;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.database.HealthIndexDAO;
import com.example.gogo.models.User;
import com.example.gogo.utils.HealthUtils;

public class HealthRepository {
    private static final String TAG = "HealthRepository";
    private final HealthIndexDAO healthIndexDAO;
    private final AccountDAO accountDAO;
    private final Context context;
    private final DatabaseHelper databaseHelper;

    public HealthRepository(Context context) {
        this.context = context;
        this.databaseHelper = new DatabaseHelper(context);
        this.healthIndexDAO = new HealthIndexDAO(databaseHelper);
        this.accountDAO = new AccountDAO(databaseHelper);
    }

    public boolean saveHealthIndex(String googleId) {
        try {
            User user = accountDAO.getUserByGoogleId(googleId);
            if (user == null) {
                Log.w(TAG, "User not found for GoogleID: " + googleId);
                return false;
            }
            if (user.getHeight() <= 0 || user.getWeight() <= 0 || user.getAge() <= 0 || user.getGender() == null) {
                Log.w(TAG, "Invalid user data for GoogleID: " + googleId +
                        " [Height=" + user.getHeight() + ", Weight=" + user.getWeight() +
                        ", Age=" + user.getAge() + ", Gender=" + user.getGender() + "]");
                return false;
            }

            float bmi = HealthUtils.calculateBMI(user.getHeight(), user.getWeight());
            float bmr = HealthUtils.calculateBMR(user.getHeight(), user.getWeight(), user.getAge(), user.getGender());
            boolean success = healthIndexDAO.insertHealthIndex(user.getUserId(), bmi, bmr);
            Log.d(TAG, "Health index save result for UserID " + user.getUserId() + ": " + (success ? "success" : "failure"));
            return success;
        } catch (Exception e) {
            Log.e(TAG, "Error saving health index for GoogleID: " + googleId, e);
            return false;
        }
    }

    public User getUserData(String googleId) {
        try {
            User user = accountDAO.getUserByGoogleId(googleId);
            if (user == null) {
                Log.w(TAG, "No user found for GoogleID: " + googleId);
            }
            return user;
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving user data for GoogleID: " + googleId, e);
            return null;
        }
    }

    public boolean updateUserDataForFullName(String googleId, int age, String gender, float height, float weight, String fullName) {
        return accountDAO.updateUserDataForFullName(googleId, age, gender, height, weight, fullName);
    }

    public void close() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}