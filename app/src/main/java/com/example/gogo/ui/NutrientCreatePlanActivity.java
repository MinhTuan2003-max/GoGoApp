package com.example.gogo.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gogo.R;
import com.example.gogo.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class NutrientCreatePlanActivity extends AppCompatActivity {
    private static final String TAG = "NutrientCreatePlanActivity";
    private DatabaseHelper dbHelper;
    private int userId;
    private List<DayPlan> dayPlans = new ArrayList<>();
    private List<String> foodList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "NutrientCreatePlanActivity onCreate called");
        setContentView(R.layout.activity_nutrient_create_plan);

        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", 1);
        Log.d(TAG, "Received userId: " + userId);

        loadFoodList();

        for (int day = 1; day <= 7; day++) {
            dayPlans.add(new DayPlan(day));
        }

        RecyclerView daysRecyclerView = findViewById(R.id.daysRecyclerView);
        daysRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DayPlanAdapter adapter = new DayPlanAdapter(dayPlans);
        daysRecyclerView.setAdapter(adapter);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        Button saveButton = findViewById(R.id.savePlanButton);
        saveButton.setOnClickListener(v -> {
            Log.d(TAG, "Save Plan button clicked");
            savePlanToDatabase();
        });
    }

    private void loadFoodList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT FoodID, FoodName FROM Food", null);
        foodList.clear();
        foodList.add("Nhịn");
        while (cursor.moveToNext()) {
            foodList.add(cursor.getString(1));
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Loaded food list: " + foodList.size() + " items");
    }

    private void savePlanToDatabase() {
        Log.d(TAG, "savePlanToDatabase() called");
        if (isPlanEmpty()) {
            Toast.makeText(this, "Vui lòng thêm ít nhất một món ăn vào kế hoạch!", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            Cursor userCheck = db.rawQuery("SELECT UserID FROM Users WHERE UserID = ?", new String[]{String.valueOf(userId)});
            if (!userCheck.moveToFirst()) {
                Log.e(TAG, "UserID " + userId + " không tồn tại trong bảng Users");
                Toast.makeText(this, "Người dùng không tồn tại!", Toast.LENGTH_SHORT).show();
                userCheck.close();
                return;
            }
            userCheck.close();
            Log.d(TAG, "Verified UserID " + userId + " exists in Users table");

            String dietPlanSql = "INSERT INTO DietPlan (UserID, PlanName, Description) VALUES (" + userId + ", 'Kế hoạch tự tạo', 'Kế hoạch ăn uống 7 ngày do người dùng tự tạo');";
            Log.d(TAG, "Executing SQL: " + dietPlanSql);
            db.execSQL(dietPlanSql);
            Cursor dietCursor = db.rawQuery("SELECT last_insert_rowid()", null);
            long dietId = -1;
            if (dietCursor.moveToFirst()) {
                dietId = dietCursor.getLong(0);
            }
            dietCursor.close();
            Log.d(TAG, "Inserted DietPlan with dietId: " + dietId);
            if (dietId <= 0) {
                Log.e(TAG, "Không thể tạo kế hoạch mới trong bảng DietPlan");
                Toast.makeText(this, "Lỗi khi tạo kế hoạch mới", Toast.LENGTH_SHORT).show();
                return;
            }

            int mealsInserted = 0;
            for (DayPlan dayPlan : dayPlans) {
                mealsInserted += saveMeal(db, dietId, dayPlan.day, "Breakfast", dayPlan.breakfastFoods);
                mealsInserted += saveMeal(db, dietId, dayPlan.day, "Lunch", dayPlan.lunchFoods);
                mealsInserted += saveMeal(db, dietId, dayPlan.day, "Dinner", dayPlan.dinnerFoods);

                String dayStatusSql = "INSERT INTO DietPlanDayStatus (DietID, DayNumber, isCompleted) VALUES (" + dietId + ", " + dayPlan.day + ", 0);";
                Log.d(TAG, "Executing SQL: " + dayStatusSql);
                db.execSQL(dayStatusSql);
                Log.d(TAG, "Inserted DietPlanDayStatus for day " + dayPlan.day);
            }
            Log.d(TAG, "Total meals inserted: " + mealsInserted);

            String deleteSql = "DELETE FROM SelectedDietPlan WHERE UserID = " + userId + ";";
            Log.d(TAG, "Executing SQL: " + deleteSql);
            db.execSQL(deleteSql);
            Log.d(TAG, "Deleted old rows from SelectedDietPlan for UserID: " + userId);

            String selectedPlanSql = "INSERT INTO SelectedDietPlan (UserID, DietID) VALUES (" + userId + ", " + dietId + ");";
            Log.d(TAG, "Executing SQL: " + selectedPlanSql);
            db.execSQL(selectedPlanSql);
            Log.d(TAG, "Inserted SelectedDietPlan for UserID: " + userId + ", DietID: " + dietId);

            Cursor verifyCursor = db.rawQuery("SELECT DietID FROM SelectedDietPlan WHERE UserID = ? AND DietID = ?",
                    new String[]{String.valueOf(userId), String.valueOf(dietId)});
            if (verifyCursor.moveToFirst()) {
                Log.d(TAG, "Verified: DietID " + dietId + " exists in SelectedDietPlan for UserID " + userId);
            } else {
                Log.e(TAG, "Failed to verify: DietID " + dietId + " not found in SelectedDietPlan");
            }
            verifyCursor.close();

            db.setTransactionSuccessful();
            Toast.makeText(this, "Đã tạo và chọn kế hoạch thành công!", Toast.LENGTH_SHORT).show();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("dietId", dietId); // dietId là long
            setResult(RESULT_OK, resultIntent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lưu kế hoạch: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khi lưu kế hoạch: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null) {
                if (db.inTransaction()) {
                    db.endTransaction();
                }
                db.close();
            }
        }
    }

    private boolean isPlanEmpty() {
        for (DayPlan dayPlan : dayPlans) {
            for (String food : dayPlan.breakfastFoods) {
                if (!food.equals("Nhịn")) return false;
            }
            for (String food : dayPlan.lunchFoods) {
                if (!food.equals("Nhịn")) return false;
            }
            for (String food : dayPlan.dinnerFoods) {
                if (!food.equals("Nhịn")) return false;
            }
        }
        return true;
    }

    private int saveMeal(SQLiteDatabase db, long dietId, int day, String mealTime, List<String> foods) {
        int insertedCount = 0;
        for (String foodName : foods) {
            if (!foodName.equals("Nhịn")) {
                Cursor cursor = null;
                try {
                    cursor = db.rawQuery(
                            "SELECT Food.FoodID, Nutrition.Calories FROM Food JOIN Nutrition ON Food.FoodID = Nutrition.FoodID WHERE Food.FoodName = ?",
                            new String[]{foodName});
                    if (cursor.moveToFirst()) {
                        int foodId = cursor.getInt(0);
                        int calories = cursor.getInt(1);

                        String foodSql = "INSERT INTO DietPlanFood (DietID, DayNumber, MealTime, FoodID, Calories) VALUES (" +
                                dietId + ", " + day + ", '" + mealTime + "', " + foodId + ", " + calories + ");";
                        Log.d(TAG, "Executing SQL: " + foodSql);
                        db.execSQL(foodSql);
                        Log.d(TAG, "Inserted food '" + foodName + "' into DietPlanFood for day " + day + ", meal: " + mealTime);
                        insertedCount++;
                    } else {
                        Log.e(TAG, "Không tìm thấy món ăn '" + foodName + "' trong Food hoặc Nutrition");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Lỗi khi lưu món '" + foodName + "' vào DietPlanFood: " + e.getMessage(), e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }
        return insertedCount;
    }

    private static class DayPlan {
        int day;
        List<String> breakfastFoods = new ArrayList<>();
        List<String> lunchFoods = new ArrayList<>();
        List<String> dinnerFoods = new ArrayList<>();

        DayPlan(int day) {
            this.day = day;
        }
    }

    private class DayPlanAdapter extends RecyclerView.Adapter<DayPlanAdapter.DayViewHolder> {
        private List<DayPlan> dayPlans;

        DayPlanAdapter(List<DayPlan> dayPlans) {
            this.dayPlans = dayPlans;
        }

        @Override
        public DayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day_plan, parent, false);
            return new DayViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DayViewHolder holder, int position) {
            DayPlan dayPlan = dayPlans.get(position);
            holder.bind(dayPlan);
        }

        @Override
        public int getItemCount() {
            return dayPlans.size();
        }

        class DayViewHolder extends RecyclerView.ViewHolder {
            TextView dayTitle;
            RecyclerView breakfastRecyclerView, lunchRecyclerView, dinnerRecyclerView;
            Button addBreakfastButton, addLunchButton, addDinnerButton;
            FoodAdapter breakfastAdapter, lunchAdapter, dinnerAdapter;

            DayViewHolder(View itemView) {
                super(itemView);
                dayTitle = itemView.findViewById(R.id.dayTitle);
                breakfastRecyclerView = itemView.findViewById(R.id.breakfastRecyclerView);
                lunchRecyclerView = itemView.findViewById(R.id.lunchRecyclerView);
                dinnerRecyclerView = itemView.findViewById(R.id.dinnerRecyclerView);
                addBreakfastButton = itemView.findViewById(R.id.addBreakfastButton);
                addLunchButton = itemView.findViewById(R.id.addLunchButton);
                addDinnerButton = itemView.findViewById(R.id.addDinnerButton);

                breakfastRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                lunchRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                dinnerRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            }

            void bind(DayPlan dayPlan) {
                dayTitle.setText("Ngày " + dayPlan.day);

                breakfastAdapter = new FoodAdapter(dayPlan.breakfastFoods);
                lunchAdapter = new FoodAdapter(dayPlan.lunchFoods);
                dinnerAdapter = new FoodAdapter(dayPlan.dinnerFoods);

                breakfastRecyclerView.setAdapter(breakfastAdapter);
                lunchRecyclerView.setAdapter(lunchAdapter);
                dinnerRecyclerView.setAdapter(dinnerAdapter);

                addBreakfastButton.setOnClickListener(v -> {
                    dayPlan.breakfastFoods.add("Nhịn");
                    breakfastAdapter.notifyItemInserted(dayPlan.breakfastFoods.size() - 1);
                });
                addLunchButton.setOnClickListener(v -> {
                    dayPlan.lunchFoods.add("Nhịn");
                    lunchAdapter.notifyItemInserted(dayPlan.lunchFoods.size() - 1);
                });
                addDinnerButton.setOnClickListener(v -> {
                    dayPlan.dinnerFoods.add("Nhịn");
                    dinnerAdapter.notifyItemInserted(dayPlan.dinnerFoods.size() - 1);
                });
            }
        }
    }

    private class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
        private List<String> foods;

        FoodAdapter(List<String> foods) {
            this.foods = foods;
        }

        @Override
        public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_selection, parent, false);
            return new FoodViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FoodViewHolder holder, int position) {
            holder.bind(foods.get(position), position);
        }

        @Override
        public int getItemCount() {
            return foods.size();
        }

        class FoodViewHolder extends RecyclerView.ViewHolder {
            Spinner foodSpinner;
            Button removeButton;

            FoodViewHolder(View itemView) {
                super(itemView);
                foodSpinner = itemView.findViewById(R.id.foodSpinner);
                removeButton = itemView.findViewById(R.id.removeButton);
            }

            void bind(String food, int position) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item, foodList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                foodSpinner.setAdapter(adapter);
                foodSpinner.setSelection(foodList.indexOf(food));
                foodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        foods.set(position, foodList.get(pos));
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });

                removeButton.setOnClickListener(v -> {
                    foods.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, foods.size());
                });
            }
        }
    }
}