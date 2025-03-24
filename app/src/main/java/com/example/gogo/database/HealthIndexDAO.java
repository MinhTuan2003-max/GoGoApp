package com.example.gogo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HealthIndexDAO {
    private final DatabaseHelper dbHelper;

    public HealthIndexDAO(DatabaseHelper dbHelper) { // Changed to accept DatabaseHelper
        this.dbHelper = dbHelper;
    }

    public synchronized boolean insertHealthIndex(int userId, float bmi, float bmr) {
        SQLiteDatabase db = dbHelper.getDatabase(true);
        try {
            ContentValues values = new ContentValues();
            values.put("UserID", userId);
            values.put("BMI", bmi);
            values.put("BMR", bmr);

            long result = db.insert("HealthIndex", null, values);
            return result != -1;
        } finally {
            db.close();
        }
    }

    public synchronized Cursor getLatestHealthIndex(int userId) {
        SQLiteDatabase db = dbHelper.getDatabase(false);
        try {
            String query = "SELECT * FROM HealthIndex WHERE UserID = ? ORDER BY RecordedAt DESC LIMIT 1";
            return db.rawQuery(query, new String[]{String.valueOf(userId)});
            // Caller should close the Cursor
        } finally {
            db.close(); // Close the database after query
        }
    }

}