package com.example.gogo.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.Exercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseRepository {
    private final DatabaseHelper dbHelper;

    public ExerciseRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long addExercise(Exercise exercise) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ExerciseName", exercise.getExerciseName());
        values.put("Category", exercise.getCategory());
        values.put("Description", exercise.getDescription());
        values.put("EnergyConsumed", exercise.getEnergyConsumed());
        values.put("EnergyUnit", exercise.getEnergyUnit());
        values.put("StandardDuration", exercise.getStandardDuration());
        values.put("DifficultyLevel", exercise.getDifficultyLevel());
        values.put("EquipmentRequired", exercise.getEquipmentRequired());
        long id = db.insert("Exercise", null, values);
        dbHelper.close();
        return id;
    }

    public List<Exercise> getAllExercises() {
        List<Exercise> exercises = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Exercise", null);
        if (cursor.moveToFirst()) {
            do {
                exercises.add(new Exercise(
                        cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getDouble(4), cursor.getString(5),
                        cursor.getInt(6), cursor.getString(7), cursor.getString(8),
                        cursor.getString(9)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return exercises;
    }

    public Exercise getExerciseById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Exercise WHERE ExerciseID = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            Exercise exercise = new Exercise(
                    cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getDouble(4), cursor.getString(5),
                    cursor.getInt(6), cursor.getString(7), cursor.getString(8),
                    cursor.getString(9));
            cursor.close();
            dbHelper.close();
            return exercise;
        }
        cursor.close();
        dbHelper.close();
        return null;
    }

    public int updateExercise(Exercise exercise) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ExerciseName", exercise.getExerciseName());
        values.put("Category", exercise.getCategory());
        values.put("Description", exercise.getDescription());
        values.put("EnergyConsumed", exercise.getEnergyConsumed());
        values.put("EnergyUnit", exercise.getEnergyUnit());
        values.put("StandardDuration", exercise.getStandardDuration());
        values.put("DifficultyLevel", exercise.getDifficultyLevel());
        values.put("EquipmentRequired", exercise.getEquipmentRequired());
        int rows = db.update("Exercise", values, "ExerciseID = ?", new String[]{String.valueOf(exercise.getExerciseID())});
        dbHelper.close();
        return rows;
    }

    public void deleteExercise(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Exercise", "ExerciseID = ?", new String[]{String.valueOf(id)});
        dbHelper.close();
    }
}