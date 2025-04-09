package com.example.gogo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gogo.models.SleepRecord;
import com.example.gogo.models.User;

import java.util.ArrayList;
import java.util.List;

public class SleepDAO {
    private final DatabaseHelper databaseHelper;

    public SleepDAO(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    public synchronized long insertSleepRecord(SleepRecord record) {
        SQLiteDatabase db = databaseHelper.getDatabase(true);
        try {
            ContentValues values = new ContentValues();
            values.put("UserID", record.getUser().getUserId());
            values.put("Date", record.getDate());
            values.put("BedTime", record.getSleepTime());
            values.put("WakeTime", record.getWakeUpTime());
            values.put("Notes", "");
            values.put("SleepHours", record.getSleepHours());

            long id = db.insert("SleepRecord", null, values);
            return id;
        } finally {
            db.close();
        }
    }

    public synchronized List<SleepRecord> getAllSleepRecords() {
        List<SleepRecord> records = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getDatabase(false);
        Cursor cursor = null;
        try {
            String query = "SELECT sr.*, u.* FROM SleepRecord sr " +
                    "INNER JOIN Users u ON sr.UserID = u.UserID " +
                    "ORDER BY sr.Date DESC";
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    User user = new User(
                            cursor.getInt(cursor.getColumnIndexOrThrow("UserID")),
                            cursor.getString(cursor.getColumnIndexOrThrow("GoogleID")),
                            cursor.getString(cursor.getColumnIndexOrThrow("FullName")),
                            cursor.getString(cursor.getColumnIndexOrThrow("Email")),
                            cursor.getString(cursor.getColumnIndexOrThrow("ProfileImageUrl")),
                            cursor.isNull(cursor.getColumnIndexOrThrow("Age")) ? 0 : cursor.getInt(cursor.getColumnIndexOrThrow("Age")),
                            cursor.getString(cursor.getColumnIndexOrThrow("Gender")),
                            cursor.isNull(cursor.getColumnIndexOrThrow("Height")) ? 0f : cursor.getFloat(cursor.getColumnIndexOrThrow("Height")),
                            cursor.isNull(cursor.getColumnIndexOrThrow("Weight")) ? 0f : cursor.getFloat(cursor.getColumnIndexOrThrow("Weight")),
                            cursor.getString(cursor.getColumnIndexOrThrow("CreatedAt")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("IsAdmin")) == 1
                    );

                    SleepRecord record = new SleepRecord(
                            cursor.getInt(cursor.getColumnIndexOrThrow("RecordID")),
                            user,
                            cursor.getString(cursor.getColumnIndexOrThrow("Date")),
                            cursor.getString(cursor.getColumnIndexOrThrow("BedTime")),
                            cursor.getString(cursor.getColumnIndexOrThrow("WakeTime")),
                            cursor.isNull(cursor.getColumnIndexOrThrow("SleepHours")) ? 0f : cursor.getFloat(cursor.getColumnIndexOrThrow("SleepHours"))
                    );
                    records.add(record);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return records;
    }

    public synchronized List<SleepRecord> getSleepRecordsByUserId(int userId) {
        List<SleepRecord> records = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getDatabase(false);
        Cursor cursor = null;
        try {
            String query = "SELECT sr.*, u.* FROM SleepRecord sr " +
                    "INNER JOIN Users u ON sr.UserID = u.UserID " +
                    "WHERE sr.UserID = ? " +
                    "ORDER BY sr.Date DESC";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor.moveToFirst()) {
                do {
                    User user = new User(
                            cursor.getInt(cursor.getColumnIndexOrThrow("UserID")),
                            cursor.getString(cursor.getColumnIndexOrThrow("GoogleID")),
                            cursor.getString(cursor.getColumnIndexOrThrow("FullName")),
                            cursor.getString(cursor.getColumnIndexOrThrow("Email")),
                            cursor.getString(cursor.getColumnIndexOrThrow("ProfileImageUrl")),
                            cursor.isNull(cursor.getColumnIndexOrThrow("Age")) ? 0 : cursor.getInt(cursor.getColumnIndexOrThrow("Age")),
                            cursor.getString(cursor.getColumnIndexOrThrow("Gender")),
                            cursor.isNull(cursor.getColumnIndexOrThrow("Height")) ? 0f : cursor.getFloat(cursor.getColumnIndexOrThrow("Height")),
                            cursor.isNull(cursor.getColumnIndexOrThrow("Weight")) ? 0f : cursor.getFloat(cursor.getColumnIndexOrThrow("Weight")),
                            cursor.getString(cursor.getColumnIndexOrThrow("CreatedAt")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("IsAdmin")) == 1
                    );

                    SleepRecord record = new SleepRecord(
                            cursor.getInt(cursor.getColumnIndexOrThrow("RecordID")),
                            user,
                            cursor.getString(cursor.getColumnIndexOrThrow("Date")),
                            cursor.getString(cursor.getColumnIndexOrThrow("BedTime")),
                            cursor.getString(cursor.getColumnIndexOrThrow("WakeTime")),
                            cursor.isNull(cursor.getColumnIndexOrThrow("SleepHours")) ? 0f : cursor.getFloat(cursor.getColumnIndexOrThrow("SleepHours"))
                    );
                    records.add(record);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return records;
    }

    public synchronized boolean deleteSleepRecord(int recordId) {
        SQLiteDatabase db = databaseHelper.getDatabase(true);
        try {
            int rowsAffected = db.delete("SleepRecord", "RecordID = ?", new String[]{String.valueOf(recordId)});
            return rowsAffected > 0;
        } finally {
            db.close();
        }
    }

    public synchronized boolean updateSleepRecord(SleepRecord record) {
        SQLiteDatabase db = databaseHelper.getDatabase(true);
        try {
            ContentValues values = new ContentValues();
            values.put("UserID", record.getUser().getUserId());
            values.put("Date", record.getDate());
            values.put("BedTime", record.getSleepTime());
            values.put("WakeTime", record.getWakeUpTime());
            values.put("SleepHours", record.getSleepHours());

            int rowsAffected = db.update("SleepRecord", values, "RecordID = ?",
                    new String[]{String.valueOf(record.getId())});
            return rowsAffected > 0;
        } finally {
            db.close();
        }
    }
}
