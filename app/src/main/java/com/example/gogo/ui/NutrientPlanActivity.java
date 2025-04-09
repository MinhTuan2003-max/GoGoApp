package com.example.gogo.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.adapters.DietPlanAdapter;
import com.example.gogo.database.AccountDAO;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.DietPlan;
import com.example.gogo.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class NutrientPlanActivity extends AppCompatActivity {
    private static final String TAG = "NutrientPlanActivity";
    private DatabaseHelper dbHelper;
    private AccountDAO accountDAO;
    private BottomNavigationView bottomNavigationView;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrient_sugget_plan);

        dbHelper = new DatabaseHelper(this);
        accountDAO = new AccountDAO(dbHelper);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView == null) {
            Log.e(TAG, "BottomNavigationView not found in layout!");
            Toast.makeText(this, "Không tìm thấy BottomNavigationView trong layout!", Toast.LENGTH_SHORT).show();
        }

        Intent intent = getIntent();
        userId = intent.getIntExtra("USER_ID", -1);
        Log.d(TAG, "Received userId from Intent: " + userId);
        if (userId == -1) {
            loadUserData();
            Log.d(TAG, "Loaded userId from Google Sign-In: " + userId);
        }

        if (userId == -1) {
            Log.e(TAG, "Không tìm thấy userId trong Intent hoặc Google Sign-In");
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(this, MainActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<DietPlan> dietPlans = loadDietPlansFromDatabase(userId);
        if (dietPlans.isEmpty()) {
            Log.w(TAG, "No diet plans found for userId: " + userId);
            Toast.makeText(this, "Không tìm thấy kế hoạch nào!", Toast.LENGTH_SHORT).show();
        }

        DietPlanAdapter adapter = new DietPlanAdapter(this, dietPlans, dietPlan -> {
            Intent detailIntent = new Intent(NutrientPlanActivity.this, Nutrient7PlanActivity.class);
            detailIntent.putExtra("dietId", dietPlan.getDietId());
            detailIntent.putExtra("USER_ID", userId);
            startActivity(detailIntent);
            Log.d(TAG, "Navigating to Nutrient7PlanActivity with dietId: " + dietPlan.getDietId() + ", userId: " + userId);
        });
        recyclerView.setAdapter(adapter);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(NutrientPlanActivity.this, NutrientSelectActivity.class);
            backIntent.putExtra("USER_ID", userId);
            startActivity(backIntent);
            finish();
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

    private List<DietPlan> loadDietPlansFromDatabase(int userId) {
        List<DietPlan> dietPlans = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.d(TAG, "Database opened: " + db.isOpen());

        try {
            User user = accountDAO.getUserById(userId);
            if (user == null) {
                Log.e(TAG, "Không tìm thấy User với userId: " + userId);
                return dietPlans;
            }

            Cursor cursor = db.rawQuery(
                    "SELECT DietID, UserID, PlanName, Description FROM DietPlan WHERE UserID = ? LIMIT 10",
                    new String[]{String.valueOf(userId)});
            Log.d(TAG, "Executing query for DietPlan with UserID: " + userId);

            while (cursor.moveToNext()) {
                int dietId = cursor.getInt(0);
                int fetchedUserId = cursor.getInt(1);
                String planName = cursor.getString(2);
                String description = cursor.getString(3);

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

                dietPlans.add(new DietPlan(dietId, user, planName, false, updatedDescription, null));
                Log.d(TAG, "Loaded DietPlan - DietID: " + dietId + ", PlanName: " + planName + ", AvgCalories: " + avgCaloriesPerDay);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi tải danh sách kế hoạch: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khi tải kế hoạch: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }

        return dietPlans;
    }

    private void setupBottomNavigation() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_profile) {
                    Intent profileIntent = new Intent(NutrientPlanActivity.this, ViewProfileActivity.class);
                    profileIntent.putExtra("USER_ID", userId);
                    startActivity(profileIntent);
                    return true;
                } else if (itemId == R.id.nav_home) {
                    Intent homeIntent = new Intent(NutrientPlanActivity.this, HomeActivity.class);
                    homeIntent.putExtra("USER_ID", userId);
                    startActivity(homeIntent);
                    return true;
                } else if (itemId == R.id.nav_settings) {
                    Intent settingsIntent = new Intent(NutrientPlanActivity.this, SettingActivity.class);
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
}