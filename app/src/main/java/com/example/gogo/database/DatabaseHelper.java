package com.example.gogo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "gogoapptest.db";
    private static final int DATABASE_VERSION = 4;

    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE Users (" +
                    "UserID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "FullName TEXT NOT NULL," +
                    "Email TEXT NOT NULL UNIQUE," +
                    "GoogleID TEXT NOT NULL UNIQUE," +
                    "Age INTEGER," +
                    "Gender TEXT," +
                    "Height REAL," +
                    "Weight REAL," +
                    "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "ProfileImageUrl TEXT NOT NULL" +
                    ");";

    private static final String CREATE_FOOD_TABLE =
            "CREATE TABLE Food (" +
                    "FoodID INTEGER PRIMARY KEY," +
                    "FoodName TEXT NOT NULL," +
                    "Description TEXT" +
                    ");";

    private static final String CREATE_DIET_PLAN_TABLE =
            "CREATE TABLE DietPlan (" +
                    "DietID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "UserID INTEGER NOT NULL," +
                    "PlanName TEXT NOT NULL," +
                    "isCompleted INTEGER DEFAULT 0," +
                    "Description TEXT," +
                    "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE" +
                    ");";

    private static final String CREATE_DIET_PLAN_FOOD_TABLE =
            "CREATE TABLE DietPlanFood (" +
                    "DietFoodID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "DietID INTEGER NOT NULL," +
                    "DayNumber INTEGER NOT NULL," +
                    "MealTime TEXT NOT NULL," +
                    "FoodID INTEGER NOT NULL," +
                    "Calories INTEGER NOT NULL," +
                    "FOREIGN KEY (DietID) REFERENCES DietPlan(DietID)," +
                    "FOREIGN KEY (FoodID) REFERENCES Food(FoodID)" +
                    ");";

    private static final String CREATE_NUTRITION_TABLE =
            "CREATE TABLE Nutrition (" +
                    "NutritionID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "FoodID INTEGER NOT NULL," +
                    "Protein REAL," +
                    "Fat REAL," +
                    "Carbs REAL," +
                    "Calories INTEGER," +
                    "Fiber REAL," +
                    "Vitamins TEXT," +
                    "Minerals TEXT," +
                    "FOREIGN KEY (FoodID) REFERENCES Food(FoodID) ON DELETE CASCADE" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_FOOD_TABLE);
        db.execSQL(CREATE_DIET_PLAN_TABLE);
        db.execSQL(CREATE_DIET_PLAN_FOOD_TABLE);
        db.execSQL(CREATE_NUTRITION_TABLE);

        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // Thêm dữ liệu cho Users
        db.execSQL("INSERT INTO Users (FullName, Email, GoogleID, Age, Gender, Height, Weight, ProfileImageUrl) " +
                "VALUES ('Nguyễn Văn A', 'nguyenvana@example.com', 'google123', 25, 'Nam', 175.5, 70.0, 'https://example.com/nguyenvana.jpg');");

        // Thêm dữ liệu cho Food
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (1, 'Ức Gà', 'Ức gà nướng');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (2, 'Cá Hồi', 'Phi lê cá hồi tươi');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (3, 'Gạo Lứt', 'Gạo lứt nguyên cám');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (4, 'Bông Cải Xanh', 'Bông cải xanh hấp');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (5, 'Trứng Gà', 'Trứng gà luộc');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (6, 'Bơ', 'Quả bơ tươi');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (7, 'Hạnh Nhân', 'Hạnh nhân sống');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (8, 'Khoai Lang', 'Khoai lang nướng');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (9, 'Kê', 'Kê nấu chín');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (10, 'Rau Muống', 'Rau muống tươi');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (11, 'Thịt Bò', 'Thịt bò nướng');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (12, 'Yến Mạch', 'Yến mạch cán');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (13, 'Sữa Chua Hy Lạp', 'Sữa chua Hy Lạp không đường');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (14, 'Cá Ngừ', 'Cá ngừ đóng hộp trong nước');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (15, 'Đậu Lăng', 'Đậu lăng nấu chín');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (16, 'Chuối', 'Chuối tươi');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (17, 'Việt Quất', 'Việt quất tươi');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (18, 'Ức Gà Tây', 'Ức gà tây nướng');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (19, 'Đậu Hà Lan', 'Đậu hà lan nấu chín');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (20, 'Cải Xoăn', 'Cải xoăn tươi');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (21, 'Óc Chó', 'Óc chó sống');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (22, 'Phô Mai Nhà Làm', 'Phô mai nhà làm ít béo');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (23, 'Thịt Heo Nạc', 'Thịt heo nạc nướng');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (24, 'Đậu Cô Ve', 'Đậu cô ve hấp');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (25, 'Cá Thu', 'Cá thu tươi');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (26, 'Bơ Đậu Phộng', 'Bơ đậu phộng tự nhiên');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (27, 'Măng Tây', 'Măng tây hấp');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (28, 'Lúa Mạch', 'Lúa mạch nấu chín');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (29, 'Cá Mòi', 'Cá mòi đóng hộp');");
        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (30, 'Bắp Cải Xanh', 'Bắp cải xanh nướng');");

        // Thêm dữ liệu cho Nutrition
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (1, 1, 31.0, 3.6, 0.0, 165, 0.0, 'B6', 'Phosphorus, Selenium');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (2, 2, 25.0, 13.0, 0.0, 206, 0.0, 'D', 'Selenium, Phosphorus');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (3, 3, 2.7, 0.9, 23.0, 111, 1.8, 'B1', 'Magnesium, Manganese');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (4, 4, 3.0, 0.4, 7.0, 35, 2.6, 'C, K', 'Calcium, Potassium');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (5, 5, 6.3, 5.0, 0.6, 78, 0.0, 'A, B12', 'Phosphorus, Selenium');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (6, 6, 2.0, 15.0, 9.0, 160, 7.0, 'E, K', 'Potassium, Magnesium');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (7, 7, 21.0, 50.0, 22.0, 579, 13.0, 'E', 'Magnesium, Calcium');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (8, 8, 1.6, 0.1, 20.0, 86, 3.0, 'A', 'Potassium, Manganese');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (9, 9, 4.1, 1.9, 21.0, 120, 2.8, 'B1', 'Magnesium, Phosphorus');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (10, 10, 2.9, 0.4, 4.3, 23, 2.1, 'A, C', 'Iron, Calcium');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (11, 11, 25.0, 15.0, 0.0, 250, 0.0, 'B12', 'Iron, Zinc');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (12, 12, 13.0, 7.0, 68.0, 389, 10.0, 'B1', 'Magnesium, Phosphorus');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (13, 13, 10.0, 0.4, 4.0, 59, 0.0, 'B12', 'Calcium, Phosphorus');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (14, 14, 30.0, 1.0, 0.0, 116, 0.0, 'D', 'Selenium, Phosphorus');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (15, 15, 9.0, 0.4, 20.0, 116, 7.9, 'B1', 'Iron, Potassium');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (16, 16, 1.1, 0.3, 23.0, 89, 2.6, 'C', 'Potassium, Magnesium');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (17, 17, 0.7, 0.3, 14.0, 57, 2.4, 'C, K', 'Manganese, Vitamin C');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (18, 18, 29.0, 0.7, 0.0, 135, 0.0, 'B6', 'Phosphorus, Selenium');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (19, 19, 8.9, 2.6, 27.0, 164, 7.6, 'B6', 'Iron, Magnesium');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (20, 20, 4.3, 0.6, 4.4, 33, 2.0, 'A, C', 'Calcium, Potassium');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (21, 21, 15.0, 65.0, 14.0, 654, 6.7, 'B6', 'Magnesium, Phosphorus');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (22, 22, 11.0, 4.3, 3.0, 98, 0.0, 'B12', 'Calcium, Phosphorus');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (23, 23, 27.0, 9.0, 0.0, 193, 0.0, 'B1', 'Phosphorus, Zinc');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (24, 24, 2.0, 0.2, 7.0, 31, 2.7, 'C, K', 'Calcium, Manganese');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (25, 25, 19.0, 14.0, 0.0, 205, 0.0, 'D', 'Selenium, Phosphorus');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (26, 26, 25.0, 50.0, 20.0, 588, 8.0, 'E', 'Magnesium, Phosphorus');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (27, 27, 2.9, 0.1, 3.9, 20, 1.5, 'A, C', 'Potassium, Iron');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (28, 28, 2.3, 0.7, 24.0, 123, 3.8, 'B3', 'Selenium, Magnesium');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (29, 29, 25.0, 11.0, 0.0, 208, 0.0, 'D', 'Calcium, Phosphorus');");
        db.execSQL("INSERT INTO Nutrition (NutritionID, FoodID, Protein, Fat, Carbs, Calories, Fiber, Vitamins, Minerals) " +
                "VALUES (30, 30, 3.4, 0.3, 9.0, 43, 3.8, 'C, K', 'Vitamin C, Manganese');");

        // Thêm dữ liệu mẫu cho DietPlan
        db.execSQL("INSERT INTO DietPlan (UserID, PlanName, isCompleted, Description, CreatedAt) " +
                "VALUES (1, 'Kế hoạch 1', 0, 'Kế hoạch ăn uống cân bằng', '2025-03-18 10:00:00');");
        db.execSQL("INSERT INTO DietPlan (UserID, PlanName, isCompleted, Description, CreatedAt) " +
                "VALUES (1, 'Kế hoạch 2', 1, 'Kế hoạch giảm cân', '2025-03-18 10:01:00');");

        // Thêm dữ liệu mẫu cho DietPlanFood
        db.execSQL("INSERT INTO DietPlanFood (DietID, DayNumber, MealTime, FoodID, Calories) " +
                "VALUES (1, 1, 'Breakfast', 1, 165);");
        db.execSQL("INSERT INTO DietPlanFood (DietID, DayNumber, MealTime, FoodID, Calories) " +
                "VALUES (1, 1, 'Lunch', 2, 206);");
        db.execSQL("INSERT INTO DietPlanFood (DietID, DayNumber, MealTime, FoodID, Calories) " +
                "VALUES (1, 1, 'Dinner', 3, 111);");
        db.execSQL("INSERT INTO DietPlanFood (DietID, DayNumber, MealTime, FoodID, Calories) " +
                "VALUES (2, 1, 'Breakfast', 4, 35);");
        db.execSQL("INSERT INTO DietPlanFood (DietID, DayNumber, MealTime, FoodID, Calories) " +
                "VALUES (2, 1, 'Lunch', 5, 78);");
        db.execSQL("INSERT INTO DietPlanFood (DietID, DayNumber, MealTime, FoodID, Calories) " +
                "VALUES (2, 1, 'Dinner', 6, 160);");


        Log.d(TAG, "Dữ liệu mẫu đã được chèn thành công");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Nutrition");
        db.execSQL("DROP TABLE IF EXISTS DietPlanFood");
        db.execSQL("DROP TABLE IF EXISTS DietPlan");
        db.execSQL("DROP TABLE IF EXISTS Food");
        db.execSQL("DROP TABLE IF EXISTS Users");
        onCreate(db);
    }
}