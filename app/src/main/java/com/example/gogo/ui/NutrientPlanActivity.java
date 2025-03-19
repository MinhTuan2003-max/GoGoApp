package com.example.gogo.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
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
        Log.d(TAG, "NutrientPlanActivity onCreate started");
        try {
            setContentView(R.layout.activity_nutrient_sugget_plan);
            Log.d(TAG, "Layout set successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting layout: " + e.getMessage());
            Toast.makeText(this, "Lỗi khi tải giao diện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);

        int userId = getIntent().getIntExtra("userId", 1);
        Log.d(TAG, "Received userId: " + userId);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<DietPlan> dietPlans = loadDietPlansFromDatabase(userId);
        if (dietPlans.isEmpty()) {
            Log.w(TAG, "No diet plans found for userId: " + userId);
            Toast.makeText(this, "Không có kế hoạch ăn uống nào", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "Loaded " + dietPlans.size() + " diet plans with DietIDs: " + getDietIds(dietPlans));
        }

        DietPlanAdapter adapter = new DietPlanAdapter(this, dietPlans, dietPlan -> {
            if (dietPlan != null && dietPlan.getDietId() > 0) {
                Log.d(TAG, "User clicked on plan: DietID=" + dietPlan.getDietId() + ", PlanName=" + dietPlan.getPlanName());
                Intent intent = new Intent(NutrientPlanActivity.this, Nutrient7PlanActivity.class);
                intent.putExtra("dietId", dietPlan.getDietId());
                Log.d(TAG, "Navigating to Nutrient7PlanActivity with dietId: " + dietPlan.getDietId());
                try {
                    startActivity(intent);
                    Log.d(TAG, "startActivity called successfully");
                } catch (Exception e) {
                    Log.e(TAG, "Error starting Nutrient7PlanActivity: " + e.getMessage());
                    Toast.makeText(this, "Lỗi khi mở Nutrient7PlanActivity", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Invalid dietPlan or dietId: " + (dietPlan != null ? dietPlan.getDietId() : "null"));
            }
        });
        recyclerView.setAdapter(adapter);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private List<DietPlan> loadDietPlansFromDatabase(int userId) {
        List<DietPlan> dietPlans = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT DietID, UserID, PlanName, isCompleted, Description, CreatedAt FROM DietPlan WHERE UserID = ? LIMIT 10",
                new String[]{String.valueOf(userId)});

        Log.d(TAG, "Number of diet plans in database: " + cursor.getCount());
        while (cursor.moveToNext()) {
            int dietId = cursor.getInt(0);
            int retrievedUserId = cursor.getInt(1);
            String planName = cursor.getString(2);
            boolean isCompleted = cursor.getInt(3) == 1;
            String description = cursor.getString(4);
            String createdAt = cursor.getString(5);
            Log.d(TAG, "Loaded plan: DietID=" + dietId + ", PlanName=" + planName + ", Description=" + description);
            dietPlans.add(new DietPlan(dietId, retrievedUserId, planName, isCompleted, description, createdAt));
        }
        cursor.close();
        return dietPlans;
    }

    private String getDietIds(List<DietPlan> plans) {
        StringBuilder sb = new StringBuilder();
        for (DietPlan plan : plans) {
            sb.append(plan.getDietId()).append(", ");
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 2) : "none";
    }
}