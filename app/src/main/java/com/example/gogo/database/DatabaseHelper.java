package com.example.gogo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "gogoapp.sqlite";
    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_PATH;

    private final Context context;

    // Bảng Sleep Records
    public static final String TABLE_SLEEP_RECORDS = "sleep_records";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_SLEEP_TIME = "sleep_time";
    public static final String COLUMN_WAKE_UP_TIME = "wake_up_time";
    public static final String COLUMN_SLEEP_HOURS = "sleep_hours";

    private static final String CREATE_TABLE_SLEEP_RECORDS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SLEEP_RECORDS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_SLEEP_TIME + " TEXT, " +
                    COLUMN_WAKE_UP_TIME + " TEXT, " +
                    COLUMN_SLEEP_HOURS + " REAL)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        DATABASE_PATH = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
        copyDatabaseIfNeeded();
    }

    private void copyDatabaseIfNeeded() {
        File dbFile = new File(DATABASE_PATH);
        if (!dbFile.exists()) {
            Log.d(TAG, "Database not found. Copying from assets...");
            copyDatabase();
        } else {
            Log.d(TAG, "Database already exists.");
        }
    }

    private void copyDatabase() {
        try (InputStream inputStream = context.getAssets().open(DATABASE_NAME);
             OutputStream outputStream = new FileOutputStream(DATABASE_PATH)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            Log.d(TAG, "Database copied successfully!");

            // Sau khi sao chép, kiểm tra và tạo bảng nếu cần
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL(CREATE_TABLE_SLEEP_RECORDS);
            db.close();

        } catch (Exception e) {
            Log.e(TAG, "Error copying database: ", e);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Nếu database không được sao chép từ assets, tạo bảng tại đây
        db.execSQL(CREATE_TABLE_SLEEP_RECORDS);
        Log.d(TAG, "onCreate: Created sleep_records table.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng cũ và tạo lại nếu cần nâng cấp
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SLEEP_RECORDS);
        onCreate(db);
        Log.d(TAG, "onUpgrade: Database upgraded.");
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }
}