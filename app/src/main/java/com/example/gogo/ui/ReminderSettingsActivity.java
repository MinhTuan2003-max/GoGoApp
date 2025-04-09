package com.example.gogo.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gogo.R;
import com.example.gogo.service.ReminderManager;

import java.util.Locale;

public class ReminderSettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ReminderPrefs";

    private Switch waterSwitch, mealSwitch, sleepSwitch;
    private Spinner waterIntervalSpinner;
    private TimePicker breakfastTimePicker, lunchTimePicker, dinnerTimePicker, sleepTimePicker;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_settings);

        initializeViews();
        loadSavedPreferences();
        setupListeners();
    }

    private void initializeViews() {
        waterSwitch = findViewById(R.id.switch_water);
        mealSwitch = findViewById(R.id.switch_meal);
        sleepSwitch = findViewById(R.id.switch_sleep);

        waterIntervalSpinner = findViewById(R.id.spinner_water_interval);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.water_intervals, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waterIntervalSpinner.setAdapter(adapter);

        breakfastTimePicker = findViewById(R.id.timePicker_breakfast);
        lunchTimePicker = findViewById(R.id.timePicker_lunch);
        dinnerTimePicker = findViewById(R.id.timePicker_dinner);
        sleepTimePicker = findViewById(R.id.timePicker_sleep);

        breakfastTimePicker.setIs24HourView(true);
        lunchTimePicker.setIs24HourView(true);
        dinnerTimePicker.setIs24HourView(true);
        sleepTimePicker.setIs24HourView(true);

        saveButton = findViewById(R.id.button_save);
    }

    private void loadSavedPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        waterSwitch.setChecked(prefs.getBoolean(ReminderManager.KEY_WATER_REMINDER_ENABLED, true));
        mealSwitch.setChecked(prefs.getBoolean(ReminderManager.KEY_MEAL_REMINDER_ENABLED, true));
        sleepSwitch.setChecked(prefs.getBoolean(ReminderManager.KEY_SLEEP_REMINDER_ENABLED, true));

        int waterInterval = prefs.getInt(ReminderManager.KEY_WATER_INTERVAL, 120);
        int spinnerPosition = 0;
        switch (waterInterval) {
            case 30:
                spinnerPosition = 0;
                break;
            case 60:
                spinnerPosition = 1;
                break;
            case 90:
                spinnerPosition = 2;
                break;
            case 120:
                spinnerPosition = 3;
                break;
            case 180:
                spinnerPosition = 4;
                break;
            case 240:
                spinnerPosition = 5;
                break;
        }
        waterIntervalSpinner.setSelection(spinnerPosition);

        setTimePickerFromString(breakfastTimePicker, prefs.getString(ReminderManager.KEY_BREAKFAST_TIME, "07:00"));
        setTimePickerFromString(lunchTimePicker, prefs.getString(ReminderManager.KEY_LUNCH_TIME, "12:00"));
        setTimePickerFromString(dinnerTimePicker, prefs.getString(ReminderManager.KEY_DINNER_TIME, "18:00"));

        setTimePickerFromString(sleepTimePicker, prefs.getString(ReminderManager.KEY_SLEEP_TIME, "22:00"));
    }

    private void setTimePickerFromString(TimePicker timePicker, String timeString) {
        String[] timeParts = timeString.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        timePicker.setHour(hour);
        timePicker.setMinute(minute);
    }

    private String getTimeStringFromTimePicker(TimePicker timePicker) {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> saveSettings());

        waterSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                waterIntervalSpinner.setEnabled(isChecked));

        mealSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            breakfastTimePicker.setEnabled(isChecked);
            lunchTimePicker.setEnabled(isChecked);
            dinnerTimePicker.setEnabled(isChecked);
        });

        sleepSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                sleepTimePicker.setEnabled(isChecked));
    }

    private void saveSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(ReminderManager.KEY_WATER_REMINDER_ENABLED, waterSwitch.isChecked());
        editor.putBoolean(ReminderManager.KEY_MEAL_REMINDER_ENABLED, mealSwitch.isChecked());
        editor.putBoolean(ReminderManager.KEY_SLEEP_REMINDER_ENABLED, sleepSwitch.isChecked());

        int waterIntervalMinutes;
        switch (waterIntervalSpinner.getSelectedItemPosition()) {
            case 0:
                waterIntervalMinutes = 30;
                break;
            case 1:
                waterIntervalMinutes = 60;
                break;
            case 2:
                waterIntervalMinutes = 90;
                break;
            case 3:
                waterIntervalMinutes = 120;
                break;
            case 4:
                waterIntervalMinutes = 180;
                break;
            case 5:
                waterIntervalMinutes = 240;
                break;
            default:
                waterIntervalMinutes = 120;
        }
        editor.putInt(ReminderManager.KEY_WATER_INTERVAL, waterIntervalMinutes);

        editor.putString(ReminderManager.KEY_BREAKFAST_TIME, getTimeStringFromTimePicker(breakfastTimePicker));
        editor.putString(ReminderManager.KEY_LUNCH_TIME, getTimeStringFromTimePicker(lunchTimePicker));
        editor.putString(ReminderManager.KEY_DINNER_TIME, getTimeStringFromTimePicker(dinnerTimePicker));

        editor.putString(ReminderManager.KEY_SLEEP_TIME, getTimeStringFromTimePicker(sleepTimePicker));

        editor.apply();

        if (waterSwitch.isChecked()) {
            ReminderManager.scheduleWaterReminders(this);
        } else {
            ReminderManager.cancelWaterReminders(this);
        }

        if (mealSwitch.isChecked()) {
            ReminderManager.scheduleMealReminders(this);
        } else {
            ReminderManager.cancelMealReminders(this);
        }

        if (sleepSwitch.isChecked()) {
            ReminderManager.scheduleSleepReminder(this);
        } else {
            ReminderManager.cancelSleepReminder(this);
        }

        Toast.makeText(this, "Đã lưu thiết lập thông báo", Toast.LENGTH_SHORT).show();
        finish();
    }
}