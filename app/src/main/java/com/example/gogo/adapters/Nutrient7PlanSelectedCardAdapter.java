package com.example.gogo.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gogo.R;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.NutritionDayPlan;
import com.example.gogo.ui.Nutrient7PlanSelectedActivity;

import java.util.List;

public class Nutrient7PlanSelectedCardAdapter extends RecyclerView.Adapter<Nutrient7PlanSelectedCardAdapter.ViewHolder> {
    private List<NutritionDayPlan> dayPlans;
    private long dietId;
    private Context context;
    private DatabaseHelper dbHelper;

    public Nutrient7PlanSelectedCardAdapter(List<NutritionDayPlan> dayPlans, long dietId, Context context) {
        this.dayPlans = dayPlans;
        this.dietId = dietId;
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nutrition_plan_selected_item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NutritionDayPlan dayPlan = dayPlans.get(position);

        // Hiển thị ngày
        holder.tvDay.setText("Day " + dayPlan.getDayNumber());

        // Xóa nội dung cũ
        holder.breakfastLayout.removeAllViews();
        holder.lunchLayout.removeAllViews();
        holder.dinnerLayout.removeAllViews();

        // Hiển thị bữa sáng
        for (String food : dayPlan.getBreakfast()) {
            TextView textView = new TextView(context);
            textView.setText(food);
            textView.setTextSize(14);
            holder.breakfastLayout.addView(textView);
        }

        // Hiển thị bữa trưa
        for (String food : dayPlan.getLunch()) {
            TextView textView = new TextView(context);
            textView.setText(food);
            textView.setTextSize(14);
            holder.lunchLayout.addView(textView);
        }

        // Hiển thị bữa tối
        for (String food : dayPlan.getDinner()) {
            TextView textView = new TextView(context);
            textView.setText(food);
            textView.setTextSize(14);
            holder.dinnerLayout.addView(textView);
        }

        // Hiển thị tổng calo
        holder.totalCaloriesTextView.setText("Tổng: " + dayPlan.getTotalCalories() + " kcal");

        // Hiển thị và xử lý checkbox
        holder.checkBoxCompleted.setChecked(dayPlan.isCompleted());
        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dayPlan.setCompleted(isChecked);
            updateDayStatusInDatabase(dayPlan.getDayNumber(), isChecked);

            // Kiểm tra nếu là ngày thứ 7 và được chọn
            if (dayPlan.getDayNumber() == 7 && isChecked) {
                showContinueDialog(holder.checkBoxCompleted); // Truyền checkbox để điều chỉnh trạng thái nếu cần
            }
        });
    }

    @Override
    public int getItemCount() {
        return dayPlans.size();
    }

    private void updateDayStatusInDatabase(int dayNumber, boolean isCompleted) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isCompleted", isCompleted ? 1 : 0);

        int rowsAffected = db.update(
                "DietPlanDayStatus",
                values,
                "DietID = ? AND DayNumber = ?",
                new String[]{String.valueOf(dietId), String.valueOf(dayNumber)}
        );

        if (rowsAffected == 0) {
            values.put("DietID", dietId);
            values.put("DayNumber", dayNumber);
            db.insert("DietPlanDayStatus", null, values);
        }

        db.close();
    }

    private void showContinueDialog(CheckBox day7CheckBox) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Hoàn thành kế hoạch");
        builder.setMessage("Bạn có muốn tiếp tục thực hiện kế hoạch này không?");
        builder.setPositiveButton("Có", (dialog, which) -> {
            resetAllDays();
        });
        builder.setNegativeButton("Không", (dialog, which) -> {
            showConfirmDeleteDialog(day7CheckBox); // Truyền checkbox vào để xử lý trường hợp "Không" xác nhận xóa
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void resetAllDays() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isCompleted", 0);

        // Cập nhật tất cả các ngày về trạng thái chưa hoàn thành
        db.update(
                "DietPlanDayStatus",
                values,
                "DietID = ?",
                new String[]{String.valueOf(dietId)}
        );

        // Cập nhật dữ liệu trong danh sách dayPlans
        for (NutritionDayPlan dayPlan : dayPlans) {
            dayPlan.setCompleted(false);
        }

        // Thông báo adapter cập nhật UI
        notifyDataSetChanged();
        db.close();
    }

    private void showConfirmDeleteDialog(CheckBox day7CheckBox) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa kế hoạch");
        builder.setMessage("Bạn có chắc chắn muốn xóa kế hoạch này không?");
        builder.setPositiveButton("Có", (dialog, which) -> {
            deleteSelectedDietPlan();
        });
        builder.setNegativeButton("Không", (dialog, which) -> {
            // Nếu người dùng chọn "Không" trong xác nhận xóa, đặt checkbox ngày 7 về 0
            day7CheckBox.setChecked(false);
            dayPlans.get(6).setCompleted(false); // Ngày 7 có index 6 trong danh sách (0-based)
            updateDayStatusInDatabase(7, false);
            dialog.dismiss();
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void deleteSelectedDietPlan() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(
                "SelectedDietPlan",
                "DietID = ?",
                new String[]{String.valueOf(dietId)}
        );

        // Cập nhật danh sách dayPlans để trống
        dayPlans.clear();
        notifyDataSetChanged();

        db.close();

        // Kết thúc activity sau khi xóa
        if (context instanceof Nutrient7PlanSelectedActivity) {
            ((Nutrient7PlanSelectedActivity) context).finish();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        LinearLayout breakfastLayout;
        LinearLayout lunchLayout;
        LinearLayout dinnerLayout;
        TextView totalCaloriesTextView;
        CheckBox checkBoxCompleted;

        ViewHolder(View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            breakfastLayout = itemView.findViewById(R.id.breakfastLayout);
            lunchLayout = itemView.findViewById(R.id.lunchLayout);
            dinnerLayout = itemView.findViewById(R.id.dinnerLayout);
            totalCaloriesTextView = itemView.findViewById(R.id.totalCaloriesTextView);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
        }
    }
}