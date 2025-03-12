package com.example.gogo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.models.NutritionDayPlan;

import java.util.List;
public class Nutrient7PlanCardAdapter extends RecyclerView.Adapter<Nutrient7PlanCardAdapter.CardViewHolder> {

    private List<NutritionDayPlan> dayPlans;

    public Nutrient7PlanCardAdapter(List<NutritionDayPlan> dayPlans) {
        this.dayPlans = dayPlans;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nutrition_plan_7day_item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        NutritionDayPlan dayPlan = dayPlans.get(position);
        holder.tvDay.setText(dayPlan.getDay());
        holder.setMeals(dayPlan.getBreakfast(), holder.breakfastLayout);
        holder.setMeals(dayPlan.getLunch(), holder.lunchLayout);
        holder.setMeals(dayPlan.getDinner(), holder.dinnerLayout);
        holder.checkBoxCompleted.setChecked(dayPlan.isCompleted());
    }

    @Override
    public int getItemCount() {
        return dayPlans.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        LinearLayout breakfastLayout, lunchLayout, dinnerLayout;
        CheckBox checkBoxCompleted;

        CardViewHolder(View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            breakfastLayout = itemView.findViewById(R.id.breakfastLayout);
            lunchLayout = itemView.findViewById(R.id.lunchLayout);
            dinnerLayout = itemView.findViewById(R.id.dinnerLayout);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
        }

        void setMeals(List<String> meals, LinearLayout layout) {
            layout.removeAllViews();
            for (String meal : meals) {
                TextView textView = new TextView(layout.getContext());
                textView.setText(meal);
                textView.setTextSize(14);
                layout.addView(textView);
            }
        }
    }
}
