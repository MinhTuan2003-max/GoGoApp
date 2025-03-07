package com.example.gogo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.gogo.models.SleepRecord;

import java.util.ArrayList;
import java.util.List;

public class SleepDao {
    private DatabaseHelper dbHelper;

    public SleepDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Thêm bản ghi giấc ngủ
    public long insertSleepRecord(SleepRecord record) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DATE, record.getDate());
        values.put(DatabaseHelper.COLUMN_SLEEP_TIME, record.getSleepTime());
        values.put(DatabaseHelper.COLUMN_WAKE_UP_TIME, record.getWakeUpTime());
        values.put(DatabaseHelper.COLUMN_SLEEP_HOURS, record.getSleepHours());
        long id = db.insert(DatabaseHelper.TABLE_SLEEP_RECORDS, null, values);
        db.close();
        return id;
    }

    // Lấy tất cả bản ghi
    public List<SleepRecord> getAllSleepRecords() {
        List<SleepRecord> records = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_SLEEP_RECORDS, null, null, null, null, null, DatabaseHelper.COLUMN_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
                String sleepTime = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SLEEP_TIME));
                String wakeUpTime = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_WAKE_UP_TIME));
                float sleepHours = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_SLEEP_HOURS));
                records.add(new SleepRecord(id, date, sleepTime, wakeUpTime, sleepHours));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return records;
    }
}