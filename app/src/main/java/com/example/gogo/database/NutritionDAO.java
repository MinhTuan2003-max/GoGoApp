package com.example.gogo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gogo.models.Nutrition;

import java.util.ArrayList;
import java.util.List;

public class NutritionDAO {
    private DatabaseHelper dbHelper;

    public NutritionDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long insertNutrition(Nutrition nutrition) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("FoodID", nutrition.getFoodId());
        values.put("Carbohydrate", nutrition.getCarbohydrate());
        values.put("Protein", nutrition.getProtein());
        values.put("Fat", nutrition.getFat());
        values.put("Fiber", nutrition.getFiber());
        values.put("Sugars", nutrition.getSugars());
        values.put("VitaminA", nutrition.getVitaminA());
        values.put("VitaminB", nutrition.getVitaminB());
        values.put("VitaminC", nutrition.getVitaminC());
        values.put("VitaminD", nutrition.getVitaminD());
        values.put("VitaminE", nutrition.getVitaminE());
        values.put("VitaminK", nutrition.getVitaminK());
        values.put("Calcium", nutrition.getCalcium());
        values.put("Iron", nutrition.getIron());
        values.put("Potassium", nutrition.getPotassium());
        values.put("Calories", nutrition.getCalories());
        long id = db.insert("Nutrition", null, values);
        db.close();
        return id;
    }

    public boolean deleteNutritionsByFoodId(int foodId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("Nutrition", "FoodID = ?", new String[]{String.valueOf(foodId)});
        db.close();
        return rows > 0;
    }

    public List<Nutrition> getNutritionsByFoodId(int foodId) {
        List<Nutrition> nutritions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Nutrition WHERE FoodID = ?", new String[]{String.valueOf(foodId)});
        if (cursor.moveToFirst()) {
            do {
                Nutrition nutrition = new Nutrition();
                nutrition.setNutritionId(cursor.getInt(cursor.getColumnIndexOrThrow("NutritionID")));
                nutrition.setFoodId(cursor.getInt(cursor.getColumnIndexOrThrow("FoodID")));
                nutrition.setCarbohydrate(cursor.getDouble(cursor.getColumnIndexOrThrow("Carbohydrate")));
                nutrition.setProtein(cursor.getDouble(cursor.getColumnIndexOrThrow("Protein")));
                nutrition.setFat(cursor.getDouble(cursor.getColumnIndexOrThrow("Fat")));
                nutrition.setFiber(cursor.getDouble(cursor.getColumnIndexOrThrow("Fiber")));
                nutrition.setSugars(cursor.getDouble(cursor.getColumnIndexOrThrow("Sugars")));
                nutrition.setVitaminA(cursor.getDouble(cursor.getColumnIndexOrThrow("VitaminA")));
                nutrition.setVitaminB(cursor.getDouble(cursor.getColumnIndexOrThrow("VitaminB")));
                nutrition.setVitaminC(cursor.getDouble(cursor.getColumnIndexOrThrow("VitaminC")));
                nutrition.setVitaminD(cursor.getDouble(cursor.getColumnIndexOrThrow("VitaminD")));
                nutrition.setVitaminE(cursor.getDouble(cursor.getColumnIndexOrThrow("VitaminE")));
                nutrition.setVitaminK(cursor.getDouble(cursor.getColumnIndexOrThrow("VitaminK")));
                nutrition.setCalcium(cursor.getDouble(cursor.getColumnIndexOrThrow("Calcium")));
                nutrition.setIron(cursor.getDouble(cursor.getColumnIndexOrThrow("Iron")));
                nutrition.setPotassium(cursor.getDouble(cursor.getColumnIndexOrThrow("Potassium")));
                nutrition.setCalories(cursor.getInt(cursor.getColumnIndexOrThrow("Calories")));
                nutritions.add(nutrition);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return nutritions;
    }
}