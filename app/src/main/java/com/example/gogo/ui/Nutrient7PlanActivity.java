////package com.example.gogo.ui;
////
////import android.content.ContentValues;
////import android.database.Cursor;
////import android.database.sqlite.SQLiteDatabase;
////import android.os.Bundle;
////import android.util.Log;
////import android.widget.ImageView;
////import androidx.appcompat.app.AppCompatActivity;
////import androidx.recyclerview.widget.LinearLayoutManager;
////import androidx.recyclerview.widget.RecyclerView;
////import com.example.gogo.R;
////import com.example.gogo.adapters.Nutrient7PlanCardAdapter;
////import com.example.gogo.database.DatabaseHelper;
////import com.example.gogo.models.NutritionDayPlan;
////
////import java.util.ArrayList;
////import java.util.List;
////
////public class Nutrient7PlanActivity extends AppCompatActivity {
////    private static final String TAG = "Nutrient7PlanActivity";
////    private DatabaseHelper dbHelper;
////    private int dietId;
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_nutrient_7plan);
////
////        dbHelper = new DatabaseHelper(this);
////        dietId = getIntent().getIntExtra("dietId", -1);
////        Log.d(TAG, "Received dietId: " + dietId);
////
////        if (dietId == -1) {
////            Log.e(TAG, "Invalid dietId received");
////            finish();
////            return;
////        }
////
////        RecyclerView recyclerView = findViewById(R.id.recyclerView);
////        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
////
////        List<NutritionDayPlan> dayPlans = loadDayPlansFromDatabase(dietId);
////        Nutrient7PlanCardAdapter adapter = new Nutrient7PlanCardAdapter(dayPlans, dietId, this);
////        recyclerView.setAdapter(adapter);
////
////        ImageView backButton = findViewById(R.id.backButton);
////        backButton.setOnClickListener(v -> finish());
////    }
////
////    private List<NutritionDayPlan> loadDayPlansFromDatabase(int dietId) {
////        List<NutritionDayPlan> dayPlans = new ArrayList<>();
////        SQLiteDatabase db = dbHelper.getReadableDatabase();
////
////        for (int day = 1; day <= 7; day++) {
////            List<String> breakfast = new ArrayList<>();
////            List<String> lunch = new ArrayList<>();
////            List<String> dinner = new ArrayList<>();
////
////            Cursor cursor = db.rawQuery(
////                    "SELECT f.FoodName, dpf.MealTime " +
////                            "FROM DietPlanFood dpf " +
////                            "JOIN Food f ON dpf.FoodID = f.FoodID " +
////                            "WHERE dpf.DietID = ? AND dpf.DayNumber = ?",
////                    new String[]{String.valueOf(dietId), String.valueOf(day)});
////
////            while (cursor.moveToNext()) {
////                String foodName = cursor.getString(0);
////                String mealTime = cursor.getString(1);
////                switch (mealTime) {
////                    case "Breakfast":
////                        breakfast.add(foodName);
////                        break;
////                    case "Lunch":
////                        lunch.add(foodName);
////                        break;
////                    case "Dinner":
////                        dinner.add(foodName);
////                        break;
////                }
////            }
////            cursor.close();
////
////            dayPlans.add(new NutritionDayPlan(day, breakfast, lunch, dinner, false));
////        }
////        db.close();
////        return dayPlans;
////    }
////}
//
//package com.example.gogo.ui;
//
//import android.content.ContentValues;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ImageView;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.gogo.R;
//import com.example.gogo.adapters.Nutrient7PlanCardAdapter;
//import com.example.gogo.database.DatabaseHelper;
//import com.example.gogo.models.NutritionDayPlan;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class Nutrient7PlanActivity extends AppCompatActivity {
//    private static final String TAG = "Nutrient7PlanActivity";
//    private DatabaseHelper dbHelper;
//    private int dietId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_nutrient_7plan);
//
//        dbHelper = new DatabaseHelper(this);
//        dietId = getIntent().getIntExtra("dietId", -1);
//        Log.d(TAG, "Received dietId: " + dietId);
//
//        if (dietId == -1) {
//            Log.e(TAG, "Invalid dietId received");
//            finish();
//            return;
//        }
//
//        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//
//        List<NutritionDayPlan> dayPlans = loadDayPlansFromDatabase(dietId);
//        if (dayPlans.isEmpty()) {
//            Log.w(TAG, "No data found for dietId: " + dietId);
//        }
//        Nutrient7PlanCardAdapter adapter = new Nutrient7PlanCardAdapter(dayPlans, dietId, this);
//        recyclerView.setAdapter(adapter);
//
//        ImageView backButton = findViewById(R.id.backButton);
//        backButton.setOnClickListener(v -> finish());
//    }
//
//    private List<NutritionDayPlan> loadDayPlansFromDatabase(int dietId) {
//        List<NutritionDayPlan> dayPlans = new ArrayList<>();
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//
//        // Truy vấn tất cả dữ liệu cho 7 ngày trong một lần
//        Cursor cursor = db.rawQuery(
//                "SELECT dpf.DayNumber, dpf.MealTime, f.FoodName, dpf.Calories, dps.isCompleted " +
//                        "FROM DietPlanFood dpf " +
//                        "JOIN Food f ON dpf.FoodID = f.FoodID " +
//                        "LEFT JOIN DietPlanDayStatus dps ON dpf.DietID = dps.DietID AND dpf.DayNumber = dps.DayNumber " +
//                        "WHERE dpf.DietID = ? AND dpf.DayNumber BETWEEN 1 AND 7 " +
//                        "ORDER BY dpf.DayNumber, dpf.MealTime",
//                new String[]{String.valueOf(dietId)}
//        );
//
//        // Sử dụng Map để nhóm dữ liệu theo ngày
//        Map<Integer, NutritionDayPlan> dayPlanMap = new HashMap<>();
//        for (int day = 1; day <= 7; day++) {
//            dayPlanMap.put(day, new NutritionDayPlan(day, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false));
//        }
//
//        while (cursor.moveToNext()) {
//            int dayNumber = cursor.getInt(0);
//            String mealTime = cursor.getString(1);
//            String foodName = cursor.getString(2);
//            int calories = cursor.getInt(3);
//            boolean isCompleted = cursor.getInt(4) == 1;
//
//            NutritionDayPlan dayPlan = dayPlanMap.get(dayNumber);
//            dayPlan.setCompleted(isCompleted); // Cập nhật trạng thái hoàn thành
//
//            String foodWithCalories = foodName + " (" + calories + " kcal)";
//            switch (mealTime) {
//                case "Breakfast":
//                    dayPlan.getBreakfast().add(foodWithCalories);
//                    break;
//                case "Lunch":
//                    dayPlan.getLunch().add(foodWithCalories);
//                    break;
//                case "Dinner":
//                    dayPlan.getDinner().add(foodWithCalories);
//                    break;
//            }
//        }
//        cursor.close();
//        db.close();
//
//        // Chuyển Map thành List
//        dayPlans.addAll(dayPlanMap.values());
//        return dayPlans;
//    }
//}
//
//package com.example.gogo.ui;
//
//import android.content.ContentValues;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ImageView;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.gogo.R;
//import com.example.gogo.adapters.Nutrient7PlanCardAdapter;
//import com.example.gogo.database.DatabaseHelper;
//import com.example.gogo.models.NutritionDayPlan;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class Nutrient7PlanActivity extends AppCompatActivity {
//    private static final String TAG = "Nutrient7PlanActivity";
//    private DatabaseHelper dbHelper;
//    private int dietId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_nutrient_7plan);
//
//        dbHelper = new DatabaseHelper(this);
//        dietId = getIntent().getIntExtra("dietId", -1);
//        Log.d(TAG, "Received dietId: " + dietId);
//
//        if (dietId == -1) {
//            Log.e(TAG, "Invalid dietId received");
//            finish();
//            return;
//        }
//
//        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//
//        List<NutritionDayPlan> dayPlans = loadDayPlansFromDatabase(dietId);
//        if (dayPlans.isEmpty()) {
//            Log.w(TAG, "No data found for dietId: " + dietId);
//        }
//        Nutrient7PlanCardAdapter adapter = new Nutrient7PlanCardAdapter(dayPlans, dietId, this);
//        recyclerView.setAdapter(adapter);
//
//        ImageView backButton = findViewById(R.id.backButton);
//        backButton.setOnClickListener(v -> finish());
//    }
//
//    private List<NutritionDayPlan> loadDayPlansFromDatabase(int dietId) {
//        List<NutritionDayPlan> dayPlans = new ArrayList<>();
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//
//        Cursor cursor = db.rawQuery(
//                "SELECT dpf.DayNumber, dpf.MealTime, f.FoodName, dpf.Calories, dps.isCompleted " +
//                        "FROM DietPlanFood dpf " +
//                        "JOIN Food f ON dpf.FoodID = f.FoodID " +
//                        "LEFT JOIN DietPlanDayStatus dps ON dpf.DietID = dps.DietID AND dpf.DayNumber = dps.DayNumber " +
//                        "WHERE dpf.DietID = ? AND dpf.DayNumber BETWEEN 1 AND 7 " +
//                        "ORDER BY dpf.DayNumber, dpf.MealTime",
//                new String[]{String.valueOf(dietId)}
//        );
//
//        // Khởi tạo Map cho 7 ngày
//        Map<Integer, NutritionDayPlan> dayPlanMap = new HashMap<>();
//        for (int day = 1; day <= 7; day++) {
//            dayPlanMap.put(day, new NutritionDayPlan(day, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false));
//        }
//
//        while (cursor.moveToNext()) {
//            int dayNumber = cursor.getInt(0);
//            String mealTime = cursor.getString(1);
//            String foodName = cursor.getString(2);
//            int calories = cursor.getInt(3);
//            boolean isCompleted = cursor.getInt(4) == 1;
//
//            NutritionDayPlan dayPlan = dayPlanMap.get(dayNumber);
//            dayPlan.setCompleted(isCompleted);
//
//            String foodWithCalories = foodName + " (" + calories + " kcal)";
//            switch (mealTime) {
//                case "Breakfast":
//                    dayPlan.getBreakfast().add(foodWithCalories);
//                    break;
//                case "Lunch":
//                    dayPlan.getLunch().add(foodWithCalories);
//                    break;
//                case "Dinner":
//                    dayPlan.getDinner().add(foodWithCalories);
//                    break;
//            }
//            // Cộng dồn calo
//            dayPlan.addCalories(calories);
//        }
//        cursor.close();
//        db.close();
//
//        // Kiểm tra và thêm "Nhịn" nếu bữa ăn trống, cập nhật tổng calo
//        for (NutritionDayPlan dayPlan : dayPlanMap.values()) {
//            if (dayPlan.getBreakfast().isEmpty()) {
//                dayPlan.getBreakfast().add("Nhịn");
//            }
//            if (dayPlan.getLunch().isEmpty()) {
//                dayPlan.getLunch().add("Nhịn");
//            }
//            if (dayPlan.getDinner().isEmpty()) {
//                dayPlan.getDinner().add("Nhịn");
//            }
//            dayPlans.add(dayPlan);
//        }
//
//        return dayPlans;
//    }
//}
package com.example.gogo.ui;

import android.content.ContentValues;
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
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.NutritionDayPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nutrient7PlanActivity extends AppCompatActivity {
    private static final String TAG = "Nutrient7PlanActivity";
    private DatabaseHelper dbHelper;
    private int dietId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrient_7plan);

        dbHelper = new DatabaseHelper(this);
        dietId = getIntent().getIntExtra("dietId", -1);
        userId = getIntent().getIntExtra("userId", 1); // Giả sử userId được truyền từ Activity trước
        Log.d(TAG, "Received dietId: " + dietId + ", userId: " + userId);

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

        // Xử lý button "Chọn kế hoạch"
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
            // Xóa các kế hoạch đã chọn trước đó của userId
            int deletedRows = db.delete("SelectedDietPlan", "UserID = ?", new String[]{String.valueOf(userId)});
            Log.d(TAG, "Deleted " + deletedRows + " previous selections for UserID " + userId);

            // Thêm kế hoạch mới được chọn
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