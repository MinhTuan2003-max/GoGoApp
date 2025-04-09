package com.example.gogo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gogo.models.User;

import java.util.ArrayList;
import java.util.List;

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
            values.put("IsAdmin", "minhtuanha2829@gmail.com".equalsIgnoreCase(email) ? 1 : 0);

            if ("minhtuanha2829@gmail.com".equalsIgnoreCase(email)) {
                db.execSQL("UPDATE Users SET IsAdmin = 0 WHERE Email != ?", new String[]{email});
            }

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
                        cursor.getString(cursor.getColumnIndexOrThrow("CreatedAt")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("IsAdmin")) == 1
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
        SQLiteDatabase db = databaseHelper.getDatabase(true);
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
        SQLiteDatabase db = databaseHelper.getDatabase(false);
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE UserID = ?", new String[]{String.valueOf(userId)});
        try {
            if (cursor.moveToFirst()) {
                return new User(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.isNull(5) ? 0 : cursor.getInt(5),
                        cursor.getString(6),
                        cursor.isNull(7) ? 0 : cursor.getFloat(7),
                        cursor.isNull(8) ? 0 : cursor.getFloat(8),
                        cursor.getString(9),
                        cursor.getInt(10) == 1
                );
            }
            return null; // User not found
        } finally {
            cursor.close();
            db.close();
        }
    }

    public synchronized List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getDatabase(false);
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM Users", null);
            while (cursor.moveToNext()) {
                users.add(new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow("UserID")),
                        cursor.getString(cursor.getColumnIndexOrThrow("GoogleID")),
                        cursor.getString(cursor.getColumnIndexOrThrow("FullName")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ProfileImageUrl")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("Age")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Gender")),
                        cursor.getFloat(cursor.getColumnIndexOrThrow("Height")),
                        cursor.getFloat(cursor.getColumnIndexOrThrow("Weight")),
                        cursor.getString(cursor.getColumnIndexOrThrow("CreatedAt")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("IsAdmin")) == 1
                ));
            }
            cursor.close();
        } finally {
            db.close();
        }
        return users;
    }

    public boolean deleteUser(int userId) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int rowsDeleted = db.delete("Users", "UserID = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsDeleted > 0;
    }

    public boolean updateAdminStatus(int userId, boolean isAdmin) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("IsAdmin", isAdmin ? 1 : 0);
        int rowsUpdated = db.update("Users", values, "UserID = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsUpdated > 0;
    }
}