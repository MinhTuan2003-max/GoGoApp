package com.example.gogo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.models.ExercisePlan;

import java.util.List;

public class ExercisePlanAdapter extends RecyclerView.Adapter<ExercisePlanAdapter.PlanViewHolder> {
    private List<ExercisePlan> planList;

    public ExercisePlanAdapter(List<ExercisePlan> planList) {
        this.planList = planList;
    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder, int position) {
        ExercisePlan plan = planList.get(position);
        holder.planIdTextView.setText("Plan ID: " + plan.getPlanID());
        holder.exerciseIdTextView.setText("Exercise ID: " + plan.getExerciseID());
        holder.durationTextView.setText("Duration: " + plan.getDuration() + " mins");
        holder.caloriesTextView.setText("Calories Burned: " + plan.getCaloriesBurned());
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView planIdTextView, exerciseIdTextView, durationTextView, caloriesTextView;

        PlanViewHolder(View itemView) {
            super(itemView);
            planIdTextView = itemView.findViewById(R.id.plan_id);
            exerciseIdTextView = itemView.findViewById(R.id.exercise_id);
            durationTextView = itemView.findViewById(R.id.plan_duration);
            caloriesTextView = itemView.findViewById(R.id.plan_calories);
        }
    }
}