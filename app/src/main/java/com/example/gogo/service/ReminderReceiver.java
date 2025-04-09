package com.example.gogo.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.gogo.R;
import com.example.gogo.ui.HomeActivity;

import java.util.Random;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "ReminderReceiver";
    private static final String CHANNEL_ID = "GoGoChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra("type");
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");

        Log.d(TAG, "Reminder received: " + type + " - " + title);

        Intent openAppIntent = new Intent(context, HomeActivity.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        openAppIntent.putExtra("FROM_NOTIFICATION", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                new Random().nextInt(),
                openAppIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        if ("water".equals(type)) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(message + " Uống nước thường xuyên giúp cơ thể khỏe mạnh và tăng cường trao đổi chất."));
        } else if ("meal".equals(type)) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(message + " Một bữa ăn cân bằng giúp bạn có đủ năng lượng cho các hoạt động."));
        } else if ("sleep".equals(type)) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(message + " Ngủ đủ giấc giúp bạn tỉnh táo và tăng năng suất làm việc cho ngày mai."));
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = new Random().nextInt(1000);
        notificationManager.notify(notificationId, notificationBuilder.build());

        if ("water".equals(type)) {
            ReminderManager.scheduleWaterReminders(context);
        }
    }
}
