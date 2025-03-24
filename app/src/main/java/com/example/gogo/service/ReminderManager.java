package com.example.gogo.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.Calendar;

public class ReminderManager {

    private static final String PREFS_NAME = "ReminderPrefs";
    public static final String KEY_WATER_REMINDER_ENABLED = "water_reminder_enabled";
    public static final String KEY_MEAL_REMINDER_ENABLED = "meal_reminder_enabled";
    public static final String KEY_SLEEP_REMINDER_ENABLED = "sleep_reminder_enabled";

    // Request codes for pending intents
    public static final int WATER_REMINDER_REQUEST_CODE = 1001;
    public static final int BREAKFAST_REMINDER_REQUEST_CODE = 1002;
    public static final int LUNCH_REMINDER_REQUEST_CODE = 1003;
    public static final int DINNER_REMINDER_REQUEST_CODE = 1004;
    public static final String KEY_WATER_INTERVAL = "water_interval_minutes";
    public static final String KEY_BREAKFAST_TIME = "breakfast_time";
    public static final String KEY_LUNCH_TIME = "lunch_time";
    public static final String KEY_DINNER_TIME = "dinner_time";
    public static final String KEY_SLEEP_TIME = "sleep_time";
    public static final int SLEEP_REMINDER_REQUEST_CODE = 1005;

    private static final int DEFAULT_WATER_INTERVAL = 120;
    private static final String DEFAULT_BREAKFAST_TIME = "07:00";
    private static final String DEFAULT_LUNCH_TIME = "12:00";
    private static final String DEFAULT_DINNER_TIME = "18:00";
    private static final String DEFAULT_SLEEP_TIME = "22:00";

    public static void scheduleWaterReminders(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean enabled = prefs.getBoolean(KEY_WATER_REMINDER_ENABLED, true);

        if (!enabled) {
            cancelWaterReminders(context);
            return;
        }

        int intervalMinutes = prefs.getInt(KEY_WATER_INTERVAL, DEFAULT_WATER_INTERVAL);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("type", "water");
        intent.putExtra("title", "Đã đến giờ uống nước");
        intent.putExtra("message", "Nhớ uống ít nhất 200ml nước để giữ cơ thể khỏe mạnh nhé!");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                WATER_REMINDER_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Thiết lập thông báo đầu tiên sau 5 phút từ thời điểm hiện tại
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);

