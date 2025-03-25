//package com.example.gogo.ui;
//
//import android.content.Intent;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ImageView;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.gogo.R;
//import com.example.gogo.adapters.DietPlanAdapter;
//import com.example.gogo.database.DatabaseHelper;
//import com.example.gogo.models.DietPlan;
//import java.util.ArrayList;
//import java.util.List;
//
//public class NutrientPlanActivity extends AppCompatActivity {
//    private static final String TAG = "NutrientPlanActivity";
//    private DatabaseHelper dbHelper;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_nutrient_sugget_plan);
//
//        dbHelper = new DatabaseHelper(this);
//        int userId = getIntent().getIntExtra("userId", 1);
//
//        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        List<DietPlan> dietPlans = loadDietPlansFromDatabase(userId);
//        DietPlanAdapter adapter = new DietPlanAdapter(this, dietPlans, dietPlan -> {
//            Intent intent = new Intent(NutrientPlanActivity.this, Nutrient7PlanActivity.class);
//            intent.putExtra("dietId", dietPlan.getDietId());
//            startActivity(intent);
//        });
//        recyclerView.setAdapter(adapter);
//
//        ImageView backButton = findViewById(R.id.backButton);
//        backButton.setOnClickListener(v -> finish());
//    }
//
//    private List<DietPlan> loadDietPlansFromDatabase(int userId) {
//        List<DietPlan> dietPlans = new ArrayList<>();
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//
//        Cursor cursor = db.rawQuery(
//                "SELECT DietID, UserID, PlanName, Description FROM DietPlan WHERE UserID = ? LIMIT 10",
//                new String[]{String.valueOf(userId)});
//
//        while (cursor.moveToNext()) {
//            int dietId = cursor.getInt(0);
//            String planName = cursor.getString(2);
//            String description = cursor.getString(3);
//            dietPlans.add(new DietPlan(dietId, userId, planName, false, description, null));
//        }
//        cursor.close();
//        return dietPlans;
//    }
////}
//package com.example.gogo.ui;
//
//import android.content.Intent;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ImageView;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.gogo.R;
//import com.example.gogo.adapters.DietPlanAdapter;
//import com.example.gogo.database.DatabaseHelper;
//import com.example.gogo.models.DietPlan;
//import java.util.ArrayList;
//import java.util.List;
//
//public class NutrientPlanActivity extends AppCompatActivity {
//    private static final String TAG = "NutrientPlanActivity";
//    private DatabaseHelper dbHelper;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_nutrient_sugget_plan);
//
//        dbHelper = new DatabaseHelper(this);
//        int userId = getIntent().getIntExtra("userId", 1);
//
//        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        List<DietPlan> dietPlans = loadDietPlansFromDatabase(userId);
//        DietPlanAdapter adapter = new DietPlanAdapter(this, dietPlans, dietPlan -> {
//            Intent intent = new Intent(NutrientPlanActivity.this, Nutrient7PlanActivity.class);
//            intent.putExtra("dietId", dietPlan.getDietId());
//            startActivity(intent);
//        });
//        recyclerView.setAdapter(adapter);
//
//        ImageView backButton = findViewById(R.id.backButton);
//        backButton.setOnClickListener(v -> finish());
//    }
//
//    private List<DietPlan> loadDietPlansFromDatabase(int userId) {
//        List<DietPlan> dietPlans = new ArrayList<>();
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//
//        Cursor cursor = db.rawQuery(
//                "SELECT DietID, UserID, PlanName, Description FROM DietPlan WHERE UserID = ? LIMIT 10",
//                new String[]{String.valueOf(userId)});
//
//        while (cursor.moveToNext()) {
//            int dietId = cursor.getInt(0);
//            int fetchedUserId = cursor.getInt(1);
//            String planName = cursor.getString(2);
//            String description = cursor.getString(3);
//            dietPlans.add(new DietPlan(dietId, fetchedUserId, planName, false, description, null));
//        }
//        cursor.close();
//        db.close();
//        return dietPlans;
//    }
//}
package com.example.gogo.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gogo.R;
import com.example.gogo.adapters.DietPlanAdapter;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.DietPlan;
import java.util.ArrayList;
import java.util.List;

public class NutrientPlanActivity extends AppCompatActivity {
    private static final String TAG = "NutrientPlanActivity";
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrient_sugget_plan);

        dbHelper = new DatabaseHelper(this);
        int userId = getIntent().getIntExtra("userId", 1);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<DietPlan> dietPlans = loadDietPlansFromDatabase(userId);
        DietPlanAdapter adapter = new DietPlanAdapter(this, dietPlans, dietPlan -> {
            Intent intent = new Intent(NutrientPlanActivity.this, Nutrient7PlanActivity.class);
            intent.putExtra("dietId", dietPlan.getDietId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(NutrientPlanActivity.this, NutrientSelectActivity.class);
            intent.putExtra("userId", userId); // Truyền userId trở lại nếu cần
            startActivity(intent);
            finish(); // Kết thúc NutrientPlanActivity để không quay lại khi nhấn nút back trên thiết bị
        });
    }

    private List<DietPlan> loadDietPlansFromDatabase(int userId) {
        List<DietPlan> dietPlans = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT DietID, UserID, PlanName, Description FROM DietPlan WHERE UserID = ? LIMIT 10",
                new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            int dietId = cursor.getInt(0);
            int fetchedUserId = cursor.getInt(1);
            String planName = cursor.getString(2);
            String description = cursor.getString(3);

            // Tính tổng calo trung bình mỗi ngày
            Cursor caloriesCursor = db.rawQuery(
                    "SELECT SUM(Calories) AS TotalCalories FROM DietPlanFood WHERE DietID = ? GROUP BY DayNumber",
                    new String[]{String.valueOf(dietId)});
            int totalCalories = 0;
            int dayCount = 0;
            while (caloriesCursor.moveToNext()) {
                totalCalories += caloriesCursor.getInt(0);
                dayCount++;
            }
            caloriesCursor.close();
            int avgCaloriesPerDay = dayCount > 0 ? totalCalories / dayCount : 0;

            String updatedDescription = description + " Trung bình: " + avgCaloriesPerDay + " kcal/ngày";
            dietPlans.add(new DietPlan(dietId, fetchedUserId, planName, false, updatedDescription, null));
        }
        cursor.close();
        db.close();
        return dietPlans;
    }
}