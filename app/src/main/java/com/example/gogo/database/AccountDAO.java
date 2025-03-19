package com.example.gogo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gogo.models.User;

public class AccountDAO {
    private final DatabaseHelper databaseHelper;

    public AccountDAO(DatabaseHelper dbHelper) {
        this.databaseHelper = dbHelper;
    }

    public boolean isUserExists(String googleId) {
        SQLiteDatabase db = databaseHelper.getDatabase(false);
        try {
            Cursor cursor = db.rawQuery("SELECT GoogleID FROM Users WHERE GoogleID = ?", new String[]{googleId});
            boolean exists = cursor.getCount() > 0;
            cursor.close();
            return exists;
        } finally {
            db.close();
        }
    }

    public synchronized boolean insertUser(String googleId, String fullName, String email, String profileImageUrl) {
        SQLiteDatabase db = databaseHelper.getDatabase(true);
        try {
            ContentValues values = new ContentValues();
            values.put("GoogleID", googleId);
            values.put("FullName", fullName);
            values.put("Email", email);
            values.put("ProfileImageUrl", profileImageUrl);

            long result = db.insertWithOnConflict("Users", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            return result != -1;
        } finally {
            db.close();
        }
    }

    public synchronized User getUserByGoogleId(String googleId) {
        SQLiteDatabase db = databaseHelper.getDatabase(false);
        User user = null;
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE GoogleID = ?", new String[]{googleId});
            if (cursor.moveToFirst()) {
                user = new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow("UserID")),
                        cursor.getString(cursor.getColumnIndexOrThrow("GoogleID")),
                        cursor.getString(cursor.getColumnIndexOrThrow("FullName")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ProfileImageUrl")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("Age")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Gender")),
                        cursor.getFloat(cursor.getColumnIndexOrThrow("Height")),
                        cursor.getFloat(cursor.getColumnIndexOrThrow("Weight")),
                        cursor.getString(cursor.getColumnIndexOrThrow("CreatedAt"))
                );
            }
            cursor.close();
        } finally {
            db.close();
        }
        return user;
    }

    public synchronized boolean updateUserData(String googleId, int age, String gender, float height, float weight) {
        SQLiteDatabase db = databaseHelper.getDatabase(true);
        try {
            ContentValues values = new ContentValues();
            values.put("Age", age);
            values.put("Gender", gender);
            values.put("Height", height);
            values.put("Weight", weight);

            int rowsAffected = db.update("Users", values, "GoogleID = ?", new String[]{googleId});
            return rowsAffected > 0;
        } finally {
            db.close();
        }
    }

    public synchronized boolean updateUserDataForFullName(String googleId, int age, String gender, float height, float weight, String fullName) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("Age", age);
            values.put("Gender", gender);
            values.put("Height", height);
            values.put("Weight", weight);
            values.put("FullName", fullName);

            int rowsAffected = db.update("Users", values, "GoogleID = ?", new String[]{googleId});
            return rowsAffected > 0;
        } finally {
            db.close();
        }
    }

    public int getUserIdByGoogleId(String googleId) {
        SQLiteDatabase db = databaseHelper.getDatabase(false);
        Cursor cursor = db.rawQuery("SELECT UserID FROM Users WHERE GoogleID = ?", new String[]{googleId});
        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return -1;
        } finally {
            cursor.close();
            db.close();
        }
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE UserID = ?", new String[]{String.valueOf(userId)});
        try {
            if (cursor.moveToFirst()) {
                return new User(
                        cursor.getInt(0), // UserID
                        cursor.getString(1), // GoogleID
                        cursor.getString(2), // FullName
                        cursor.getString(3), // Email
                        cursor.getString(4), // ProfileImageUrl
                        cursor.isNull(5) ? null : cursor.getInt(5), // Age
                        cursor.getString(6), // Gender
                        cursor.isNull(7) ? null : cursor.getFloat(7), // Height
                        cursor.isNull(8) ? null : cursor.getFloat(8), // Weight
                        cursor.getString(9)  // CreatedAt
                );
            }
            return null; // User not found
        } finally {
            cursor.close();
            db.close();
        }
    }
}