        // Thiết lập thông báo lặp lại
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    intervalMinutes * 60 * 1000, // chuyển phút thành milli giây
                    pendingIntent
            );
        } else {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    intervalMinutes * 60 * 1000, // chuyển phút thành milli giây
                    pendingIntent
            );
        }
    }

    public static void scheduleMealReminders(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean enabled = prefs.getBoolean(KEY_MEAL_REMINDER_ENABLED, true);

        if (!enabled) {
            cancelMealReminders(context);
            return;
        }

        // Lấy thời gian từ SharedPreferences
        String breakfastTime = prefs.getString(KEY_BREAKFAST_TIME, DEFAULT_BREAKFAST_TIME);
        String lunchTime = prefs.getString(KEY_LUNCH_TIME, DEFAULT_LUNCH_TIME);
        String dinnerTime = prefs.getString(KEY_DINNER_TIME, DEFAULT_DINNER_TIME);

        // Thiết lập thông báo ăn sáng
        scheduleSpecificMealReminder(
                context,
                breakfastTime,
                BREAKFAST_REMINDER_REQUEST_CODE,
                "Đã đến giờ ăn sáng",
                "Ăn sáng đầy đủ để có nguồn năng lượng cho cả ngày!"
        );

        // Thiết lập thông báo ăn trưa
        scheduleSpecificMealReminder(
                context,
                lunchTime,
                LUNCH_REMINDER_REQUEST_CODE,
                "Đã đến giờ ăn trưa",
                "Dừng công việc và ăn trưa đi nào!"
        );

        // Thiết lập thông báo ăn tối
        scheduleSpecificMealReminder(
                context,
                dinnerTime,
                DINNER_REMINDER_REQUEST_CODE,
                "Đã đến giờ ăn tối",
                "Đừng quên ăn tối để nạp năng lượng cho buổi tối nhé!"
        );
    }

    public static void scheduleSleepReminder(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean enabled = prefs.getBoolean(KEY_SLEEP_REMINDER_ENABLED, true);

        if (!enabled) {
            cancelSleepReminder(context);
            return;
        }

        String sleepTime = prefs.getString(KEY_SLEEP_TIME, DEFAULT_SLEEP_TIME);

        String[] timeParts = sleepTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Thiết lập thông báo
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("type", "sleep");
        intent.putExtra("title", "Đã đến giờ đi ngủ");
        intent.putExtra("message", "Nghỉ ngơi sớm để có sức khỏe tốt cho ngày mai nhé!");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                SLEEP_REMINDER_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    private static void scheduleSpecificMealReminder(Context context, String timeString, int requestCode, String title, String message) {

        String[] timeParts = timeString.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Thiết lập thông báo
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("type", "meal");
        intent.putExtra("title", title);
        intent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    public static void cancelWaterReminders(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                WATER_REMINDER_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }

    public static void cancelMealReminders(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Hủy thông báo ăn sáng
        Intent breakfastIntent = new Intent(context, ReminderReceiver.class);
        PendingIntent breakfastPI = PendingIntent.getBroadcast(
                context,
                BREAKFAST_REMINDER_REQUEST_CODE,
                breakfastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(breakfastPI);

        // Hủy thông báo ăn trưa
        Intent lunchIntent = new Intent(context, ReminderReceiver.class);
        PendingIntent lunchPI = PendingIntent.getBroadcast(
                context,
                LUNCH_REMINDER_REQUEST_CODE,
                lunchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(lunchPI);

        // Hủy thông báo ăn tối
        Intent dinnerIntent = new Intent(context, ReminderReceiver.class);
        PendingIntent dinnerPI = PendingIntent.getBroadcast(
                context,
                DINNER_REMINDER_REQUEST_CODE,
                dinnerIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(dinnerPI);
    }

    public static void cancelSleepReminder(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                SLEEP_REMINDER_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }

    public static void setupDefaultReminderSettings(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (!prefs.contains(KEY_WATER_REMINDER_ENABLED)) {
            editor.putBoolean(KEY_WATER_REMINDER_ENABLED, true);
        }

        if (!prefs.contains(KEY_MEAL_REMINDER_ENABLED)) {
            editor.putBoolean(KEY_MEAL_REMINDER_ENABLED, true);
        }

        if (!prefs.contains(KEY_SLEEP_REMINDER_ENABLED)) {
            editor.putBoolean(KEY_SLEEP_REMINDER_ENABLED, true);
        }

        if (!prefs.contains(KEY_WATER_INTERVAL)) {
            editor.putInt(KEY_WATER_INTERVAL, DEFAULT_WATER_INTERVAL);
        }

        if (!prefs.contains(KEY_BREAKFAST_TIME)) {
            editor.putString(KEY_BREAKFAST_TIME, DEFAULT_BREAKFAST_TIME);
        }

        if (!prefs.contains(KEY_LUNCH_TIME)) {
            editor.putString(KEY_LUNCH_TIME, DEFAULT_LUNCH_TIME);
        }

        if (!prefs.contains(KEY_DINNER_TIME)) {
            editor.putString(KEY_DINNER_TIME, DEFAULT_DINNER_TIME);
        }

        if (!prefs.contains(KEY_SLEEP_TIME)) {
            editor.putString(KEY_SLEEP_TIME, DEFAULT_SLEEP_TIME);
        }

        editor.apply();
    }

    public static void setupAllReminders(Context context) {
        setupDefaultReminderSettings(context);
        scheduleWaterReminders(context);
        scheduleMealReminders(context);
        scheduleSleepReminder(context);
    }
}