package com.example.gogo.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.ExerciseCompletion;

import java.util.ArrayList;
import java.util.List;

public class ExerciseCompletionRepository {
    private final DatabaseHelper dbHelper;

    public ExerciseCompletionRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long addCompletion(ExerciseCompletion completion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("PlanID", completion.getPlanID());
        values.put("UserID", completion.getUserID());
        values.put("ExerciseID", completion.getExerciseID());
        values.put("CaloriesBurned", completion.getCaloriesBurned());
        long id = db.insert("ExerciseCompletion", null, values);
        dbHelper.close();
        return id;
    }

    public List<ExerciseCompletion> getCompletionsByUserId(int userId) {
        List<ExerciseCompletion> completions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM ExerciseCompletion WHERE UserID = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                completions.add(new ExerciseCompletion(
                        cursor.getInt(0), // CompletionID
                        cursor.getInt(1), // PlanID
                        cursor.getInt(2), // UserID
                        cursor.getInt(3), // ExerciseID
                        cursor.getInt(4), // CaloriesBurned
                        cursor.getString(5)  // CompletedAt
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return completions;
    }

    public int getTotalCaloriesBurnedInWeek(int userId, String startDate, String endDate) {
        int totalCalories = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(CaloriesBurned) FROM ExerciseCompletion WHERE UserID = ? AND CompletedAt BETWEEN ? AND ?",
                new String[]{String.valueOf(userId), startDate, endDate});
        if (cursor.moveToFirst()) {
            totalCalories = cursor.getInt(0);
        }
        cursor.close();
        dbHelper.close();
        return totalCalories;
    }

    public int getTotalExercisesCompletedInWeek(int userId, String startDate, String endDate) {
        int totalExercises = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM ExerciseCompletion WHERE UserID = ? AND CompletedAt BETWEEN ? AND ?",
                new String[]{String.valueOf(userId), startDate, endDate});
        if (cursor.moveToFirst()) {
            totalExercises = cursor.getInt(0);
        }
        cursor.close();
        dbHelper.close();
        return totalExercises;
    }

    public int getTotalDurationInWeek(int userId, String startDate, String endDate) {
        int totalDuration = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(e.StandardDuration) " +
                        "FROM ExerciseCompletion ec " +
                        "JOIN Exercise e ON ec.ExerciseID = e.ExerciseID " +
                        "WHERE ec.UserID = ? AND ec.CompletedAt BETWEEN ? AND ?",
                new String[]{String.valueOf(userId), startDate, endDate});
        if (cursor.moveToFirst()) {
            totalDuration = cursor.getInt(0);
        }
        cursor.close();
        dbHelper.close();
        return totalDuration;
    }
}