//////package com.example.gogo.adapters;
//////
//////import android.content.ContentValues;
//////import android.content.Context;
//////import android.database.sqlite.SQLiteDatabase;
//////import android.util.Log;
//////import android.view.LayoutInflater;
//////import android.view.View;
//////import android.view.ViewGroup;
//////import android.widget.CheckBox;
//////import android.widget.LinearLayout;
//////import android.widget.TextView;
//////import androidx.annotation.NonNull;
//////import androidx.recyclerview.widget.RecyclerView;
//////import com.example.gogo.R;
//////import com.example.gogo.database.DatabaseHelper;
//////import com.example.gogo.models.NutritionDayPlan;
//////
//////import java.util.List;
//////
//////public class Nutrient7PlanCardAdapter extends RecyclerView.Adapter<Nutrient7PlanCardAdapter.CardViewHolder> {
//////
//////    private List<NutritionDayPlan> dayPlans;
//////    private int dietId;
//////    private Context context;
//////    private DatabaseHelper dbHelper;
//////
//////    public Nutrient7PlanCardAdapter(List<NutritionDayPlan> dayPlans, int dietId, Context context) {
//////        this.dayPlans = dayPlans;
//////        this.dietId = dietId;
//////        this.context = context;
//////        this.dbHelper = new DatabaseHelper(context);
//////    }
//////
//////    @NonNull
//////    @Override
//////    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//////        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nutrition_plan_7day_item_card, parent, false);
//////        return new CardViewHolder(view);
//////    }
//////
//////    @Override
//////    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
//////        NutritionDayPlan dayPlan = dayPlans.get(position);
//////        holder.tvDay.setText(dayPlan.getDay());
//////        holder.setMeals(dayPlan.getBreakfast(), holder.breakfastLayout);
//////        holder.setMeals(dayPlan.getLunch(), holder.lunchLayout);
//////        holder.setMeals(dayPlan.getDinner(), holder.dinnerLayout);
//////        holder.checkBoxCompleted.setChecked(dayPlan.isCompleted());
//////
//////        // Xử lý sự kiện khi checkbox thay đổi
//////        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
//////            dayPlan.setCompleted(isChecked);
//////            updateCompletionStatus(dayPlan, isChecked);
//////        });
//////    }
//////
//////    @Override
//////    public int getItemCount() {
//////        return dayPlans.size();
//////    }
//////
//////    static class CardViewHolder extends RecyclerView.ViewHolder {
//////        TextView tvDay;
//////        LinearLayout breakfastLayout, lunchLayout, dinnerLayout;
//////        CheckBox checkBoxCompleted;
//////
//////        CardViewHolder(View itemView) {
//////            super(itemView);
//////            tvDay = itemView.findViewById(R.id.tvDay);
//////            breakfastLayout = itemView.findViewById(R.id.breakfastLayout);
//////            lunchLayout = itemView.findViewById(R.id.lunchLayout);
//////            dinnerLayout = itemView.findViewById(R.id.dinnerLayout);
//////            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
//////        }
//////
//////        void setMeals(List<String> meals, LinearLayout layout) {
//////            layout.removeAllViews();
//////            for (String meal : meals) {
//////                TextView textView = new TextView(layout.getContext());
//////                textView.setText(meal);
//////                textView.setTextSize(14);
//////                layout.addView(textView);
//////            }
//////        }
//////    }
//////
//////    private void updateCompletionStatus(NutritionDayPlan dayPlan, boolean isCompleted) {
//////        SQLiteDatabase db = dbHelper.getWritableDatabase();
//////        ContentValues values = new ContentValues();
//////        values.put("isCompleted", isCompleted ? 1 : 0);
//////
//////        // Cập nhật trạng thái hoàn thành cho DietPlan
//////        // Lưu ý: Hiện tại bảng DietPlan chỉ có một cột isCompleted cho cả kế hoạch.
//////        // Nếu muốn lưu trạng thái riêng cho từng ngày, cần tạo bảng mới hoặc thêm cột.
//////        db.update("DietPlan", values, "DietID = ?", new String[]{String.valueOf(dietId)});
//////        Log.d("Nutrient7PlanCardAdapter", "Updated completion status for DietID " + dietId + " to " + isCompleted);
//////        db.close();
//////    }
//////}
////
////package com.example.gogo.adapters;
////
////import android.content.ContentValues;
////import android.content.Context;
////import android.database.sqlite.SQLiteDatabase;
////import android.util.Log;
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////import android.widget.CheckBox;
////import android.widget.LinearLayout;
////import android.widget.TextView;
////import androidx.annotation.NonNull;
////import androidx.recyclerview.widget.RecyclerView;
////import com.example.gogo.R;
////import com.example.gogo.database.DatabaseHelper;
////import com.example.gogo.models.NutritionDayPlan;
////
////import java.util.List;
////
////public class Nutrient7PlanCardAdapter extends RecyclerView.Adapter<Nutrient7PlanCardAdapter.CardViewHolder> {
////
////    private List<NutritionDayPlan> dayPlans;
////    private int dietId;
////    private Context context;
////    private DatabaseHelper dbHelper;
////
////    public Nutrient7PlanCardAdapter(List<NutritionDayPlan> dayPlans, int dietId, Context context) {
////        this.dayPlans = dayPlans;
////        this.dietId = dietId;
////        this.context = context;
////        this.dbHelper = new DatabaseHelper(context);
////    }
////
////    @NonNull
////    @Override
////    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
////        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nutrition_plan_7day_item_card, parent, false);
////        return new CardViewHolder(view);
////    }
////
////    @Override
////    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
////        NutritionDayPlan dayPlan = dayPlans.get(position);
////        holder.tvDay.setText(dayPlan.getDay());
////        holder.setMeals(dayPlan.getBreakfast(), holder.breakfastLayout);
////        holder.setMeals(dayPlan.getLunch(), holder.lunchLayout);
////        holder.setMeals(dayPlan.getDinner(), holder.dinnerLayout);
////        // Comment phần hiển thị checkbox để sửa sau
////        // holder.checkBoxCompleted.setChecked(dayPlan.isCompleted());
////        // holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
////        //     dayPlan.setCompleted(isChecked);
////        //     updateCompletionStatus(dayPlan, isChecked);
////        // });
////    }
////
////    @Override
////    public int getItemCount() {
////        return dayPlans.size();
////    }
////
////    static class CardViewHolder extends RecyclerView.ViewHolder {
////        TextView tvDay;
////        LinearLayout breakfastLayout, lunchLayout, dinnerLayout;
////        CheckBox checkBoxCompleted;
////
////        CardViewHolder(View itemView) {
////            super(itemView);
////            tvDay = itemView.findViewById(R.id.tvDay);
////            breakfastLayout = itemView.findViewById(R.id.breakfastLayout);
////            lunchLayout = itemView.findViewById(R.id.lunchLayout);
////            dinnerLayout = itemView.findViewById(R.id.dinnerLayout);
////            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
////        }
////
////        void setMeals(List<String> meals, LinearLayout layout) {
////            layout.removeAllViews();
////            for (String meal : meals) {
////                TextView textView = new TextView(layout.getContext());
////                textView.setText(meal);
////                textView.setTextSize(14);
////                layout.addView(textView);
////            }
////        }
////    }
////
////    private void updateCompletionStatus(NutritionDayPlan dayPlan, boolean isCompleted) {
////        SQLiteDatabase db = dbHelper.getWritableDatabase();
////        ContentValues values = new ContentValues();
////        values.put("isCompleted", isCompleted ? 1 : 0);
////
////        db.update("DietPlanDayStatus", values, "DietID = ? AND DayNumber = ?",
////                new String[]{String.valueOf(dietId), String.valueOf(dayPlan.getDay().split(" ")[1])});
////        Log.d("Nutrient7PlanCardAdapter", "Updated completion status for DietID " + dietId + ", Day " + dayPlan.getDay() + " to " + isCompleted);
////        db.close();
////    }
////}
//
//
//package com.example.gogo.adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.gogo.R;
//import com.example.gogo.models.NutritionDayPlan;
//
//import java.util.List;
//
//public class Nutrient7PlanCardAdapter extends RecyclerView.Adapter<Nutrient7PlanCardAdapter.ViewHolder> {
//    private List<NutritionDayPlan> dayPlans;
//    private int dietId;
//    private Context context;
//
//    public Nutrient7PlanCardAdapter(List<NutritionDayPlan> dayPlans, int dietId, Context context) {
//        this.dayPlans = dayPlans;
//        this.dietId = dietId;
//        this.context = context;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.nutrition_plan_7day_item_card, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        NutritionDayPlan dayPlan = dayPlans.get(position);
//
//        // Hiển thị ngày
//        holder.tvDay.setText("Day " + dayPlan.getDayNumber());
//
//        // Xóa nội dung cũ
//        holder.breakfastLayout.removeAllViews();
//        holder.lunchLayout.removeAllViews();
//        holder.dinnerLayout.removeAllViews();
//
//        // Hiển thị bữa sáng
//        for (String food : dayPlan.getBreakfast()) {
//            TextView textView = new TextView(context);
//            textView.setText(food);
//            textView.setTextSize(14);
//            holder.breakfastLayout.addView(textView);
//        }
//
//        // Hiển thị bữa trưa
//        for (String food : dayPlan.getLunch()) {
//            TextView textView = new TextView(context);
//            textView.setText(food);
//            textView.setTextSize(14);
//            holder.lunchLayout.addView(textView);
//        }
//
//        // Hiển thị bữa tối
//        for (String food : dayPlan.getDinner()) {
//            TextView textView = new TextView(context);
//            textView.setText(food);
//            textView.setTextSize(14);
//            holder.dinnerLayout.addView(textView);
//        }
//
//        // Hiển thị tổng calo
//        holder.totalCaloriesTextView.setText("Tổng: " + dayPlan.getTotalCalories() + " kcal");
//
//        // Hiển thị trạng thái hoàn thành
//        holder.checkBoxCompleted.setChecked(dayPlan.isCompleted());
//    }
//
//    @Override
//    public int getItemCount() {
//        return dayPlans.size();
//    }
//
//    static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView tvDay;
//        LinearLayout breakfastLayout;
//        LinearLayout lunchLayout;
//        LinearLayout dinnerLayout;
//        TextView totalCaloriesTextView;
//        CheckBox checkBoxCompleted;
//
//        ViewHolder(View itemView) {
//            super(itemView);
//            tvDay = itemView.findViewById(R.id.tvDay);
//            breakfastLayout = itemView.findViewById(R.id.breakfastLayout);
//            lunchLayout = itemView.findViewById(R.id.lunchLayout);
//            dinnerLayout = itemView.findViewById(R.id.dinnerLayout);
//            totalCaloriesTextView = itemView.findViewById(R.id.totalCaloriesTextView);
//            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
//        }
//    }
//}
package com.example.gogo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.models.NutritionDayPlan;

