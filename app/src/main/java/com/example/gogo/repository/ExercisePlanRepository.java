package com.example.gogo.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.Exercise;
import com.example.gogo.models.ExercisePlan;

import java.util.ArrayList;
import java.util.List;

public class ExercisePlanRepository {
    private final DatabaseHelper dbHelper;

    public ExercisePlanRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long addPlan(ExercisePlan plan) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("UserID", plan.getUserID());
        values.put("ExerciseID", plan.getExerciseID());
        values.put("Duration", plan.getDuration());
        values.put("CaloriesBurned", plan.getCaloriesBurned());
        values.put("PlanName", plan.getPlanName());
        long id = db.insert("ExercisePlan", null, values);
        dbHelper.close();
        return id;
    }

    public List<ExercisePlan> getPlansByUserId(int userId) {
        List<ExercisePlan> plans = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM ExercisePlan WHERE UserID = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                plans.add(new ExercisePlan(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getInt(4),
                        cursor.getString(5),
                        cursor.getString(6)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return plans;
    }

    public List<ExercisePlan> getPlansByPlanName(String planName, int userId) {
        List<ExercisePlan> plans = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM ExercisePlan WHERE PlanName = ? AND UserID = ?",
                new String[]{planName, String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                plans.add(new ExercisePlan(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getInt(4),
                        cursor.getString(5),
                        cursor.getString(6)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return plans;
    }

    public List<String> getPlanNamesByUserId(int userId) {
        List<String> planNames = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT PlanName FROM ExercisePlan WHERE UserID = ?",
                new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                planNames.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return planNames;
    }

    public int getTotalCaloriesByPlanName(String planName, int userId) {
        int totalCalories = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(CaloriesBurned) FROM ExercisePlan WHERE PlanName = ? AND UserID = ?",
                new String[]{planName, String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            totalCalories = cursor.getInt(0);
        }
        cursor.close();
        dbHelper.close();
        return totalCalories;
    }

    public Exercise getExerciseById(int exerciseId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Exercise WHERE ExerciseID = ?", new String[]{String.valueOf(exerciseId)});
        if (cursor.moveToFirst()) {
            Exercise exercise = new Exercise(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getDouble(4),
                    cursor.getString(5),
                    cursor.getInt(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9)
            );
            cursor.close();
            dbHelper.close();
            return exercise;
        }
        cursor.close();
        dbHelper.close();
        return null;
    }

    public void deletePlan(int planId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("ExercisePlan", "PlanID = ?", new String[]{String.valueOf(planId)});
        dbHelper.close();
    }
}