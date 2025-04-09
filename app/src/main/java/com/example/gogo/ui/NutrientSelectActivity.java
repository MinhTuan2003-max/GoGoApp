package com.example.gogo.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.gogo.R;
import com.example.gogo.database.AccountDAO;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class NutrientSelectActivity extends AppCompatActivity {
    private static final String TAG = "NutrientSelectActivity";
    private DatabaseHelper dbHelper;
    private AccountDAO accountDAO;
    private int userId = -1;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrients_select);

        dbHelper = new DatabaseHelper(this);
        accountDAO = new AccountDAO(dbHelper);

        Intent intent = getIntent();
        userId = intent.getIntExtra("USER_ID", -1);
        Log.d(TAG, "Received userId from Intent: " + userId);
        if (userId == -1) {
            loadUserData();
            Log.d(TAG, "Loaded userId from Google Sign-In: " + userId);
        }

        if (userId == -1) {
            Log.e(TAG, "Không tìm thấy userId");
            Intent loginIntent = new Intent(this, MainActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        AppCompatButton mealPlannerButton = findViewById(R.id.rightButton);
        mealPlannerButton.setOnClickListener(v -> new GenerateMealPlansTask().execute());

        AppCompatButton nutrientInfoButton = findViewById(R.id.leftButton);
        nutrientInfoButton.setOnClickListener(v -> {
            Intent nutrientIntent = new Intent(NutrientSelectActivity.this, Nutrient7PlanSelectedActivity.class);
            nutrientIntent.putExtra("USER_ID", userId);
            startActivity(nutrientIntent);
            Log.d(TAG, "Navigating to Nutrient7PlanSelectedActivity with userId: " + userId);
        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        setupBottomNavigation();
    }

    private void loadUserData() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            User user = accountDAO.getUserByGoogleId(account.getId());
            if (user != null) {
                userId = user.getUserId();
            } else {
                userId = accountDAO.getUserIdByGoogleId(account.getId());
            }
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_profile) {
                Intent profileIntent = new Intent(NutrientSelectActivity.this, ViewProfileActivity.class);
                profileIntent.putExtra("USER_ID", userId);
                startActivity(profileIntent);
                return true;
            } else if (itemId == R.id.nav_home) {
                Intent homeIntent = new Intent(NutrientSelectActivity.this, HomeActivity.class);
                homeIntent.putExtra("USER_ID", userId);
                startActivity(homeIntent);
                return true;
            } else if (itemId == R.id.nav_settings) {
                Intent settingsIntent = new Intent(NutrientSelectActivity.this, SettingActivity.class);
                settingsIntent.putExtra("USER_ID", userId);
                startActivity(settingsIntent);
                return true;
            }
            return false;
        });
    }

    private class GenerateMealPlansTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor userCursor = db.rawQuery("SELECT UserID, Height, Weight, Gender, Age FROM Users WHERE UserID = ?",
                        new String[]{String.valueOf(userId)});
                double bmi = 0, bmr = 0;
                if (userCursor.moveToFirst()) {
                    float height = userCursor.getFloat(1) / 100;
                    float weight = userCursor.getFloat(2);
                    String gender = userCursor.getString(3);
                    int age = userCursor.getInt(4);

                    bmi = weight / (height * height);
                    if ("Nam".equalsIgnoreCase(gender)) {
                        bmr = 88.362 + (13.397 * weight) + (4.799 * height * 100) - (5.677 * age);
                    } else {
                        bmr = 447.593 + (9.247 * weight) + (3.098 * height * 100) - (4.330 * age);
                    }
                } else {
                    Log.e(TAG, "Không tìm thấy thông tin người dùng với UserID: " + userId);
                }
                userCursor.close();

                if (bmr > 0) {
                    generateDietPlans(bmi, bmr);
                    return true;
                }
                return false;
            } catch (Exception e) {
                Log.e(TAG, "Lỗi: " + e.getMessage(), e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Intent intent = new Intent(NutrientSelectActivity.this, NutrientPlanActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                Log.d(TAG, "Navigating to NutrientPlanActivity with userId: " + userId);
            } else {
                Log.e(TAG, "Failed to generate meal plans");
            }
        }
    }

    private void generateDietPlans(double bmi, double bmr) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Random random = new Random();

        double dailyCalories = bmi < 18.5 ? bmr * 1.4 : (bmi < 25 ? bmr * 1.2 : bmr * 0.9);
        double minCalories = dailyCalories - 300;
        double maxCalories = dailyCalories + 300;
        String goal = bmi < 18.5 ? "tăng cân" : (bmi < 25 ? "duy trì cân nặng" : "giảm cân");

        int[] calorieDistributionMin = {25, 40, 25};
        int[] calorieDistributionMax = {30, 45, 30};
        String[] mealTimes = {"Breakfast", "Lunch", "Dinner"};

        db.beginTransaction();
        try {
            db.delete("DietPlanFood", "DietID IN (SELECT DietID FROM DietPlan WHERE UserID = ?)", new String[]{String.valueOf(userId)});
            db.delete("DietPlanDayStatus", "DietID IN (SELECT DietID FROM DietPlan WHERE UserID = ?)", new String[]{String.valueOf(userId)});
            db.delete("DietPlan", "UserID = ?", new String[]{String.valueOf(userId)});

            for (int planNum = 1; planNum <= 10; planNum++) {
                ContentValues planValues = new ContentValues();
                planValues.put("UserID", userId);
                planValues.put("PlanName", "Kế hoạch " + planNum);
                planValues.put("Description", String.format("Khoảng %d-%d calo/ngày, hỗ trợ %s.", (int) minCalories, (int) maxCalories, goal));
                long dietId = db.insert("DietPlan", null, planValues);

                if (dietId == -1) {
                    Log.e(TAG, "Không thể thêm Kế hoạch " + planNum);
                    continue;
                }

                for (int day = 1; day <= 7; day++) {
                    int totalDayCalories = 0;
                    Set<Integer> usedFoodIds = new HashSet<>();

                    for (int mealIndex = 0; mealIndex < mealTimes.length; mealIndex++) {
                        int distribution = calorieDistributionMin[mealIndex] + random.nextInt(calorieDistributionMax[mealIndex] - calorieDistributionMin[mealIndex] + 1);
                        int mealCalories = (int) (dailyCalories * distribution / 100);
                        int foodCount = random.nextInt(3) + 3;

                        for (int food = 0; food < foodCount; food++) {
                            int foodId;
                            int attempts = 0;
                            do {
                                foodId = random.nextInt(60) + 1;
                                attempts++;
                                if (attempts > 100) {
                                    Log.w(TAG, "Không đủ món ăn độc đáo, sử dụng lại foodId: " + foodId);
                                    break;
                                }
                            } while (usedFoodIds.contains(foodId));

                            usedFoodIds.add(foodId);

                            Cursor nutritionCursor = db.rawQuery(
                                    "SELECT Calories FROM Nutrition WHERE FoodID = ?",
                                    new String[]{String.valueOf(foodId)});
                            int actualCalories = 0;
                            if (nutritionCursor.moveToFirst()) {
                                actualCalories = nutritionCursor.getInt(0);
                            } else {
                                Log.w(TAG, "Không tìm thấy thông tin calo cho FoodID: " + foodId + ", gán mặc định 100 kcal");
                                actualCalories = 100;
                            }
                            nutritionCursor.close();

                            if (totalDayCalories + actualCalories > maxCalories) {
                                actualCalories = (int) (maxCalories - totalDayCalories);
                            }
                            if (actualCalories <= 0) {
                                break;
                            }

                            ContentValues foodValues = new ContentValues();
                            foodValues.put("DietID", dietId);
                            foodValues.put("DayNumber", day);
                            foodValues.put("MealTime", mealTimes[mealIndex]);
                            foodValues.put("FoodID", foodId);
                            foodValues.put("Calories", actualCalories);
                            db.insert("DietPlanFood", null, foodValues);

                            totalDayCalories += actualCalories;
                            if (totalDayCalories >= maxCalories) break;
                        }
                        if (totalDayCalories >= maxCalories) break;
                    }

                    if (totalDayCalories < minCalories) {
                        Log.w(TAG, "Plan " + planNum + ", Day " + day + ": Tổng calo " + totalDayCalories + " thấp hơn giới hạn tối thiểu " + minCalories);
                    } else if (totalDayCalories > maxCalories) {
                        Log.w(TAG, "Plan " + planNum + ", Day " + day + ": Tổng calo " + totalDayCalories + " vượt quá giới hạn tối đa " + maxCalories);
                    } else {
                        Log.d(TAG, "Plan " + planNum + ", Day " + day + ": Tổng calo = " + totalDayCalories);
                    }

                    ContentValues statusValues = new ContentValues();
                    statusValues.put("DietID", dietId);
                    statusValues.put("DayNumber", day);
                    statusValues.put("isCompleted", 0);
                    db.insert("DietPlanDayStatus", null, statusValues);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }
}