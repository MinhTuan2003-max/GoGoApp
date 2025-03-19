package com.example.gogo.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.adapters.Nutrient7PlanCardAdapter;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.NutritionDayPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nutrient7PlanActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int dietId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrient_7plan);

        dbHelper = new DatabaseHelper(this);

        // Lấy dietId từ intent
        dietId = getIntent().getIntExtra("dietId", -1);
        Log.d("Nutrient7PlanActivity", "Received dietId: " + dietId);
        if (dietId == -1) {
            Toast.makeText(this, "Không tìm thấy kế hoạch ăn uống", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Lấy dữ liệu từ database
        List<NutritionDayPlan> dayPlans = loadDayPlansFromDatabase();
        if (dayPlans.isEmpty()) {
            Log.w("Nutrient7PlanActivity", "No day plans found for dietId: " + dietId);
            Toast.makeText(this, "Không có dữ liệu kế hoạch cho ngày này. DietID: " + dietId, Toast.LENGTH_LONG).show();
            // finish(); // Bỏ tạm thời để debug
            return;
        } else {
            Log.d("Nutrient7PlanActivity", "Loaded " + dayPlans.size() + " day plans with days: " + getDayNumbers(dayPlans));
        }

        // Gắn adapter vào RecyclerView
        Nutrient7PlanCardAdapter adapter = new Nutrient7PlanCardAdapter(dayPlans, dietId, this);
        recyclerView.setAdapter(adapter);

        // Xử lý nút back
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private List<NutritionDayPlan> loadDayPlansFromDatabase() {
        List<NutritionDayPlan> dayPlans = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Tạo dữ liệu mặc định cho 7 ngày
        Map<Integer, NutritionDayPlan> dayPlanMap = new HashMap<>();
        for (int day = 1; day <= 7; day++) {
            NutritionDayPlan dayPlan = new NutritionDayPlan();
            dayPlan.setDay("Ngày " + day);
            dayPlanMap.put(day, dayPlan);
        }

        // Lấy dữ liệu từ DietPlanFood
        String query = "SELECT DayNumber, MealTime, FoodID, Calories FROM DietPlanFood WHERE DietID = ? ORDER BY DayNumber, MealTime";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(dietId)});
        Log.d("Nutrient7PlanActivity", "Cursor count for DietID " + dietId + ": " + cursor.getCount());

        while (cursor.moveToNext()) {
            int dayNumber = cursor.getInt(0);
            String mealTime = cursor.getString(1);
            int foodId = cursor.getInt(2);
            int calories = cursor.getInt(3);

            if (dayNumber < 1 || dayNumber > 7) continue; // Bỏ qua nếu ngày không hợp lệ

            String foodName = getFoodNameById(db, foodId);
            Log.d("Nutrient7PlanActivity", "Found meal: Day " + dayNumber + ", MealTime " + mealTime + ", Food " + foodName + " (" + calories + " cal)");

            NutritionDayPlan dayPlan = dayPlanMap.get(dayNumber);
            if ("Breakfast".equalsIgnoreCase(mealTime)) {
                dayPlan.getBreakfast().add(foodName + " (" + calories + " cal)");
            } else if ("Lunch".equalsIgnoreCase(mealTime)) {
                dayPlan.getLunch().add(foodName + " (" + calories + " cal)");
            } else if ("Dinner".equalsIgnoreCase(mealTime)) {
                dayPlan.getDinner().add(foodName + " (" + calories + " cal)");
            }
        }
        cursor.close();

        // Thêm tất cả 7 ngày vào danh sách, kể cả ngày không có dữ liệu
        for (int day = 1; day <= 7; day++) {
            NutritionDayPlan dayPlan = dayPlanMap.get(day);
            dayPlan.setCompleted(day % 2 == 0); // Ví dụ trạng thái hoàn thành
            dayPlans.add(dayPlan);
        }

        Log.d("Nutrient7PlanActivity", "Loaded " + dayPlans.size() + " day plans");
        return dayPlans;
    }

    private String getFoodNameById(SQLiteDatabase db, int foodId) {
        Cursor foodCursor = db.rawQuery("SELECT FoodName FROM Food WHERE FoodID = ?", new String[]{String.valueOf(foodId)});
        String foodName = "Không xác định";
        if (foodCursor.moveToFirst()) {
            foodName = foodCursor.getString(0);
        }
        foodCursor.close();
        return foodName;
    }

    private String getDayNumbers(List<NutritionDayPlan> plans) {
        StringBuilder sb = new StringBuilder();
        for (NutritionDayPlan plan : plans) {
            sb.append(plan.getDay()).append(", ");
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 2) : "none";
    }
}