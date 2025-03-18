package com.example.gogo.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.example.gogo.R;
import com.example.gogo.database.DatabaseHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class NutrientSelectActivity extends AppCompatActivity {
    private static final String TAG = "NutrientSelectActivity";
    private DatabaseHelper dbHelper;
    private int userId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrients_select);
        dbHelper = new DatabaseHelper(this);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        AppCompatButton mealPlannerButton = findViewById(R.id.rightButton);
        mealPlannerButton.setOnClickListener(v -> {
            new GenerateMealPlansTask().execute();
        });
    }

    private class GenerateMealPlansTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "Starting meal plan generation in background...");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Log.d(TAG, "Database opened successfully");

                Cursor userCursor = db.rawQuery("SELECT UserID, Height, Weight, Gender, Age FROM Users WHERE GoogleID = ?",
                        new String[]{"google123"});
                double bmi = 0, bmr = 0;
                if (userCursor.moveToFirst()) {
                    userId = userCursor.getInt(0);
                    float height = userCursor.getFloat(1) / 100;
                    float weight = userCursor.getFloat(2);
                    String gender = userCursor.getString(3);
                    int age = userCursor.getInt(4);

                    bmi = weight / (height * height);
                    Log.d(TAG, "BMI calculated: " + bmi);

                    if ("Male".equalsIgnoreCase(gender)) {
                        bmr = 88.362 + (13.397 * weight) + (4.799 * height * 100) - (5.677 * age);
                    } else {
                        bmr = 447.593 + (9.247 * weight) + (3.098 * height * 100) - (4.330 * age);
                    }
                    Log.d(TAG, "BMR calculated: " + bmr);
                } else {
                    Log.w(TAG, "No user found with GoogleID: google123");
                    SQLiteDatabase writableDb = dbHelper.getWritableDatabase();
                    ContentValues userValues = new ContentValues();
                    userValues.put("FullName", "Nguyễn Văn A");
                    userValues.put("Email", "nguyenvana@example.com");
                    userValues.put("GoogleID", "google123");
                    userValues.put("Age", 25);
                    userValues.put("Gender", "Nam");
                    userValues.put("Height", 175.5);
                    userValues.put("Weight", 70.0);
                    userValues.put("ProfileImageUrl", "https://example.com/nguyenvana.jpg");
                    userId = (int) writableDb.insert("Users", null, userValues);
                    writableDb.close();
                    Log.d(TAG, "Inserted default user with UserID: " + userId);
                }
                userCursor.close();

                if (bmr > 0) {
                    generateDietPlans(bmi, bmr);
                    Log.d(TAG, "Diet plans generated and saved");
                    return true;
                } else {
                    Log.e(TAG, "Cannot generate diet plans: Invalid BMR");
                    return false;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in doInBackground: " + e.getMessage(), e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Intent intent = new Intent(NutrientSelectActivity.this, NutrientPlanActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                Log.d(TAG, "Intent started for NutrientPlanActivity");
            } else {
                Log.e(TAG, "Failed to generate meal plans");
            }
        }
    }

    private void generateDietPlans(double bmi, double bmr) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Random random = new Random();

        double dailyCalories;
        String goal;
        if (bmi < 18.5) {
            dailyCalories = bmr * 1.4;
            goal = "tăng cân";
        } else if (bmi >= 18.5 && bmi < 25) {
            dailyCalories = bmr * 1.2;
            goal = "duy trì cân nặng";
        } else {
            dailyCalories = bmr * 0.9;
            goal = "giảm cân";
        }

        int[] calorieDistribution = {30, 40, 30};
        String[] mealTimes = {"Breakfast", "Lunch", "Dinner"};

        db.beginTransaction();
        try {
            db.delete("DietPlanFood", "DietID IN (SELECT DietID FROM DietPlan WHERE UserID = ?)", new String[]{String.valueOf(userId)});
            db.delete("DietPlan", "UserID = ?", new String[]{String.valueOf(userId)});

            for (int planNum = 1; planNum <= 10; planNum++) {
                ContentValues planValues = new ContentValues();
                planValues.put("UserID", userId);
                planValues.put("PlanName", "Kế hoạch " + planNum);
                planValues.put("isCompleted", 0);
                long dietId = db.insert("DietPlan", null, planValues);

                for (int day = 1; day <= 7; day++) {
                    for (int mealIndex = 0; mealIndex < mealTimes.length; mealIndex++) {
                        int mealCalories = (int) (dailyCalories * calorieDistribution[mealIndex] / 100);
                        int foodCountPerMeal = random.nextInt(3) + 3;
                        int caloriesPerFood = mealCalories / foodCountPerMeal;

                        for (int food = 0; food < foodCountPerMeal; food++) {
                            int foodId = random.nextInt(30) + 1;
                            ContentValues foodValues = new ContentValues();
                            foodValues.put("DietID", dietId);
                            foodValues.put("DayNumber", day);
                            foodValues.put("MealTime", mealTimes[mealIndex]);
                            foodValues.put("FoodID", foodId);
                            foodValues.put("Calories", caloriesPerFood);
                            db.insert("DietPlanFood", null, foodValues);
                        }
                    }
                }

                String description = String.format("Kế hoạch này cung cấp khoảng %d calo mỗi ngày, hỗ trợ %s.", (int) dailyCalories, goal);
                ContentValues updateValues = new ContentValues();
                updateValues.put("Description", description);
                db.update("DietPlan", updateValues, "DietID = ?", new String[]{String.valueOf(dietId)});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }
}