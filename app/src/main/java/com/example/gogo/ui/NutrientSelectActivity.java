//package com.example.gogo.ui;
//
//import android.content.ContentValues;
//import android.content.Intent;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ImageView;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.AppCompatButton;
//import com.example.gogo.R;
//import com.example.gogo.database.DatabaseHelper;
//
//import java.util.Random;
//
//public class NutrientSelectActivity extends AppCompatActivity {
//    private static final String TAG = "NutrientSelectActivity";
//    private DatabaseHelper dbHelper;
//    private int userId = 1;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_nutrients_select);
//        dbHelper = new DatabaseHelper(this);
//
//        ImageView backButton = findViewById(R.id.backButton);
//        backButton.setOnClickListener(v -> finish());
//
//        AppCompatButton mealPlannerButton = findViewById(R.id.rightButton);
//        mealPlannerButton.setOnClickListener(v -> new GenerateMealPlansTask().execute());
//    }
//
//    private class GenerateMealPlansTask extends AsyncTask<Void, Void, Boolean> {
//        @Override
//        protected Boolean doInBackground(Void... voids) {
//            try {
//                SQLiteDatabase db = dbHelper.getReadableDatabase();
//                Cursor userCursor = db.rawQuery("SELECT UserID, Height, Weight, Gender, Age FROM Users WHERE GoogleID = ?",
//                        new String[]{"google123"});
//                double bmi = 0, bmr = 0;
//                if (userCursor.moveToFirst()) {
//                    userId = userCursor.getInt(0);
//                    float height = userCursor.getFloat(1) / 100;
//                    float weight = userCursor.getFloat(2);
//                    String gender = userCursor.getString(3);
//                    int age = userCursor.getInt(4);
//
//                    bmi = weight / (height * height);
//                    if ("Nam".equalsIgnoreCase(gender)) {
//                        bmr = 88.362 + (13.397 * weight) + (4.799 * height * 100) - (5.677 * age);
//                    } else {
//                        bmr = 447.593 + (9.247 * weight) + (3.098 * height * 100) - (4.330 * age);
//                    }
//                }
//                userCursor.close();
//
//                if (bmr > 0) {
//                    generateDietPlans(bmi, bmr);
//                    return true;
//                }
//                return false;
//            } catch (Exception e) {
//                Log.e(TAG, "Lỗi: " + e.getMessage(), e);
//                return false;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Boolean success) {
//            if (success) {
//                Intent intent = new Intent(NutrientSelectActivity.this, NutrientPlanActivity.class);
//                intent.putExtra("userId", userId);
//                startActivity(intent);
//            }
//        }
//    }
//
//    private void generateDietPlans(double bmi, double bmr) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Random random = new Random();
//
//        double dailyCalories = bmi < 18.5 ? bmr * 1.4 : (bmi < 25 ? bmr * 1.2 : bmr * 0.9);
//        String goal = bmi < 18.5 ? "tăng cân" : (bmi < 25 ? "duy trì cân nặng" : "giảm cân");
//        int[] calorieDistribution = {30, 40, 30};
//        String[] mealTimes = {"Breakfast", "Lunch", "Dinner"};
//
//        db.beginTransaction();
//        try {
//            db.delete("DietPlanFood", "DietID IN (SELECT DietID FROM DietPlan WHERE UserID = ?)", new String[]{String.valueOf(userId)});
//            db.delete("DietPlanDayStatus", "DietID IN (SELECT DietID FROM DietPlan WHERE UserID = ?)", new String[]{String.valueOf(userId)});
//            db.delete("DietPlan", "UserID = ?", new String[]{String.valueOf(userId)});
//
//            for (int planNum = 1; planNum <= 10; planNum++) {
//                ContentValues planValues = new ContentValues();
//                planValues.put("UserID", userId);
//                planValues.put("PlanName", "Kế hoạch " + planNum);
//                planValues.put("Description", String.format("Khoảng %d calo/ngày, hỗ trợ %s.", (int) dailyCalories, goal));
//                long dietId = db.insert("DietPlan", null, planValues);
//
//                if (dietId == -1) {
//                    Log.e(TAG, "Không thể thêm Kế hoạch " + planNum);
//                    continue;
//                }
//
//                for (int day = 1; day <= 7; day++) {
//                    for (int mealIndex = 0; mealIndex < mealTimes.length; mealIndex++) {
//                        int mealCalories = (int) (dailyCalories * calorieDistribution[mealIndex] / 100);
//                        int foodCount = random.nextInt(3) + 3; // Số món ăn từ 3-5
//                        int caloriesPerFood = mealCalories / foodCount;
//
//                        for (int food = 0; food < foodCount; food++) {
//                            int foodId = random.nextInt(55) + 1; // Chọn ngẫu nhiên từ 1 đến 55
//                            ContentValues foodValues = new ContentValues();
//                            foodValues.put("DietID", dietId);
//                            foodValues.put("DayNumber", day);
//                            foodValues.put("MealTime", mealTimes[mealIndex]);
//                            foodValues.put("FoodID", foodId);
//                            foodValues.put("Calories", caloriesPerFood);
//                            db.insert("DietPlanFood", null, foodValues);
//                        }
//                    }
//
//                    ContentValues statusValues = new ContentValues();
//                    statusValues.put("DietID", dietId);
//                    statusValues.put("DayNumber", day);
//                    statusValues.put("isCompleted", 0);
//                    db.insert("DietPlanDayStatus", null, statusValues);
//                }
//            }
//            db.setTransactionSuccessful();
//        } finally {
//            db.endTransaction();
//        }
//        db.close();
//    }
//}
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
import com.example.gogo.database.DatabaseHelper;

