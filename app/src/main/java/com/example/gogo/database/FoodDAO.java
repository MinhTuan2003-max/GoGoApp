package com.example.gogo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gogo.models.Food;
import com.example.gogo.models.Nutrition;

import java.util.ArrayList;
import java.util.List;

public class FoodDAO {
    private DatabaseHelper dbHelper;
    private NutritionDAO nutritionDAO;

    public FoodDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.nutritionDAO = new NutritionDAO(dbHelper);
    }

    public long insertFood(Food food) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("FoodName", food.getName());
        long id = db.insert("Food", null, values);
        db.close();
        return id;
    }

    public boolean updateFood(Food food) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("FoodName", food.getName());
        int rows = db.update("Food", values, "FoodID = ?", new String[]{String.valueOf(food.getFoodId())});
        db.close();
        return rows > 0;
    }

    public boolean deleteFood(int foodId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("Food", "FoodID = ?", new String[]{String.valueOf(foodId)});
        db.close();
        return rows > 0;
    }

    public boolean isFoodExists(String name) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Food WHERE FoodName = ?", new String[]{name});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public List<Food> getAllFoods() {
        List<Food> foods = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Food ORDER BY FoodID DESC", null);
        if (cursor.moveToFirst()) {
            do {
                int foodId = cursor.getInt(cursor.getColumnIndexOrThrow("FoodID"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("FoodName"));
                List<Nutrition> nutritionList = nutritionDAO.getNutritionsByFoodId(foodId);
                Food food = new Food(foodId, name, nutritionList);
                foods.add(food);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return foods;
    }

    public Food getFoodById(int foodId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Food WHERE FoodID = ?", new String[]{String.valueOf(foodId)});
        Food food = null;
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("FoodName"));
            List<Nutrition> nutritionList = nutritionDAO.getNutritionsByFoodId(foodId);
            food = new Food(foodId, name, nutritionList);
        }
        cursor.close();
        db.close();
        return food;
    }
}
