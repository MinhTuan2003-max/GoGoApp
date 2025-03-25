package com.example.gogo.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gogo.R;
import com.example.gogo.adapters.Nutrient7PlanSelectedCardAdapter;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.NutritionDayPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nutrient7PlanSelectedActivity extends AppCompatActivity {
    private static final String TAG = "Nutrient7PlanSelectedActivity";
    private static final int REQUEST_CREATE_PLAN = 1;
    private DatabaseHelper dbHelper;
    private int userId;
    private long dietId = -1;
    private RecyclerView recyclerView;
    private TextView titleTextView;
    private LinearLayout contentLayout;
    private LinearLayout buttonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Nutrient7PlanSelectedActivity onCreate called");
        setContentView(R.layout.activity_nutrient_7plan_selected);

        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", 1);
        Log.d(TAG, "Received userId: " + userId);

        recyclerView = findViewById(R.id.recyclerView);
        titleTextView = findViewById(R.id.titleTextView);
        contentLayout = findViewById(R.id.contentLayout);
        buttonLayout = findViewById(R.id.buttonLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Button suggestPlanButton = findViewById(R.id.suggestPlanButton);
        Button createPlanButton = findViewById(R.id.createPlanButton);

        suggestPlanButton.setOnClickListener(v -> {
            Log.d(TAG, "Suggest Plan button clicked");
            Intent intent = new Intent(Nutrient7PlanSelectedActivity.this, NutrientPlanActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        createPlanButton.setOnClickListener(v -> {
            Log.d(TAG, "Create Plan button clicked");
            Intent intent = new Intent(Nutrient7PlanSelectedActivity.this, NutrientCreatePlanActivity.class);
            intent.putExtra("userId", userId);
            startActivityForResult(intent, REQUEST_CREATE_PLAN);
        });

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        refreshUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called, refreshing UI");
        refreshUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult called, requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == REQUEST_CREATE_PLAN && resultCode == RESULT_OK) {
            dietId = data.getLongExtra("dietId", -1);
            Log.d(TAG, "Received result from NutrientCreatePlanActivity, dietId: " + dietId);
            refreshUI();
        }
    }

    private void refreshUI() {
        Log.d(TAG, "refreshUI called");
        dietId = getSelectedDietId(userId);
        Log.d(TAG, "Refreshing UI, dietId: " + dietId);

        // Kiểm tra xem có dữ liệu kế hoạch hay không
        if (dietId != -1) {
            // Có dữ liệu: hiển thị cả RecyclerView và buttonLayout
            List<NutritionDayPlan> dayPlans = loadDayPlansFromDatabase(dietId);
            recyclerView.setVisibility(View.VISIBLE);
            buttonLayout.setVisibility(View.VISIBLE);
            titleTextView.setText("Kế hoạch ăn uống 7 ngày");
            titleTextView.setTextSize(24);

            Nutrient7PlanSelectedCardAdapter adapter = new Nutrient7PlanSelectedCardAdapter(dayPlans, dietId, this);
            recyclerView.setAdapter(adapter);
            Log.d(TAG, "Updated RecyclerView with " + dayPlans.size() + " days");
        } else {
            // Không có dữ liệu: chỉ hiển thị buttonLayout, ẩn RecyclerView
            recyclerView.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.VISIBLE);
            titleTextView.setText("Chưa có kế hoạch được chọn");
            titleTextView.setTextSize(24);
            Log.d(TAG, "No plan data found, hiding RecyclerView");
        }
    }

    private long getSelectedDietId(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT DietID FROM SelectedDietPlan WHERE UserID = " + userId + " ORDER BY SelectedAt DESC LIMIT 1";
        Log.d(TAG, "Executing query: " + query);
        Cursor cursor = db.rawQuery(query, null);
        long selectedDietId = -1;
        if (cursor.moveToFirst()) {
            selectedDietId = cursor.getLong(0);
            Log.d(TAG, "Found DietID: " + selectedDietId + " in SelectedDietPlan");
        } else {
            Log.w(TAG, "No rows found in SelectedDietPlan for UserID: " + userId);
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Selected DietID: " + selectedDietId);
        return selectedDietId;
    }

    private List<NutritionDayPlan> loadDayPlansFromDatabase(long dietId) {
        Log.d(TAG, "loadDayPlansFromDatabase called with dietId: " + dietId);
        List<NutritionDayPlan> dayPlans = new ArrayList<>();
        Map<Integer, NutritionDayPlan> dayPlanMap = new HashMap<>();
        for (int day = 1; day <= 7; day++) {
            dayPlanMap.put(day, new NutritionDayPlan(day, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false));
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT dpf.DayNumber, dpf.MealTime, f.FoodName, dpf.Calories, dps.isCompleted " +
                "FROM DietPlanFood dpf " +
                "JOIN Food f ON dpf.FoodID = f.FoodID " +
                "LEFT JOIN DietPlanDayStatus dps ON dpf.DietID = dps.DietID AND dpf.DayNumber = dps.DayNumber " +
                "WHERE dpf.DietID = " + dietId + " AND dpf.DayNumber BETWEEN 1 AND 7 " +
                "ORDER BY dpf.DayNumber, dpf.MealTime";
        Log.d(TAG, "Executing query: " + query);
        Cursor cursor = db.rawQuery(query, null);

        int rowCount = 0;
        while (cursor.moveToNext()) {
            rowCount++;
            int dayNumber = cursor.getInt(0);
            String mealTime = cursor.getString(1);
            String foodName = cursor.getString(2);
            int calories = cursor.getInt(3);
            boolean isCompleted = cursor.getInt(4) == 1;

            Log.d(TAG, "Loaded row " + rowCount + ": Day=" + dayNumber + ", Meal=" + mealTime + ", Food=" + foodName + ", Calories=" + calories + ", Completed=" + isCompleted);

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
        Log.d(TAG, "Loaded " + rowCount + " rows from DietPlanFood for DietID: " + dietId);
        cursor.close();
        db.close();

        for (NutritionDayPlan dayPlan : dayPlanMap.values()) {
            if (dayPlan.getBreakfast().isEmpty()) dayPlan.getBreakfast().add("Nhịn");
            if (dayPlan.getLunch().isEmpty()) dayPlan.getLunch().add("Nhịn");
            if (dayPlan.getDinner().isEmpty()) dayPlan.getDinner().add("Nhịn");
            dayPlans.add(dayPlan);
            Log.d(TAG, "Day " + dayPlan.getDayNumber() + ": Breakfast=" + dayPlan.getBreakfast() +
                    ", Lunch=" + dayPlan.getLunch() + ", Dinner=" + dayPlan.getDinner());
        }

        return dayPlans;
    }
}