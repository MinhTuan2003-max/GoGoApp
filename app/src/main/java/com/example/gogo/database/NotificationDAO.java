package com.example.gogo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gogo.models.Notification;

public class NotificationDAO {
    private final DatabaseHelper dbHelper;

    public NotificationDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void insertNotification(Notification notification) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("UserID", notification.getUser().getUserId());
        values.put("Message", notification.getMessage());
        values.put("NotifyTime", notification.getNotifyTime());
        values.put("IsRead", notification.getIsRead());
        values.put("Type", notification.getType());
        db.insert("Notification", null, values);
        db.close();
    }

    public long insertNotificationByUserId(Notification notification, int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("UserID", notification.getUser() != null ? notification.getUser().getUserId() : userId);
        values.put("Message", notification.getMessage());
        values.put("NotifyTime", notification.getNotifyTime());
        values.put("IsRead", notification.getIsRead());
        values.put("Type", notification.getType());
        long newRowId = db.insert("Notification", null, values);
        db.close();
        return newRowId;
    }

    public synchronized Cursor getUserNotifications(int userId) {
        SQLiteDatabase db = dbHelper.getDatabase(false);
        return db.rawQuery("SELECT NotificationID, Message, NotifyTime, IsRead, Type FROM Notification WHERE UserID = ?",
                new String[]{String.valueOf(userId)});
    }

    public synchronized Cursor getReminderNotifications(int userId) {
        SQLiteDatabase db = dbHelper.getDatabase(false);
        return db.rawQuery("SELECT NotificationID, Message, NotifyTime FROM Notification WHERE UserID = ? AND Type = 1",
                new String[]{String.valueOf(userId)});
    }

    public synchronized void markNotificationAsRead(int notificationId) {
        SQLiteDatabase db = dbHelper.getDatabase(true);
        try {
            ContentValues values = new ContentValues();
            values.put("IsRead", 1);
            db.update("Notification", values, "NotificationID = ?", new String[]{String.valueOf(notificationId)});
        } finally {
            db.close();
        }
    }

    public Cursor getLatestUserNotification(int userId) {
        SQLiteDatabase db = dbHelper.getDatabase(false);
        String query = "SELECT NotificationID, Message, NotifyTime, IsRead, Type FROM Notification WHERE UserID = ? ORDER BY NotifyTime DESC LIMIT 1";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }
}