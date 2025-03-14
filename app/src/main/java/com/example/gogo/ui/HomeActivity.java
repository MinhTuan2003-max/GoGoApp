package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gogo.R;
import com.example.gogo.adapters.HomeAdapter;
import com.example.gogo.adapters.SliderAdapter;
import com.example.gogo.database.AccountDAO;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.SliderItem;
import com.example.gogo.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private ImageView avatarImageView;
    private TextView welcomeTextView;
    private TextView userNameTextView;
    private DatabaseHelper databaseHelper;
    private AccountDAO accountDAO;
    private GoogleSignInClient googleSignInClient;
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        avatarImageView = findViewById(R.id.avatar);
        welcomeTextView = findViewById(R.id.greeting_text);
        userNameTextView = findViewById(R.id.user_name_text);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tab_indicator);
        recyclerView = findViewById(R.id.recycler_view_medications);

        // Setup database and Google Sign-In
        databaseHelper = new DatabaseHelper(this);
        accountDAO = new AccountDAO(databaseHelper);
        googleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        HomeAdapter adapter = new HomeAdapter(this);
        recyclerView.setAdapter(adapter);

        // Load user data and setup UI
        loadUserData();
        setupSlider();
        setupBottomNavigation();

        // Setup logout button if you want to add it
        // Note: Your layout doesn't have a logout button, add it if needed
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
            }
        } else {
            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
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
            }
            return false;
        });
    }
}