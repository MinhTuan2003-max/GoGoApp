package com.example.gogo.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
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
                Log.d(TAG, "Sign-in result received: code=" + result.getResultCode() + ", data=" + result.getData());
                Intent data = result.getData();

                if (result.getResultCode() == RESULT_OK && data != null) {
                    Log.d(TAG, "Sign-in successful, processing result");
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(task);
                } else {
                    Log.e(TAG, "Sign-in was canceled or failed: code=" + result.getResultCode());
                    Toast.makeText(MainActivity.this, "Đăng nhập bị hủy", Toast.LENGTH_SHORT).show();
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
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "GoGo Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Thông báo nhắc nhở của ứng dụng GoGo");
            channel.enableVibration(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void startReminderService() {
        Log.d(TAG, "Starting reminder service");
        Intent serviceIntent = new Intent(this, ReminderService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        // Thiết lập mặc định và lên lịch các thông báo
        ReminderManager.setupAllReminders(this);
    }

    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int resultCode = googleApi.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Google Play Services unavailable: " + resultCode);
            if (googleApi.isUserResolvableError(resultCode)) {
                googleApi.getErrorDialog(this, resultCode, 9000).show();
            } else {
                Toast.makeText(this, "Thiết bị không hỗ trợ Google Play Services", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        Log.d(TAG, "Google Play Services is available");
        return true;
    }

    private void checkPreviousSignIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Log.d(TAG, "Previous sign-in detected: " + account.getEmail());
            navigateToHomeActivity(account);
        } else {
            Log.d(TAG, "No previous sign-in detected");
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        Log.d(TAG, "Launching sign-in intent");
        signInLauncher.launch(signInIntent);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d(TAG, "Sign-in successful, account: " + (account != null ? account.getEmail() : "null"));

            if (account != null) {
                saveUserToDatabase(account);
            } else {
                Log.e(TAG, "Account is null after successful sign-in");
                Toast.makeText(MainActivity.this, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show();
            }
        } catch (ApiException e) {
            Log.w(TAG, "Sign-in failed: code=" + e.getStatusCode() + ", message=" + e.getMessage());
            String errorMessage;
            switch (e.getStatusCode()) {
                case 12501:
                    errorMessage = "Đăng nhập bị hủy bởi người dùng";
                    break;
                case 12500:
                    errorMessage = "Lỗi đăng nhập không xác định";
                    break;
                case 10:
                    errorMessage = "Lỗi cấu hình nhà phát triển (kiểm tra Google Cloud Console)";
                    break;
                default:
                    errorMessage = "Đăng nhập thất bại: " + e.getStatusMessage();
            }
            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error during sign-in: ", e);
            Toast.makeText(MainActivity.this, "Lỗi không xác định khi đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserToDatabase(GoogleSignInAccount account) {
        String googleId = account.getId();
        Log.d(TAG, "Attempting to save user with GoogleID: " + googleId);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("GoGo")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        if (accountDAO.isUserExists(googleId)) {
            Log.d(TAG, "User already exists with GoogleID: " + googleId);
            Toast.makeText(this, "Chào mừng trở lại", Toast.LENGTH_SHORT).show();
            User user = accountDAO.getUserByGoogleId(googleId);
            if (user != null) {
                Notification welcomeBackNotification = new Notification(
                        user,
                        "Chào mừng " + account.getDisplayName() + " trở lại!",
                        currentTime,
                        0,
                        0
                );
                notificationDAO.insertNotification(welcomeBackNotification);
                builder.setContentText("Chào mừng " + account.getDisplayName() + " trở lại!");
                manager.notify((int) System.currentTimeMillis(), builder.build());
            }
            navigateToHomeActivity(account);
        } else {
            boolean success = accountDAO.insertUser(
                    googleId,
                    account.getDisplayName(),
                    account.getEmail(),
                    account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "default_url"
            );

            if (success) {
                int userId = accountDAO.getUserIdByGoogleId(googleId);
                Log.d(TAG, "User saved successfully, UserID: " + userId);
                User user = accountDAO.getUserByGoogleId(googleId);
                if (user != null) {
                    Notification joinNotification = new Notification(
                            user,
                            "Chào mừng " + account.getDisplayName() + " tham gia GoGo!",
                            currentTime,
                            0,
                            0
                    );
                    notificationDAO.insertNotification(joinNotification);

                    Intent intent = new Intent(MainActivity.this, CreateProfileActivity.class);
                    intent.putExtra("USER_ID", userId);
                    intent.putExtra("GOOGLE_ID", googleId);
                    startActivity(intent);
                    finish();
                }
            } else {
                Log.e(TAG, "Failed to save user to database");
                Toast.makeText(this, "Lỗi lưu thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToHomeActivity(GoogleSignInAccount account) {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        if (account != null) {
            int userId = accountDAO.getUserIdByGoogleId(account.getId());
            intent.putExtra("USER_ID", userId);
            intent.putExtra("GOOGLE_ID", account.getId());
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}