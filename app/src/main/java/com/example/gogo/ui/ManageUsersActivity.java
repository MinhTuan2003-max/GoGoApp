package com.example.gogo.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.adapters.FoodAdapter;
import com.example.gogo.adapters.UserAdapter;
import com.example.gogo.database.AccountDAO;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.database.FoodDAO;
import com.example.gogo.database.NotificationDAO;
import com.example.gogo.models.Food;
import com.example.gogo.models.Notification;
import com.example.gogo.models.User;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManageUsersActivity extends AppCompatActivity implements UserAdapter.OnUserActionListener, FoodAdapter.OnFoodActionListener {
    private static final String CHANNEL_ID = "GoGoChannel";
    private static final String PREFS_NAME = "GoGoPrefs";
    private static final String KEY_DARK_MODE = "darkMode";
    private static final String KEY_MAINTENANCE_MODE = "maintenanceMode";

    private DatabaseHelper databaseHelper;
    private AccountDAO accountDAO;
    private NotificationDAO notificationDAO;
    private FoodDAO foodDAO;
    private RecyclerView usersRecyclerView, recentActivityRecyclerView, foodRecyclerView;
    private UserAdapter userAdapter;
    private FoodAdapter foodAdapter;
    private List<User> userList;
    private List<Food> foodList;
    private TabLayout adminTabLayout;
    private MaterialToolbar toolbar;
    private int userId;
    private TextInputEditText notificationMessage, searchFoodInput;
    private MaterialAutoCompleteTextView notificationTitle;
    private MaterialButton sendNotificationButton, logoutButton, addFoodButton;
    private SwitchMaterial maintenanceModeSwitch, darkModeSwitch;
    private PieChart userStatsChart;
    private GoogleSignInClient googleSignInClient;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        databaseHelper = new DatabaseHelper(this);
        accountDAO = new AccountDAO(databaseHelper);
        notificationDAO = new NotificationDAO(databaseHelper);
        foodDAO = new FoodDAO(databaseHelper);

        toolbar = findViewById(R.id.toolbar);
        adminTabLayout = findViewById(R.id.admin_tab_layout);
        usersRecyclerView = findViewById(R.id.users_recycler_view);
        foodRecyclerView = findViewById(R.id.food_recycler_view);
        notificationTitle = findViewById(R.id.notification_title);
        notificationMessage = findViewById(R.id.notification_message);
        sendNotificationButton = findViewById(R.id.send_notification_button);
        logoutButton = findViewById(R.id.logout_button);
        maintenanceModeSwitch = findViewById(R.id.maintenance_mode_switch);
        darkModeSwitch = findViewById(R.id.dark_mode_switch);
        userStatsChart = findViewById(R.id.user_stats_chart);
        recentActivityRecyclerView = findViewById(R.id.recent_activity_recycler_view);
        searchFoodInput = findViewById(R.id.search_food_input);
        addFoodButton = findViewById(R.id.add_food_button);

        userId = getIntent().getIntExtra("USER_ID", -1);

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList, this);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setAdapter(userAdapter);

        foodList = new ArrayList<>();
        foodAdapter = new FoodAdapter(this, foodList, this);
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodRecyclerView.setAdapter(foodAdapter);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        boolean isMaintenanceMode = prefs.getBoolean(KEY_MAINTENANCE_MODE, false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        darkModeSwitch.setChecked(isDarkMode);
        maintenanceModeSwitch.setChecked(isMaintenanceMode);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        setupTabLayout();
        setupSettings();
        setupReports();
        setupFoodManagement();

        loadUsers();
    }

    private void setupTabLayout() {
        adminTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                findViewById(R.id.users_recycler_view).setVisibility(View.GONE);
                findViewById(R.id.food_management_container).setVisibility(View.GONE);
                findViewById(R.id.settings_container).setVisibility(View.GONE);
                findViewById(R.id.reports_container).setVisibility(View.GONE);

                switch (tab.getPosition()) {
                    case 0:
                        toolbar.setTitle("Manage Accounts");
                        usersRecyclerView.setVisibility(View.VISIBLE);
                        loadUsers();
                        break;
                    case 1:
                        toolbar.setTitle("Manage Food");
                        findViewById(R.id.food_management_container).setVisibility(View.VISIBLE);
                        loadFoods();
                        break;
                    case 2:
                        toolbar.setTitle("Settings");
                        findViewById(R.id.settings_container).setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        toolbar.setTitle("Reports");
                        findViewById(R.id.reports_container).setVisibility(View.VISIBLE);
                        loadUserStats();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        adminTabLayout.getTabAt(0).select();
    }

    private void setupSettings() {
        sendNotificationButton.setOnClickListener(v -> {
            String message = notificationMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                sendNotificationToAllUsers(message);
                notificationMessage.setText("");
                Toast.makeText(this, "Notification sent", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            }
        });

        logoutButton.setOnClickListener(v -> {
            googleSignInClient.signOut().addOnCompleteListener(this, task -> {
                Intent intent = new Intent(ManageUsersActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });

        maintenanceModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_MAINTENANCE_MODE, isChecked);
            editor.apply();
            Toast.makeText(this, "Maintenance Mode: " + (isChecked ? "ON" : "OFF"), Toast.LENGTH_SHORT).show();
        });

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_DARK_MODE, isChecked);
            editor.apply();
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            recreate();
        });
    }

    private void setupFoodManagement() {
        addFoodButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManageUsersActivity.this, AddFoodActivity.class);
            startActivityForResult(intent, 1);
        });

        searchFoodInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFoods(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterFoods(String query) {
        List<Food> filteredList = new ArrayList<>();
        for (Food food : foodDAO.getAllFoods()) {
            if (food.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(food);
            }
        }
        foodList.clear();
        foodList.addAll(filteredList);
        foodAdapter.notifyDataSetChanged();
    }

    private void sendNotificationToAllUsers(String message) {
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            List<User> users = accountDAO.getAllUsers();
            if (users == null || users.isEmpty()) {
                Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show();
                return;
            }

            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("GoGo Admin")
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            for (User user : users) {
                Notification notification = new Notification(
                        user.getUserId(), user, message, currentTime, 0, 0);
                notificationDAO.insertNotification(notification);
            }

            manager.notify((int) System.currentTimeMillis(), builder.build());
        } catch (Exception e) {
            Log.e("NotificationError", "Failed to send notification", e);
            Toast.makeText(this, "Error sending notification", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupReports() {
        userStatsChart.getDescription().setEnabled(false);
        userStatsChart.setDrawHoleEnabled(true);
        userStatsChart.setHoleColor(android.graphics.Color.TRANSPARENT);
        userStatsChart.setTransparentCircleRadius(40f);
        userStatsChart.setHoleRadius(35f);
        userStatsChart.setDrawEntryLabels(false);

        recentActivityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadUserStats() {
        List<User> users = accountDAO.getAllUsers();
        int adminCount = 0, userCount = 0;
        for (User user : users) {
            if (user.isAdmin()) adminCount++;
            else userCount++;
        }

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(adminCount, "Admins"));
        entries.add(new PieEntry(userCount, "Users"));

        PieDataSet dataSet = new PieDataSet(entries, "User Distribution");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(android.graphics.Color.WHITE);

        PieData data = new PieData(dataSet);
        userStatsChart.setData(data);
        userStatsChart.invalidate();
    }

    private void loadUsers() {
        userList.clear();
        List<User> allUsers = accountDAO.getAllUsers();

        List<User> admins = new ArrayList<>();
        List<User> regularUsers = new ArrayList<>();
        for (User user : allUsers) {
            if (user.isAdmin()) {
                admins.add(user);
            } else {
                regularUsers.add(user);
            }
        }

        Collections.sort(regularUsers, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return Integer.compare(u2.getUserId(), u1.getUserId());
            }
        });

        userList.addAll(admins);
        userList.addAll(regularUsers);

        userAdapter.updateList(userList);
    }

    private void loadFoods() {
        foodList.clear();
        foodList.addAll(foodDAO.getAllFoods());
        foodAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeleteUser(int userId) {
        boolean success = accountDAO.deleteUser(userId);
        if (success) {
            Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show();
            loadUsers();
        } else {
            Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onToggleAdmin(int userId, boolean isAdmin) {
        boolean newAdminStatus = !isAdmin;
        boolean success = accountDAO.updateAdminStatus(userId, newAdminStatus);
        if (success) {
            Toast.makeText(this, newAdminStatus ? "User promoted to admin" : "Admin status removed", Toast.LENGTH_SHORT).show();
            loadUsers();
        } else {
            Toast.makeText(this, "Failed to update admin status", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpdateFood(int foodId) {
        Intent intent = new Intent(this, AddFoodActivity.class);
        intent.putExtra("FOOD_ID", foodId);
        intent.putExtra("IS_UPDATE", true);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onDeleteFood(int foodId) {
        boolean success = foodDAO.deleteFood(foodId);
        if (success) {
            Toast.makeText(this, "Food deleted successfully", Toast.LENGTH_SHORT).show();
            loadFoods();
        } else {
            Toast.makeText(this, "Failed to delete food", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1 || requestCode == 2) {
                loadFoods();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}