import java.util.Random;

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
        mealPlannerButton.setOnClickListener(v -> new GenerateMealPlansTask().execute());

        AppCompatButton nutrientInfoButton = findViewById(R.id.leftButton);
        nutrientInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(NutrientSelectActivity.this, Nutrient7PlanSelectedActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    private class GenerateMealPlansTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
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
                    if ("Nam".equalsIgnoreCase(gender)) {
                        bmr = 88.362 + (13.397 * weight) + (4.799 * height * 100) - (5.677 * age);
                    } else {
                        bmr = 447.593 + (9.247 * weight) + (3.098 * height * 100) - (4.330 * age);
                    }
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
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        }
    }

    private void generateDietPlans(double bmi, double bmr) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Random random = new Random();

        double dailyCalories = bmi < 18.5 ? bmr * 1.4 : (bmi < 25 ? bmr * 1.2 : bmr * 0.9);
        String goal = bmi < 18.5 ? "tăng cân" : (bmi < 25 ? "duy trì cân nặng" : "giảm cân");
        int[] calorieDistribution = {30, 40, 30}; // Breakfast, Lunch, Dinner
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
                planValues.put("Description", String.format("Khoảng %d calo/ngày, hỗ trợ %s.", (int) dailyCalories, goal));
                long dietId = db.insert("DietPlan", null, planValues);

                if (dietId == -1) {
                    Log.e(TAG, "Không thể thêm Kế hoạch " + planNum);
                    continue;
                }

                for (int day = 1; day <= 7; day++) {
                    int totalDayCalories = 0;

                    for (int mealIndex = 0; mealIndex < mealTimes.length; mealIndex++) {
                        int mealCalories = (int) (dailyCalories * calorieDistribution[mealIndex] / 100);
                        int foodCount = random.nextInt(3) + 3;
                        int remainingCalories = mealCalories;

                        for (int food = 0; food < foodCount; food++) {
                            int foodId = random.nextInt(60) + 1;
                            Cursor nutritionCursor = db.rawQuery(
                                    "SELECT Calories FROM Nutrition WHERE FoodID = ?",
                                    new String[]{String.valueOf(foodId)});
                            int actualCalories = 0;
                            if (nutritionCursor.moveToFirst()) {
                                actualCalories = nutritionCursor.getInt(0);
                            } else {
                                actualCalories = remainingCalories / (foodCount - food);
                            }
                            nutritionCursor.close();

                            if (food == foodCount - 1) {
                                actualCalories = remainingCalories;
                            } else if (actualCalories > remainingCalories) {
                                actualCalories = remainingCalories / (foodCount - food);
                            }

                            ContentValues foodValues = new ContentValues();
                            foodValues.put("DietID", dietId);
                            foodValues.put("DayNumber", day);
                            foodValues.put("MealTime", mealTimes[mealIndex]);
                            foodValues.put("FoodID", foodId);
                            foodValues.put("Calories", actualCalories);
                            db.insert("DietPlanFood", null, foodValues);

                            totalDayCalories += actualCalories;
                            remainingCalories -= actualCalories;
                            if (remainingCalories <= 0) break;
                        }
                    }

                    Log.d(TAG, "Plan " + planNum + ", Day " + day + ": Total Calories = " + totalDayCalories);

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