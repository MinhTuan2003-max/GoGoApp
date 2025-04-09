package com.example.gogo.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.adapters.Nutrient7PlanCardAdapter;
import com.example.gogo.database.AccountDAO;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.NutritionDayPlan;
import com.example.gogo.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nutrient7PlanActivity extends AppCompatActivity {
    private static final String TAG = "Nutrient7PlanActivity";
    private DatabaseHelper dbHelper;
    private AccountDAO accountDAO;
    private int dietId = -1;
    private BottomNavigationView bottomNavigationView;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrient_7plan);

        dbHelper = new DatabaseHelper(this);
        accountDAO = new AccountDAO(dbHelper);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView == null) {
            Log.w(TAG, "BottomNavigationView not found in layout activity_nutrient_7plan.xml");
        }

        Intent intent = getIntent();
        userId = intent.getIntExtra("USER_ID", -1);
        dietId = intent.getIntExtra("dietId", -1);
        Log.d(TAG, "Received from Intent - userId: " + userId + ", dietId: " + dietId);

        if (userId == -1) {
            loadUserData();
            Log.d(TAG, "Loaded userId from Google Sign-In: " + userId);
        }

        if (userId == -1) {
            Log.e(TAG, "Không tìm thấy userId");
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(this, MainActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        if (dietId == -1) {
            Log.e(TAG, "Invalid dietId received");
            Toast.makeText(this, "Không thể tải kế hoạch: DietID không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<NutritionDayPlan> dayPlans = loadDayPlansFromDatabase(dietId);
        if (dayPlans.isEmpty()) {
            Log.w(TAG, "No data found for dietId: " + dietId);
            Toast.makeText(this, "Không tìm thấy dữ liệu cho kế hoạch này", Toast.LENGTH_SHORT).show();
        }
        Nutrient7PlanCardAdapter adapter = new Nutrient7PlanCardAdapter(dayPlans, dietId, this);
        recyclerView.setAdapter(adapter);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        Button selectPlanButton = findViewById(R.id.selectPlanButton);
        selectPlanButton.setOnClickListener(v -> {
            boolean success = saveSelectedPlanToDatabase(userId, dietId);
            if (success) {
                Toast.makeText(this, "Đã chọn kế hoạch thành công!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Plan selected and saved: DietID " + dietId + " for UserID " + userId);
            } else {
                Toast.makeText(this, "Chọn kế hoạch thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to save selected plan for DietID " + dietId + " and UserID " + userId);
            }
        });

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
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_profile) {
                    Intent profileIntent = new Intent(Nutrient7PlanActivity.this, ViewProfileActivity.class);
                    profileIntent.putExtra("USER_ID", userId);
                    startActivity(profileIntent);
                    return true;
                } else if (itemId == R.id.nav_home) {
                    Intent homeIntent = new Intent(Nutrient7PlanActivity.this, HomeActivity.class);
                    homeIntent.putExtra("USER_ID", userId);
                    startActivity(homeIntent);
                    return true;
                } else if (itemId == R.id.nav_settings) {
                    Intent settingsIntent = new Intent(Nutrient7PlanActivity.this, SettingActivity.class);
                    settingsIntent.putExtra("USER_ID", userId);
                    startActivity(settingsIntent);
                    return true;
                }
                return false;
            });
        } else {
            Log.w(TAG, "BottomNavigationView is null, skipping navigation setup");
        }
    }

    private List<NutritionDayPlan> loadDayPlansFromDatabase(int dietId) {
        List<NutritionDayPlan> dayPlans = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT dpf.DayNumber, dpf.MealTime, f.FoodName, dpf.Calories, dps.isCompleted " +
                        "FROM DietPlanFood dpf " +
                        "JOIN Food f ON dpf.FoodID = f.FoodID " +
                        "LEFT JOIN DietPlanDayStatus dps ON dpf.DietID = dps.DietID AND dpf.DayNumber = dps.DayNumber " +
                        "WHERE dpf.DietID = ? AND dpf.DayNumber BETWEEN 1 AND 7 " +
                        "ORDER BY dpf.DayNumber, dpf.MealTime",
                new String[]{String.valueOf(dietId)}
        );

        Map<Integer, NutritionDayPlan> dayPlanMap = new HashMap<>();
        for (int day = 1; day <= 7; day++) {
            dayPlanMap.put(day, new NutritionDayPlan(day, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false));
        }

        while (cursor.moveToNext()) {
            int dayNumber = cursor.getInt(0);
            String mealTime = cursor.getString(1);
            String foodName = cursor.getString(2);
            int calories = cursor.getInt(3);
            boolean isCompleted = cursor.getInt(4) == 1;

            NutritionDayPlan dayPlan = dayPlanMap.get(dayNumber);
            dayPlan.setCompleted(isCompleted);

            String foodWithCalories = foodName + " (" + calories + " kcal)";
            switch (mealTime) {
                case "Breakfast":
                    dayPlan.getBreakfast().add(foodWithCalories);
                    break;
                case "Lunch":
                    dayPlan.getLunch().add(foodWithCalories);
                    break;
                case "Dinner":
                    dayPlan.getDinner().add(foodWithCalories);
                    break;
            }
            dayPlan.addCalories(calories);
        }
        cursor.close();

        for (NutritionDayPlan dayPlan : dayPlanMap.values()) {
            if (dayPlan.getBreakfast().isEmpty()) dayPlan.getBreakfast().add("Nhịn");
            if (dayPlan.getLunch().isEmpty()) dayPlan.getLunch().add("Nhịn");
            if (dayPlan.getDinner().isEmpty()) dayPlan.getDinner().add("Nhịn");
            dayPlans.add(dayPlan);
            Log.d(TAG, "DietID " + dietId + ", Day " + dayPlan.getDayNumber() + ": Total Calories = " + dayPlan.getTotalCalories());
        }

        db.close();
        return dayPlans;
    }

    private boolean saveSelectedPlanToDatabase(int userId, int dietId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;
        db.beginTransaction();
        try {
            int deletedRows = db.delete("SelectedDietPlan", "UserID = ?", new String[]{String.valueOf(userId)});
            Log.d(TAG, "Deleted " + deletedRows + " previous selections for UserID " + userId);

            ContentValues values = new ContentValues();
            values.put("UserID", userId);
            values.put("DietID", dietId);
            long newRowId = db.insert("SelectedDietPlan", null, values);

            if (newRowId != -1) {
                success = true;
                Log.d(TAG, "Inserted new selection: SelectionID " + newRowId);
            } else {
                Log.e(TAG, "Failed to insert new selection into SelectedDietPlan");
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error saving selected plan: " + e.getMessage());
            success = false;
        } finally {
            db.endTransaction();
            db.close();
        }
        return success;
    }
}