package com.example.gogo.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gogo.R;
import com.example.gogo.adapters.HomeAdapter;
import com.example.gogo.adapters.NotificationAdapter;
import com.example.gogo.adapters.SliderAdapter;
import com.example.gogo.database.AccountDAO;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.database.NotificationDAO;
import com.example.gogo.models.Notification;
import com.example.gogo.models.SliderItem;
import com.example.gogo.models.User;
import com.example.gogo.service.NotificationService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private ImageView avatarImageView;
    private TextView welcomeTextView;
    private TextView userNameTextView;
    private DatabaseHelper databaseHelper;
    private AccountDAO accountDAO;
    private NotificationDAO notificationDAO;
    private GoogleSignInClient googleSignInClient;
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private ImageView notificationIcon;
    private RelativeLayout notificationContainer;
    private CardView notificationPanel;
    private TextView notificationTitle;
    private RecyclerView notificationRecyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notifications;
    private ImageView notificationBackdrop;
    private boolean isNotificationVisible = false;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        avatarImageView = findViewById(R.id.avatar);
        welcomeTextView = findViewById(R.id.greeting_text);
        userNameTextView = findViewById(R.id.user_name_text);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tab_indicator);
        recyclerView = findViewById(R.id.recycler_view_medications);
        notificationIcon = findViewById(R.id.notification_icon);

        databaseHelper = new DatabaseHelper(this);
        accountDAO = new AccountDAO(databaseHelper);
        notificationDAO = new NotificationDAO(databaseHelper);
        googleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_ID")) {
            userId = intent.getIntExtra("USER_ID", -1);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        HomeAdapter adapter = new HomeAdapter(this);
        recyclerView.setAdapter(adapter);

        notifications = new ArrayList<>();

        loadUserData();
        setupSlider();
        setupBottomNavigation();
        setupNotificationUI();

        notificationIcon.setOnClickListener(v -> toggleNotificationList());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        Intent serviceIntent = new Intent(this, NotificationService.class);
        serviceIntent.putExtra("userId", userId);
        startService(serviceIntent);
    }

    private void setupNotificationUI() {
        notificationBackdrop = new ImageView(this);
        notificationBackdrop.setBackgroundColor(ContextCompat.getColor(this, R.color.black_transparent));
        notificationBackdrop.setVisibility(View.GONE);
        notificationBackdrop.setOnClickListener(v -> toggleNotificationList());

        RelativeLayout.LayoutParams backdropParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        notificationPanel = new CardView(this);
        notificationPanel.setCardElevation(dpToPx(8));
        notificationPanel.setRadius(dpToPx(16));
        notificationPanel.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));
        notificationPanel.setVisibility(View.GONE);

        LinearLayout panelContent = new LinearLayout(this);
        panelContent.setOrientation(LinearLayout.VERTICAL);
        panelContent.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        panelContent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        notificationTitle = new TextView(this);
        notificationTitle.setText("Thông báo");
        notificationTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        notificationTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
        notificationTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        notificationTitle.setPadding(0, 0, 0, dpToPx(16));

        ImageView closeButton = new ImageView(this);
        closeButton.setImageResource(R.drawable.ic_close);
        closeButton.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        closeButton.setOnClickListener(v -> toggleNotificationList());

        LinearLayout headerContainer = new LinearLayout(this);
        headerContainer.setOrientation(LinearLayout.HORIZONTAL);
        headerContainer.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        headerContainer.addView(notificationTitle, titleParams);
        headerContainer.addView(closeButton);

        notificationRecyclerView = new RecyclerView(this);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationAdapter = new NotificationAdapter(notifications);
        notificationRecyclerView.setAdapter(notificationAdapter);
        notificationRecyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        notificationRecyclerView.setHasFixedSize(false);

        panelContent.addView(headerContainer);

        View divider = new View(this);
        divider.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(1));
        dividerParams.setMargins(0, 0, 0, dpToPx(16));
        panelContent.addView(divider, dividerParams);

        panelContent.addView(notificationRecyclerView);
        notificationPanel.addView(panelContent);

        notificationContainer = new RelativeLayout(this);
        notificationContainer.setVisibility(View.GONE);

        notificationContainer.addView(notificationBackdrop, backdropParams);

        RelativeLayout.LayoutParams panelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        panelParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        panelParams.setMargins(dpToPx(16), dpToPx(56), dpToPx(16), 0);
        notificationContainer.addView(notificationPanel, panelParams);

        ViewGroup rootView = findViewById(android.R.id.content);
        rootView.addView(notificationContainer, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void loadWelcomeBackNotifications() {
        notifications.clear();

        if (userId != -1) {
            Cursor cursor = notificationDAO.getLatestUserNotification(userId);
            try {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(0);
                    String message = cursor.getString(1);
                    String time = cursor.getString(2);
                    int isRead = cursor.getInt(3);
                    int type = cursor.getInt(4);

                    Log.d("Notification", "ID: " + id + ", Message: " + message + ", Time: " + time + ", IsRead: " + isRead + ", Type: " + type);

                    String displayMessage = (isRead == 0 ? "[Chưa xem] " : "[Đã xem] ") + message;
                    Notification notification = new Notification(id, null, displayMessage, time, isRead, type);
                    notifications.add(notification);

                    if (isRead == 0) {
                        notificationDAO.markNotificationAsRead(id);
                    }
                }
            } finally {
                cursor.close();
            }

            if (notifications.isEmpty()) {
                notifications.add(new Notification(
                        -1, null, "Không có thông báo nào", "", 1, 0));
            }
        } else {
            notifications.add(new Notification(
                    -1, null, "Không thể tải thông báo: userId không hợp lệ", "", 1, 0));
        }

        notificationAdapter.notifyDataSetChanged();
    }

    private void toggleNotificationList() {
        isNotificationVisible = !isNotificationVisible;
        if (isNotificationVisible) {
            loadWelcomeBackNotifications();
            notificationContainer.setVisibility(View.VISIBLE);
            notificationBackdrop.setVisibility(View.VISIBLE);
            notificationPanel.setTranslationY(-notificationPanel.getHeight());
            notificationPanel.setAlpha(0f);
            notificationPanel.setVisibility(View.VISIBLE);
            notificationPanel.animate()
                    .translationY(0)
                    .alpha(1f)
                    .setDuration(300)
                    .setListener(null);
            notificationBackdrop.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setListener(null);
        } else {
            notificationPanel.animate()
                    .translationY(-notificationPanel.getHeight())
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            notificationPanel.setVisibility(View.GONE);
                            notificationContainer.setVisibility(View.GONE);
                        }
                    });
            notificationBackdrop.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            notificationBackdrop.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private void loadUserData() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            User user = accountDAO.getUserByGoogleId(account.getId());
            if (user != null) {
                welcomeTextView.setText("Hello,");
                userNameTextView.setText(user.getFullName());
                if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                    Glide.with(this)
                            .load(user.getProfileImageUrl())
                            .placeholder(R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .into(avatarImageView);
                }
                userId = user.getUserId();
            } else {
                welcomeTextView.setText("Hello,");
                userNameTextView.setText(account.getDisplayName());
                if (account.getPhotoUrl() != null) {
                    Glide.with(this)
                            .load(account.getPhotoUrl())
                            .placeholder(R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .into(avatarImageView);
                }
                userId = accountDAO.getUserIdByGoogleId(account.getId());
            }
        } else {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void setupSlider() {
        List<SliderItem> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItem(R.drawable.tip_exercise, "Theo dõi sức khỏe",
                "Ghi lại cân nặng, chế độ ăn uống và thói quen tập luyện"));
        sliderItems.add(new SliderItem(R.drawable.tip_nutrition, "Lời khuyên dinh dưỡng",
                "Nhận gợi ý về chế độ ăn uống phù hợp với bạn"));
        sliderItems.add(new SliderItem(R.drawable.tip_sleep, "Cải thiện thói quen",
                "Theo dõi sự tiến bộ và xây dựng lối sống lành mạnh"));

        SliderAdapter sliderAdapter = new SliderAdapter(sliderItems, this);
        viewPager.setAdapter(sliderAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> { }).attach();

        // Auto-scroll
        if (sliderItems.size() > 1) {
            viewPager.setCurrentItem(0, true);
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    int currentPosition = viewPager.getCurrentItem();
                    if (currentPosition == sliderItems.size() - 1) {
                        viewPager.setCurrentItem(0, true);
                    } else {
                        viewPager.setCurrentItem(currentPosition + 1, true);
                    }
                    handler.postDelayed(this, 3000);
                }
            };
            handler.postDelayed(runnable, 3000);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(HomeActivity.this, ViewProfileActivity.class));
                return true;
            } else if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}