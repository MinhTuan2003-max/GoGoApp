package com.example.gogo.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.gogo.R;
import com.example.gogo.database.AccountDAO;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.database.NotificationDAO;
import com.example.gogo.models.Notification;
import com.example.gogo.models.User;
import com.example.gogo.service.ReminderManager;
import com.example.gogo.service.ReminderService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "GoogleSignIn";
    private static final String CHANNEL_ID = "GoGoChannel";
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseHelper databaseHelper;
    private AccountDAO accountDAO;
    private NotificationDAO notificationDAO;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
                } else {
                    Toast.makeText(this, "Đăng nhập bị hủy", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        accountDAO = new AccountDAO(databaseHelper);
        notificationDAO = new NotificationDAO(databaseHelper);

        createNotificationChannel();
        startReminderService();

        if (!checkGooglePlayServices()) {
            finish();
            return;
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        MaterialButton googleSignInButton = findViewById(R.id.googleSignInButton);
        googleSignInButton.setOnClickListener(v -> signIn());

        checkPreviousSignIn();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "GoGo Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Thông báo nhắc nhở của ứng dụng GoGo");
            channel.enableVibration(true);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    private void startReminderService() {
        Intent serviceIntent = new Intent(this, ReminderService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        ReminderManager.setupAllReminders(this);
    }

    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int resultCode = googleApi.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApi.isUserResolvableError(resultCode)) {
                googleApi.getErrorDialog(this, resultCode, 9000).show();
            } else {
                Toast.makeText(this, "Thiết bị không hỗ trợ Google Play Services", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    private void checkPreviousSignIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            navigateToNextActivity(account);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                saveUserToDatabase(account);
            } else {
                Toast.makeText(this, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show();
            }
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Đăng nhập thất bại: " + e.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserToDatabase(GoogleSignInAccount account) {
        String googleId = account.getId();
        String email = account.getEmail();
        NotificationManager manager = getSystemService(NotificationManager.class);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("GoGo")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        if (accountDAO.isUserExists(googleId)) {
            User user = accountDAO.getUserByGoogleId(googleId);
            if (user != null) {
                Toast.makeText(this, "Chào mừng trở lại", Toast.LENGTH_SHORT).show();
                Notification welcomeBackNotification = new Notification(
                        user.getUserId(), user, "Chào mừng " + account.getDisplayName() + " trở lại!", currentTime, 0, 0);
                notificationDAO.insertNotification(welcomeBackNotification);
                builder.setContentText("Chào mừng " + account.getDisplayName() + " trở lại!");
                manager.notify((int) System.currentTimeMillis(), builder.build());
                navigateToNextActivity(account);
            } else {
                Log.e(TAG, "User exists but could not be retrieved from database for GoogleID: " + googleId);
                Toast.makeText(this, "Lỗi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        } else {
            boolean success = accountDAO.insertUser(
                    googleId, account.getDisplayName(), email,
                    account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "default_url");
            if (success) {
                User user = accountDAO.getUserByGoogleId(googleId);
                if (user != null) {
                    int userId = user.getUserId();
                    Notification joinNotification = new Notification(
                            userId, user, "Chào mừng " + account.getDisplayName() + " tham gia GoGo!", currentTime, 0, 0);
                    notificationDAO.insertNotification(joinNotification);
                    builder.setContentText("Chào mừng " + account.getDisplayName() + " tham gia GoGo!");
                    manager.notify((int) System.currentTimeMillis(), builder.build());

                    Intent intent = new Intent(MainActivity.this, CreateProfileActivity.class);
                    intent.putExtra("USER_ID", userId);
                    intent.putExtra("GOOGLE_ID", googleId);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e(TAG, "Failed to retrieve newly inserted user with GoogleID: " + googleId);
                    Toast.makeText(this, "Lỗi tải thông tin người dùng sau khi đăng ký", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Failed to insert user with GoogleID: " + googleId);
                Toast.makeText(this, "Lỗi lưu thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToNextActivity(GoogleSignInAccount account) {
        User user = accountDAO.getUserByGoogleId(account.getId());
        if (user != null) {
            Intent intent;
            if (user.isAdmin()) {
                intent = new Intent(this, ManageUsersActivity.class);
            } else {
                intent = new Intent(this, HomeActivity.class);
            }
            intent.putExtra("USER_ID", user.getUserId());
            intent.putExtra("GOOGLE_ID", account.getId());
            intent.putExtra("IS_ADMIN", user.isAdmin());
            startActivity(intent);
            finish();
        } else {
            Log.e(TAG, "Failed to retrieve user for navigation with GoogleID: " + account.getId());
            Toast.makeText(this, "Lỗi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
        }
    }
}