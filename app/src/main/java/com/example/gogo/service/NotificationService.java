package com.example.gogo.service;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.database.NotificationDAO;
import com.example.gogo.ui.HomeActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationService extends Service {
    private DatabaseHelper dbHelper;
    private NotificationDAO notificationDAO;
    private static final String CHANNEL_ID = "GoGoChannel";
    private static final int FOREGROUND_ID = 1234;
    private int userId = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DatabaseHelper(this);
        notificationDAO = new NotificationDAO(dbHelper);
        createNotificationChannel();
        startForegroundService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("userId")) {
            userId = intent.getIntExtra("userId", -1);
            scheduleNotifications();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void scheduleNotifications() {
        if (userId == -1) return;

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e("NotificationService", "AlarmManager is null");
            return;
        }

        Cursor cursor = notificationDAO.getReminderNotifications(userId);
        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String message = cursor.getString(1);
                String notifyTime = cursor.getString(2);
                scheduleAlarm(alarmManager, id, message, notifyTime);
            }
        } finally {
            cursor.close();
        }
    }

    private void scheduleAlarm(AlarmManager alarmManager, int notificationId, String message, String notifyTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = sdf.parse(notifyTime);
            if (date == null) return;

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, date.getHours());
            calendar.set(Calendar.MINUTE, date.getMinutes());
            calendar.set(Calendar.SECOND, 0);

            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("notificationId", notificationId);
            intent.putExtra("message", message);
            intent.putExtra("notifyTime", notifyTime);
            intent.putExtra("userId", userId);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notificationId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    try {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        Log.d("NotificationService", "Scheduled exact notification: " + message + " at " + notifyTime);
                    } catch (SecurityException e) {
                        Log.e("NotificationService", "SecurityException while scheduling exact alarm: ", e);
                        // Fallback to inexact alarm if permission is denied
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        Log.d("NotificationService", "Falling back to inexact alarm for: " + message);
                    }
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    Log.w("NotificationService", "Exact alarms not allowed, using inexact alarm for: " + message);
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                Log.d("NotificationService", "Scheduled exact notification: " + message + " at " + notifyTime);
            }
        } catch (Exception e) {
            Log.e("NotificationService", "Error scheduling alarm: ", e);
        }
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "GoGo Notifications", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    private void startForegroundService() {
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("GoGo Service")
                .setContentText("Running in the background")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent);

        startForeground(FOREGROUND_ID, builder.build());
    }

    public static class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int notificationId = intent.getIntExtra("notificationId", -1);
            String message = intent.getStringExtra("message");
            String notifyTime = intent.getStringExtra("notifyTime");
            int userId = intent.getIntExtra("userId", -1);

            if (notificationId != -1 && message != null && notifyTime != null && userId != -1) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Intent notificationIntent = new Intent(context, HomeActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("GoGo Reminder")
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

                manager.notify(notificationId, builder.build());

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarmManager == null) {
                    Log.e("NotificationReceiver", "AlarmManager is null");
                    return;
                }

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    Date date = sdf.parse(notifyTime);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, date.getHours());
                    calendar.set(Calendar.MINUTE, date.getMinutes());
                    calendar.set(Calendar.SECOND, 0);
                    calendar.add(Calendar.DAY_OF_YEAR, 1);

                    Intent nextIntent = new Intent(context, NotificationReceiver.class);
                    nextIntent.putExtra("notificationId", notificationId);
                    nextIntent.putExtra("message", message);
                    nextIntent.putExtra("notifyTime", notifyTime);
                    nextIntent.putExtra("userId", userId);

                    PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, notificationId, nextIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            try {
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), nextPendingIntent);
                                Log.d("NotificationReceiver", "Rescheduled exact notification: " + message + " for tomorrow at " + notifyTime);
                            } catch (SecurityException e) {
                                Log.e("NotificationReceiver", "SecurityException while rescheduling exact alarm: ", e);
                                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), nextPendingIntent);
                                Log.d("NotificationReceiver", "Falling back to inexact alarm for: " + message);
                            }
                        } else {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), nextPendingIntent);
                            Log.w("NotificationReceiver", "Exact alarms not allowed, using inexact alarm for rescheduling: " + message);
                        }
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), nextPendingIntent);
                        Log.d("NotificationReceiver", "Rescheduled exact notification: " + message + " for tomorrow at " + notifyTime);
                    }
                } catch (Exception e) {
                    Log.e("NotificationReceiver", "Error rescheduling alarm: ", e);
                }
            }
        }
    }
}