package com.example.gogo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.activities.SuggestPlanActivity;

import java.util.List;

public class SuggestedExerciseAdapter extends RecyclerView.Adapter<SuggestedExerciseAdapter.SuggestedExerciseViewHolder> {
    private List<SuggestPlanActivity.SuggestedExercise> suggestedExercises;
    private OnAddToPlanClickListener addToPlanListener;

    public SuggestedExerciseAdapter(List<SuggestPlanActivity.SuggestedExercise> suggestedExercises, OnAddToPlanClickListener listener) {
        this.suggestedExercises = suggestedExercises;
        this.addToPlanListener = listener;
    }

    @Override
    public SuggestedExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggested_exercise, parent, false);
        return new SuggestedExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SuggestedExerciseViewHolder holder, int position) {
        SuggestPlanActivity.SuggestedExercise suggested = suggestedExercises.get(position);
        holder.nameText.setText(suggested.exercise.getExerciseName());
        holder.durationText.setText("Thời gian: " + suggested.minutes + " phút");
        holder.caloriesText.setText("Calories: " + suggested.caloriesBurned + " kcal");

        holder.btnAddToPlan.setOnClickListener(v -> {
            if (addToPlanListener != null) {
                addToPlanListener.onAddToPlanClick(suggested);
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestedExercises.size();
    }

    static class SuggestedExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, durationText, caloriesText;
        Button btnAddToPlan;

        SuggestedExerciseViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.exercise_name);
            durationText = itemView.findViewById(R.id.exercise_duration);
            caloriesText = itemView.findViewById(R.id.exercise_calories);
            btnAddToPlan = itemView.findViewById(R.id.btn_add_to_plan);
        }
    }

    public interface OnAddToPlanClickListener {
        void onAddToPlanClick(SuggestPlanActivity.SuggestedExercise suggestedExercise);
    }
}