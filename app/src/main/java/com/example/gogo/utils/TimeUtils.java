package com.example.gogo.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeUtils {
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // Tính giờ đi ngủ dựa trên giờ thức dậy và số giờ ngủ mong muốn
    public static String calculateSleepTime(String wakeUpTime, float sleepHours) {
        try {
            Calendar wakeUpCal = Calendar.getInstance();
            wakeUpCal.setTime(TIME_FORMAT.parse(wakeUpTime));
            int minutesToSubtract = (int) (sleepHours * 60);
            wakeUpCal.add(Calendar.MINUTE, -minutesToSubtract);
            return TIME_FORMAT.format(wakeUpCal.getTime());
        } catch (Exception e) {
            return "Error";
        }
    }

    // Tính số giờ ngủ
    public static float calculateSleepHours(String sleepTime, String wakeUpTime) {
        try {
            Calendar sleepCal = Calendar.getInstance();
            Calendar wakeUpCal = Calendar.getInstance();
            sleepCal.setTime(TIME_FORMAT.parse(sleepTime));
            wakeUpCal.setTime(TIME_FORMAT.parse(wakeUpTime));

            long diffMillis = wakeUpCal.getTimeInMillis() - sleepCal.getTimeInMillis();
            if (diffMillis < 0) diffMillis += 24 * 60 * 60 * 1000; // Qua ngày mới
            return diffMillis / (1000f * 60 * 60);
        } catch (Exception e) {
            return 0f;
        }
    }

    public static String getCurrentDate() {
        return DATE_FORMAT.format(Calendar.getInstance().getTime());
    }
}