//package com.example.gogo.database;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
//public class DatabaseHelper extends SQLiteOpenHelper {
//    private static final String TAG = "DatabaseHelper";
//    private static final String DATABASE_NAME = "gogoapptest.db";
//    private static final int DATABASE_VERSION = 6; // Tăng version để thêm bảng mới
//
//    private static final String CREATE_USERS_TABLE =
//            "CREATE TABLE Users (" +
//                    "UserID INTEGER PRIMARY KEY AUTOINCREMENT," +
//                    "FullName TEXT NOT NULL," +
//                    "Email TEXT NOT NULL UNIQUE," +
//                    "GoogleID TEXT NOT NULL UNIQUE," +
//                    "Age INTEGER," +
//                    "Gender TEXT," +
//                    "Height REAL," +
//                    "Weight REAL," +
//                    "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP," +
//                    "ProfileImageUrl TEXT NOT NULL" +
//                    ");";
//
//    private static final String CREATE_FOOD_TABLE =
//            "CREATE TABLE Food (" +
//                    "FoodID INTEGER PRIMARY KEY," +
//                    "FoodName TEXT NOT NULL," +
//                    "Description TEXT" +
//                    ");";
//
//    private static final String CREATE_DIET_PLAN_TABLE =
//            "CREATE TABLE DietPlan (" +
//                    "DietID INTEGER PRIMARY KEY AUTOINCREMENT," +
//                    "UserID INTEGER NOT NULL," +
//                    "PlanName TEXT NOT NULL," +
//                    "Description TEXT," +
//                    "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP," +
//                    "FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE" +
//                    ");";
//
//    private static final String CREATE_DIET_PLAN_FOOD_TABLE =
//            "CREATE TABLE DietPlanFood (" +
//                    "DietFoodID INTEGER PRIMARY KEY AUTOINCREMENT," +
//                    "DietID INTEGER NOT NULL," +
//                    "DayNumber INTEGER NOT NULL," +
//                    "MealTime TEXT NOT NULL," +
//                    "FoodID INTEGER NOT NULL," +
//                    "Calories INTEGER NOT NULL," +
//                    "FOREIGN KEY (DietID) REFERENCES DietPlan(DietID)," +
//                    "FOREIGN KEY (FoodID) REFERENCES Food(FoodID)" +
//                    ");";
//
//    private static final String CREATE_NUTRITION_TABLE =
//            "CREATE TABLE Nutrition (" +
//                    "NutritionID INTEGER PRIMARY KEY AUTOINCREMENT," +
//                    "FoodID INTEGER NOT NULL," +
//                    "Carbohydrate REAL," +
//                    "Protein REAL," +
//                    "Fat REAL," +
//                    "Fiber REAL," +
//                    "Sugars REAL," +
//                    "VitaminA REAL," +
//                    "VitaminB REAL," +
//                    "VitaminC REAL," +
//                    "VitaminD REAL," +
//                    "VitaminE REAL," +
//                    "VitaminK REAL," +
//                    "Calcium REAL," +
//                    "Iron REAL," +
//                    "Potassium REAL," +
//                    "Calories INTEGER," +
//                    "FOREIGN KEY (FoodID) REFERENCES Food(FoodID) ON DELETE CASCADE" +
//                    ");";
//
//    private static final String CREATE_DIET_PLAN_DAY_STATUS_TABLE =
//            "CREATE TABLE DietPlanDayStatus (" +
//                    "DietID INTEGER," +
//                    "DayNumber INTEGER," +
//                    "isCompleted INTEGER DEFAULT 0," +
//                    "PRIMARY KEY (DietID, DayNumber)," +
//                    "FOREIGN KEY (DietID) REFERENCES DietPlan(DietID)" +
//                    ");";
//
//    private static final String CREATE_SELECTED_DIET_PLAN_TABLE =
//            "CREATE TABLE SelectedDietPlan (" +
//                    "SelectionID INTEGER PRIMARY KEY AUTOINCREMENT," +
//                    "UserID INTEGER NOT NULL," +
//                    "DietID INTEGER NOT NULL," +
//                    "SelectedAt DATETIME DEFAULT CURRENT_TIMESTAMP," +
//                    "FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE," +
//                    "FOREIGN KEY (DietID) REFERENCES DietPlan(DietID) ON DELETE CASCADE" +
//                    ");";
//
//    public DatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(CREATE_USERS_TABLE);
//        db.execSQL(CREATE_FOOD_TABLE);
//        db.execSQL(CREATE_DIET_PLAN_TABLE);
//        db.execSQL(CREATE_DIET_PLAN_FOOD_TABLE);
//        db.execSQL(CREATE_NUTRITION_TABLE);
//        db.execSQL(CREATE_DIET_PLAN_DAY_STATUS_TABLE);
//        db.execSQL(CREATE_SELECTED_DIET_PLAN_TABLE);
//
//        insertSampleData(db);
//    }
//
//    private void insertSampleData(SQLiteDatabase db) {
//        // Thêm dữ liệu người dùng mẫu
//        db.execSQL("INSERT INTO Users (FullName, Email, GoogleID, Age, Gender, Height, Weight, ProfileImageUrl) " +
//                "VALUES ('Nguyễn Văn A', 'nguyenvana@example.com', 'google123', 25, 'Nam', 175.5, 70.0, 'https://example.com/nguyenvana.jpg');");
//
//        // Thêm 60 món ăn cơ bản của người Việt Nam
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (1, 'Cơm Trắng', 'Cơm nấu từ gạo trắng');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (2, 'Phở Bò', 'Phở nước dùng bò');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (3, 'Bánh Mì Thịt', 'Bánh mì kẹp thịt heo');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (4, 'Thịt Gà Luộc', 'Gà luộc chấm muối ớt');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (5, 'Cá Kho Tộ', 'Cá kho với nước mắm');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (6, 'Trứng Chiên', 'Trứng gà chiên đơn giản');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (7, 'Rau Muống Xào', 'Rau muống xào tỏi');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (8, 'Khoai Lang Luộc', 'Khoai lang luộc chín');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (9, 'Đậu Hũ Chiên', 'Đậu hũ chiên giòn');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (10, 'Thịt Heo Kho', 'Thịt heo kho nước dừa');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (11, 'Canh Chua Cá', 'Canh chua nấu với cá lóc');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (12, 'Bún Riêu', 'Bún nước dùng cua');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (13, 'Gỏi Gà', 'Gỏi gà trộn rau răm');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (14, 'Chuối Chín', 'Chuối chín tự nhiên');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (15, 'Cam Tươi', 'Cam vắt nước hoặc ăn trực tiếp');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (16, 'Dưa Hấu', 'Dưa hấu cắt miếng');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (17, 'Bắp Luộc', 'Bắp ngô luộc chín');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (18, 'Cà Rốt Luộc', 'Cà rốt luộc mềm');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (19, 'Bí Đỏ Hấp', 'Bí đỏ hấp chín');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (20, 'Cải Thìa Luộc', 'Cải thìa luộc giữ xanh');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (21, 'Thịt Bò Xào', 'Thịt bò xào hành tây');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (22, 'Mực Nướng', 'Mực tươi nướng than');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (23, 'Tôm Hấp', 'Tôm hấp sả ớt');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (24, 'Cá Hồi Áp Chảo', 'Cá hồi áp chảo với bơ');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (25, 'Đậu Phộng Rang', 'Đậu phộng rang muối');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (26, 'Sữa Chua', 'Sữa chua không đường');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (27, 'Cháo Gà', 'Cháo gà nấu với gạo');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (28, 'Mì Gói', 'Mì gói nấu với rau');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (29, 'Bánh Xèo', 'Bánh xèo nhân tôm thịt');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (30, 'Nem Chua', 'Nem chua truyền thống');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (31, 'Dưa Leo', 'Dưa leo ăn sống');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (32, 'Ổi', 'Ổi chín cắt miếng');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (33, 'Dứa', 'Dứa tươi gọt vỏ');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (34, 'Nho', 'Nho tươi không hạt');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (35, 'Đậu Đen Hầm', 'Đậu đen hầm đường');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (36, 'Đậu Xanh', 'Chè đậu xanh');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (37, 'Khoai Mì Luộc', 'Khoai mì luộc chấm muối');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (38, 'Cá Nục Kho', 'Cá nục kho tiêu');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (39, 'Thịt Vịt Quay', 'Thịt vịt quay chấm nước mắm');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (40, 'Rau Cải Xanh', 'Rau cải xanh luộc');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (41, 'Canh Bí', 'Canh bí nấu tôm');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (42, 'Bún Chả', 'Bún chả nướng với nước mắm');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (43, 'Xôi Gà', 'Xôi nấu với gà xé');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (44, 'Khoai Tây Chiên', 'Khoai tây chiên giòn');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (45, 'Sườn Nướng', 'Sườn heo nướng mật ong');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (46, 'Canh Rau Ngót', 'Canh rau ngót nấu thịt');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (47, 'Táo', 'Táo tươi nhập khẩu');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (48, 'Hạt Điều Rang', 'Hạt điều rang muối');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (49, 'Mít', 'Mít chín tự nhiên');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (50, 'Bưởi', 'Bưởi bóc múi');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (51, 'Thịt Lợn Xay', 'Thịt lợn xay làm chả');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (52, 'Cá Thu Nướng', 'Cá thu nướng giấy bạc');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (53, 'Đậu Hà Lan', 'Đậu Hà Lan luộc');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (54, 'Sữa Tươi', 'Sữa tươi không đường');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (55, 'Bánh Tráng Nướng', 'Bánh tráng nướng trứng');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (56, 'Cải Bó Xôi', 'Cải bó xôi luộc');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (57, 'Hạt Dẻ', 'Hạt dẻ rang');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (58, 'Đu Đủ', 'Đu đủ chín cắt miếng');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (59, 'Gạo Lứt', 'Cơm gạo lứt');");
//        db.execSQL("INSERT INTO Food (FoodID, FoodName, Description) VALUES (60, 'Chả Cá', 'Chả cá chiên vàng');");
//
//        // Thêm dữ liệu dinh dưỡng cho một số món ăn (giá trị ước lượng)
//        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
//                "VALUES (1, 28.0, 2.5, 0.3, 0.5, 0.1, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 10.0, 0.5, 130.0, 130);"); // Cơm Trắng
//        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
//                "VALUES (2, 35.0, 20.0, 5.0, 2.0, 1.0, 0.0, 2.0, 5.0, 0.0, 0.0, 0.0, 30.0, 3.0, 300.0, 250);"); // Phở Bò
//        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
//                "VALUES (3, 40.0, 10.0, 8.0, 2.0, 2.0, 50.0, 1.0, 10.0, 0.0, 0.0, 0.0, 20.0, 2.0, 200.0, 300);"); // Bánh Mì Thịt
//        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
//                "VALUES (4, 0.0, 25.0, 3.0, 0.0, 0.0, 0.0, 1.5, 0.0, 0.0, 0.0, 0.0, 20.0, 1.0, 100.0, 150);"); // Thịt Gà Luộc
//        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
//                "VALUES (5, 5.0, 20.0, 10.0, 0.5, 2.0, 0.0, 1.0, 5.0, 0.0, 0.0, 0.0, 30.0, 2.5, 200.0, 200);"); // Cá Kho Tộ
//        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
//                "VALUES (6, 0.5, 6.0, 5.0, 0.0, 0.0, 80.0, 0.5, 0.0, 0.5, 0.0, 0.0, 50.0, 1.0, 55.0, 80);"); // Trứng Chiên
//        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
//                "VALUES (7, 3.0, 2.0, 2.0, 3.0, 1.0, 200.0, 0.5, 10.0, 0.0, 0.0, 50.0, 100.0, 2.0, 300.0, 40);"); // Rau Muống Xào
//        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
//                "VALUES (8, 20.0, 2.0, 0.2, 3.0, 5.0, 1000.0, 0.2, 5.0, 0.0, 0.0, 0.0, 20.0, 0.5, 400.0, 90);"); // Khoai Lang Luộc
//        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
//                "VALUES (9, 2.0, 8.0, 5.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.5, 20.0, 100.0, 1.0, 100.0, 90);"); // Đậu Hũ Chiên
//        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
//                "VALUES (10, 0.0, 20.0, 10.0, 0.0, 2.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 20.0, 2.0, 150.0, 180);"); // Thịt Heo Kho
//
//        Log.d(TAG, "Dữ liệu mẫu đã được chèn thành công");
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        if (oldVersion < 6) {
//            // Tạo bảng SelectedDietPlan cho các phiên bản cũ
//            db.execSQL(CREATE_SELECTED_DIET_PLAN_TABLE);
//        }
//    }
//}

package com.example.gogo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "gogoapptest.db";
    private static final int DATABASE_VERSION = 8; // Tăng version lên 8

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
                    "FoodID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "FoodName TEXT NOT NULL," +
                    "Description TEXT" +
                    ");";

    private static final String CREATE_DIET_PLAN_TABLE =
            "CREATE TABLE DietPlan (" +
                    "DietID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "UserID INTEGER NOT NULL," +
                    "PlanName TEXT NOT NULL," +
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
                    "Carbohydrate REAL," +
                    "Protein REAL," +
                    "Fat REAL," +
                    "Fiber REAL," +
                    "Sugars REAL," +
                    "VitaminA REAL," +
                    "VitaminB REAL," +
                    "VitaminC REAL," +
                    "VitaminD REAL," +
                    "VitaminE REAL," +
                    "VitaminK REAL," +
                    "Calcium REAL," +
                    "Iron REAL," +
                    "Potassium REAL," +
                    "Calories INTEGER," +
                    "FOREIGN KEY (FoodID) REFERENCES Food(FoodID) ON DELETE CASCADE" +
                    ");";

    private static final String CREATE_DIET_PLAN_DAY_STATUS_TABLE =
            "CREATE TABLE DietPlanDayStatus (" +
                    "DietID INTEGER," +
                    "DayNumber INTEGER," +
                    "isCompleted INTEGER DEFAULT 0," +
                    "PRIMARY KEY (DietID, DayNumber)," +
                    "FOREIGN KEY (DietID) REFERENCES DietPlan(DietID)" +
                    ");";

    private static final String CREATE_SELECTED_DIET_PLAN_TABLE =
            "CREATE TABLE SelectedDietPlan (" +
                    "SelectionID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "UserID INTEGER NOT NULL," +
                    "DietID INTEGER NOT NULL," +
                    "SelectedAt DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE," +
                    "FOREIGN KEY (DietID) REFERENCES DietPlan(DietID) ON DELETE CASCADE" +
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
        db.execSQL(CREATE_DIET_PLAN_DAY_STATUS_TABLE);
        db.execSQL(CREATE_SELECTED_DIET_PLAN_TABLE);

        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO Users (FullName, Email, GoogleID, Age, Gender, Height, Weight, ProfileImageUrl) " +
                "VALUES ('Nguyễn Văn A', 'nguyenvana@example.com', 'google123', 25, 'Nam', 175.5, 70.0, 'https://example.com/nguyenvana.jpg');");

        db.execSQL("INSERT INTO Food (FoodName, Description) VALUES ('Cơm Trắng', 'Cơm nấu từ gạo trắng');");
        db.execSQL("INSERT INTO Food (FoodName, Description) VALUES ('Phở Bò', 'Phở nước dùng bò');");
        db.execSQL("INSERT INTO Food (FoodName, Description) VALUES ('Bánh Mì Thịt', 'Bánh mì kẹp thịt heo');");
        db.execSQL("INSERT INTO Food (FoodName, Description) VALUES ('Thịt Gà Luộc', 'Gà luộc chấm muối ớt');");
        db.execSQL("INSERT INTO Food (FoodName, Description) VALUES ('Cá Kho Tộ', 'Cá kho với nước mắm');");
        db.execSQL("INSERT INTO Food (FoodName, Description) VALUES ('Trứng Chiên', 'Trứng gà chiên đơn giản');");
        db.execSQL("INSERT INTO Food (FoodName, Description) VALUES ('Rau Muống Xào', 'Rau muống xào tỏi');");
        db.execSQL("INSERT INTO Food (FoodName, Description) VALUES ('Khoai Lang Luộc', 'Khoai lang luộc chín');");
        db.execSQL("INSERT INTO Food (FoodName, Description) VALUES ('Đậu Hũ Chiên', 'Đậu hũ chiên giòn');");
        db.execSQL("INSERT INTO Food (FoodName, Description) VALUES ('Thịt Heo Kho', 'Thịt heo kho nước dừa');");

        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
                "VALUES (1, 28.0, 2.5, 0.3, 0.5, 0.1, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 10.0, 0.5, 130.0, 130);");
        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
                "VALUES (2, 35.0, 20.0, 5.0, 2.0, 1.0, 0.0, 2.0, 5.0, 0.0, 0.0, 0.0, 30.0, 3.0, 300.0, 250);");
        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
                "VALUES (3, 40.0, 10.0, 8.0, 2.0, 2.0, 50.0, 1.0, 10.0, 0.0, 0.0, 0.0, 20.0, 2.0, 200.0, 300);");
        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
                "VALUES (4, 0.0, 25.0, 3.0, 0.0, 0.0, 0.0, 1.5, 0.0, 0.0, 0.0, 0.0, 20.0, 1.0, 100.0, 150);");
        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
                "VALUES (5, 5.0, 20.0, 10.0, 0.5, 2.0, 0.0, 1.0, 5.0, 0.0, 0.0, 0.0, 30.0, 2.5, 200.0, 200);");
        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
                "VALUES (6, 0.5, 6.0, 5.0, 0.0, 0.0, 80.0, 0.5, 0.0, 0.5, 0.0, 0.0, 50.0, 1.0, 55.0, 80);");
        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
                "VALUES (7, 3.0, 2.0, 2.0, 3.0, 1.0, 200.0, 0.5, 10.0, 0.0, 0.0, 50.0, 100.0, 2.0, 300.0, 40);");
        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
                "VALUES (8, 20.0, 2.0, 0.2, 3.0, 5.0, 1000.0, 0.2, 5.0, 0.0, 0.0, 0.0, 20.0, 0.5, 400.0, 90);");
        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
                "VALUES (9, 2.0, 8.0, 5.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.5, 20.0, 100.0, 1.0, 100.0, 90);");
        db.execSQL("INSERT INTO Nutrition (FoodID, Carbohydrate, Protein, Fat, Fiber, Sugars, VitaminA, VitaminB, VitaminC, VitaminD, VitaminE, VitaminK, Calcium, Iron, Potassium, Calories) " +
                "VALUES (10, 0.0, 20.0, 10.0, 0.0, 2.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 20.0, 2.0, 150.0, 180);");

        Log.d(TAG, "Dữ liệu mẫu đã được chèn thành công");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS SelectedDietPlan");
        db.execSQL("DROP TABLE IF EXISTS DietPlanDayStatus");
        db.execSQL("DROP TABLE IF EXISTS DietPlanFood");
        db.execSQL("DROP TABLE IF EXISTS DietPlan");
        db.execSQL("DROP TABLE IF EXISTS Nutrition");
        db.execSQL("DROP TABLE IF EXISTS Food");
        db.execSQL("DROP TABLE IF EXISTS Users");
        onCreate(db);
    }
}