import java.util.List;

public class Nutrient7PlanCardAdapter extends RecyclerView.Adapter<Nutrient7PlanCardAdapter.ViewHolder> {
    private List<NutritionDayPlan> dayPlans;
    private int dietId;
    private Context context;

    public Nutrient7PlanCardAdapter(List<NutritionDayPlan> dayPlans, int dietId, Context context) {
        this.dayPlans = dayPlans;
        this.dietId = dietId;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nutrition_plan_7day_item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NutritionDayPlan dayPlan = dayPlans.get(position);

        holder.tvDay.setText("Day " + dayPlan.getDayNumber());

        holder.breakfastLayout.removeAllViews();
        holder.lunchLayout.removeAllViews();
        holder.dinnerLayout.removeAllViews();

        for (String food : dayPlan.getBreakfast()) {
            TextView textView = new TextView(context);
            textView.setText(food);
            textView.setTextSize(14);
            holder.breakfastLayout.addView(textView);
        }

        for (String food : dayPlan.getLunch()) {
            TextView textView = new TextView(context);
            textView.setText(food);
            textView.setTextSize(14);
            holder.lunchLayout.addView(textView);
        }

        for (String food : dayPlan.getDinner()) {
            TextView textView = new TextView(context);
            textView.setText(food);
            textView.setTextSize(14);
            holder.dinnerLayout.addView(textView);
        }

        holder.totalCaloriesTextView.setText("Tổng: " + dayPlan.getTotalCalories() + " kcal");
    }

    @Override
    public int getItemCount() {
        return dayPlans.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        LinearLayout breakfastLayout;
        LinearLayout lunchLayout;
        LinearLayout dinnerLayout;
        TextView totalCaloriesTextView;

        ViewHolder(View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            breakfastLayout = itemView.findViewById(R.id.breakfastLayout);
            lunchLayout = itemView.findViewById(R.id.lunchLayout);
            dinnerLayout = itemView.findViewById(R.id.dinnerLayout);
            totalCaloriesTextView = itemView.findViewById(R.id.totalCaloriesTextView);
        }
    }
}