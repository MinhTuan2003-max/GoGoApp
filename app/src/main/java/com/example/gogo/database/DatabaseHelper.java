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

        } catch (Exception e) {
            Log.e(TAG, "Error copying database: ", e);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Database already exists, skipping onCreate.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade called, but no changes needed.");
    }

    public SQLiteDatabase getDatabase(boolean writable) {
        return SQLiteDatabase.openDatabase(DATABASE_PATH, null,
                writable ? SQLiteDatabase.OPEN_READWRITE : SQLiteDatabase.OPEN_READONLY);
    }
}
