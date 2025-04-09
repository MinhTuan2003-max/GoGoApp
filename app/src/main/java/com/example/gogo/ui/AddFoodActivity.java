package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gogo.R;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.database.FoodDAO;
import com.example.gogo.database.NutritionDAO;
import com.example.gogo.models.Food;
import com.example.gogo.models.Nutrition;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AddFoodActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private FoodDAO foodDAO;
    private NutritionDAO nutritionDAO;
    private MaterialToolbar toolbar;
    private TextInputEditText foodNameInput, carbInput, proteinInput, fatInput, caloriesInput;
    private MaterialButton addFoodButton;
    private boolean isUpdate = false;
    private int foodId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        databaseHelper = new DatabaseHelper(this);
        foodDAO = new FoodDAO(databaseHelper);
        nutritionDAO = new NutritionDAO(databaseHelper);

        toolbar = findViewById(R.id.toolbar);
        foodNameInput = findViewById(R.id.food_name_input);
        carbInput = findViewById(R.id.carb_input);
        proteinInput = findViewById(R.id.protein_input);
        fatInput = findViewById(R.id.fat_input);
        caloriesInput = findViewById(R.id.calories_input);
        addFoodButton = findViewById(R.id.add_food_button);

        toolbar.setTitle("Add New Food");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        Intent intent = getIntent();
        isUpdate = intent.getBooleanExtra("IS_UPDATE", false);
        foodId = intent.getIntExtra("FOOD_ID", -1);
        if (isUpdate && foodId != -1) {
            loadFoodData(foodId);
            toolbar.setTitle("Update Food");
            addFoodButton.setText("Update Food");
        }

        addFoodButton.setOnClickListener(v -> saveFood());
    }

    private void loadFoodData(int foodId) {
        Food food = foodDAO.getFoodById(foodId);
        if (food != null) {
            foodNameInput.setText(food.getName());
            List<Nutrition> nutritions = food.getNutritionList();
            if (!nutritions.isEmpty()) {
                Nutrition nutrition = nutritions.get(0);
                carbInput.setText(String.valueOf(nutrition.getCarbohydrate()));
                proteinInput.setText(String.valueOf(nutrition.getProtein()));
                fatInput.setText(String.valueOf(nutrition.getFat()));
                caloriesInput.setText(String.valueOf(nutrition.getCalories()));
            }
        }
    }

    private void saveFood() {
        String foodName = foodNameInput.getText().toString().trim();
        String carbStr = carbInput.getText().toString().trim();
        String proteinStr = proteinInput.getText().toString().trim();
        String fatStr = fatInput.getText().toString().trim();
        String caloriesStr = caloriesInput.getText().toString().trim();

        if (TextUtils.isEmpty(foodName)) {
            Toast.makeText(this, "Please enter food name", Toast.LENGTH_SHORT).show();
            return;
        }

        double carbs = TextUtils.isEmpty(carbStr) ? 0 : Double.parseDouble(carbStr);
        double protein = TextUtils.isEmpty(proteinStr) ? 0 : Double.parseDouble(proteinStr);
        double fat = TextUtils.isEmpty(fatStr) ? 0 : Double.parseDouble(fatStr);
        int calories = TextUtils.isEmpty(caloriesStr) ? 0 : Integer.parseInt(caloriesStr);

        Food food = new Food();
        food.setName(foodName);

        Nutrition nutrition = new Nutrition();
        nutrition.setCarbohydrate(carbs);
        nutrition.setProtein(protein);
        nutrition.setFat(fat);
        nutrition.setCalories(calories);

        List<Nutrition> nutritionList = new ArrayList<>();
        nutritionList.add(nutrition);
        food.setNutritionList(nutritionList);

        if (isUpdate) {
            if (foodDAO.isFoodExists(foodName)) {
                Toast.makeText(this, "Food already exists in database", Toast.LENGTH_SHORT).show();
                return;
            }

            food.setFoodId(foodId);
            boolean success = foodDAO.updateFood(food);
            if (success) {
                nutritionDAO.deleteNutritionsByFoodId(foodId);
                nutrition.setFoodId(foodId);
                nutritionDAO.insertNutrition(nutrition);
                Toast.makeText(this, "Food updated successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to update food", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (foodDAO.isFoodExists(foodName)) {
                Toast.makeText(this, "Food already exists in database", Toast.LENGTH_SHORT).show();
                return;
            }
            long newFoodId = foodDAO.insertFood(food);
            if (newFoodId != -1) {
                nutrition.setFoodId((int) newFoodId);
                nutritionDAO.insertNutrition(nutrition);
                Toast.makeText(this, "Food added successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to add food", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}