package com.example.gogo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.models.Food;
import com.example.gogo.models.Nutrition;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private Context context;
    private List<Food> foodList;
    private OnFoodActionListener listener;

    public FoodAdapter(Context context, List<Food> foodList, OnFoodActionListener listener) {
        this.context = context;
        this.foodList = foodList;
        this.listener = listener;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FoodViewHolder holder, int position) {
        Food food = foodList.get(position);

        // Hiển thị tên thực phẩm
        holder.foodName.setText(food.getName());

        // Lấy tổng calories từ nutritionList
        List<Nutrition> nutritionList = food.getNutritionList();
        int totalCalories = 0;
        double totalCarbs = 0, totalProtein = 0, totalFat = 0;

        if (nutritionList != null && !nutritionList.isEmpty()) {
            for (Nutrition nutrition : nutritionList) {
                totalCalories += nutrition.getCalories();
                totalCarbs += nutrition.getCarbohydrate();
                totalProtein += nutrition.getProtein();
                totalFat += nutrition.getFat();
            }
            holder.foodCalories.setText("Total Calories: " + totalCalories + " kcal");
        } else {
            holder.foodCalories.setText("Total Calories: 0 kcal");
        }

        // Hiển thị thông tin dinh dưỡng chính
        StringBuilder nutritionDetails = new StringBuilder();
        if (nutritionList != null && !nutritionList.isEmpty()) {
            nutritionDetails.append("Carbs: ").append(String.format("%.1f", totalCarbs)).append("g, ");
            nutritionDetails.append("Protein: ").append(String.format("%.1f", totalProtein)).append("g, ");
            nutritionDetails.append("Fat: ").append(String.format("%.1f", totalFat)).append("g");
        } else {
            nutritionDetails.append("No nutrition data available");
        }
        holder.foodNutritionDetails.setText(nutritionDetails.toString());

        // Thiết lập sự kiện cho nút
        holder.updateButton.setOnClickListener(v -> listener.onUpdateFood(food.getFoodId()));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteFood(food.getFoodId()));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void updateList(List<Food> newList) {
        foodList.clear();
        foodList.addAll(newList);
        notifyDataSetChanged();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, foodCalories, foodNutritionDetails;
        MaterialButton updateButton, deleteButton;

        FoodViewHolder(View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.food_name);
            foodCalories = itemView.findViewById(R.id.food_calories);
            foodNutritionDetails = itemView.findViewById(R.id.food_nutrition_details);
            updateButton = itemView.findViewById(R.id.update_food_button);
            deleteButton = itemView.findViewById(R.id.delete_food_button);
        }
    }

    public interface OnFoodActionListener {
        void onUpdateFood(int foodId);
        void onDeleteFood(int foodId);
    }
}