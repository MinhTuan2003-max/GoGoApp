package com.example.gogo.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.ExerciseCompletion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ExerciseCompletionRepository {
    private final DatabaseHelper dbHelper;

    public ExerciseCompletionRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void addCompletion(ExerciseCompletion completion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("PlanID", completion.getPlanID());
        values.put("UserID", completion.getUserID());
        values.put("ExerciseID", completion.getExerciseID());
        values.put("CaloriesBurned", completion.getCaloriesBurned());

        String dateCompleted = completion.getDateCompleted();
        if (dateCompleted == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateCompleted = sdf.format(new Date());
        }
        values.put("DateCompleted", dateCompleted);

        values.put("Duration", completion.getDuration());

        db.insert("ExerciseCompletion", null, values);
        dbHelper.close();
    }

    public int getTotalCaloriesBurnedInWeek(int userId, String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT SUM(CaloriesBurned) FROM ExerciseCompletion " +
                "WHERE UserID = ? AND DateCompleted BETWEEN ? AND ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), startDate, endDate});
        int totalCalories = 0;
        if (cursor.moveToFirst()) {
            totalCalories = cursor.getInt(0);
        }
        cursor.close();
        dbHelper.close();
        return totalCalories;
    }

    public int getTotalExercisesCompletedInWeek(int userId, String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM ExerciseCompletion " +
                "WHERE UserID = ? AND DateCompleted BETWEEN ? AND ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), startDate, endDate});
        int totalExercises = 0;
        if (cursor.moveToFirst()) {
            totalExercises = cursor.getInt(0);
        }
        cursor.close();
        dbHelper.close();
        return totalExercises;
    }

    public int getTotalDurationInWeek(int userId, String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT SUM(Duration) FROM ExerciseCompletion " +
                "WHERE UserID = ? AND DateCompleted BETWEEN ? AND ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), startDate, endDate});
        int totalDuration = 0;
        if (cursor.moveToFirst()) {
            totalDuration = cursor.getInt(0);
        }
        cursor.close();
        dbHelper.close();
        return totalDuration;
    }

    public int getTrainingDaysInWeek(int userId, String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(DISTINCT DateCompleted) FROM ExerciseCompletion " +
                "WHERE UserID = ? AND DateCompleted BETWEEN ? AND ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), startDate, endDate});
        int totalDays = 0;
        if (cursor.moveToFirst()) {
            totalDays = cursor.getInt(0);
        }
        cursor.close();
        dbHelper.close();
        return totalDays;
    }

    public boolean isExerciseCompleted(int planId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM ExerciseCompletion WHERE PlanID = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(planId)});
        boolean isCompleted = false;
        if (cursor.moveToFirst()) {
            isCompleted = cursor.getInt(0) > 0;
        }
        cursor.close();
        dbHelper.close();
        return isCompleted;
    }

    public Map<String, Integer> getCaloriesByCategoryInWeek(int userId, String startDate, String endDate) {
        Map<String, Integer> caloriesByCategory = new HashMap<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT e.Category, SUM(ec.CaloriesBurned) " +
                "FROM ExerciseCompletion ec " +
                "JOIN Exercise e ON ec.ExerciseID = e.ExerciseID " +
                "WHERE ec.UserID = ? AND ec.DateCompleted BETWEEN ? AND ? " +
                "GROUP BY e.Category";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), startDate, endDate});
        while (cursor.moveToNext()) {
            String category = cursor.getString(0);
            int calories = cursor.getInt(1);
            caloriesByCategory.put(category, calories);
        }
        cursor.close();
        dbHelper.close();
        return caloriesByCategory;
    }

    public void deleteCompletion(int planId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("ExerciseCompletion", "PlanID = ?", new String[]{String.valueOf(planId)});
        dbHelper.close();
    }
}