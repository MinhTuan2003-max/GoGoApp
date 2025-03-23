package com.example.gogo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.models.Exercise;
import com.example.gogo.models.ExercisePlan;
import com.example.gogo.repository.ExercisePlanRepository;

import java.util.ArrayList;
import java.util.List;

public class SectionedPlanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Section> sections;
    private OnPlanGroupClickListener listener;
    private ExercisePlanRepository repo;

    public SectionedPlanAdapter(List<Section> sections, OnPlanGroupClickListener listener, ExercisePlanRepository repo) {
        this.sections = sections;
        this.listener = listener;
        this.repo = repo;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isHeader ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan_group_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan_group_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Item item = getItem(position);
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.headerText.setText(item.header);
            int totalCalories = repo.getTotalCaloriesByPlanName(item.header, sections.get(0).items.get(0).getUserID());
            headerHolder.totalCaloriesText.setText("Tổng calories: " + totalCalories + " kcal");
            headerHolder.itemView.setOnClickListener(v -> listener.onPlanGroupClick(item.header));
        } else {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            ExercisePlan plan = item.plan;
            Exercise exercise = repo.getExerciseById(plan.getExerciseID());
            itemHolder.nameText.setText(exercise.getExerciseName());
            itemHolder.durationText.setText("Thời gian: " + plan.getDuration() + " phút");
            itemHolder.itemView.setOnClickListener(v -> listener.onPlanGroupClick(item.header));
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (Section section : sections) {
            count += section.items.size() + 1; // +1 cho header
        }
        return count;
    }

    private Item getItem(int position) {
        int currentPos = 0;
        for (Section section : sections) {
            if (position == currentPos) {
                return new Item(true, section.header, null);
            }
            currentPos++;
            if (position < currentPos + section.items.size()) {
                return new Item(false, section.header, section.items.get(position - currentPos));
            }
            currentPos += section.items.size();
        }
        return null;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerText, totalCaloriesText;

        HeaderViewHolder(View itemView) {
            super(itemView);
            headerText = itemView.findViewById(R.id.plan_group_header);
            totalCaloriesText = itemView.findViewById(R.id.total_calories);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, durationText;

        ItemViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.exercise_name);
            durationText = itemView.findViewById(R.id.exercise_duration);
        }
    }

    public static class Section {
        String header;
        List<ExercisePlan> items;

        public Section(String header, List<ExercisePlan> items) {
            this.header = header;
            this.items = items;
        }
    }

    private static class Item {
        boolean isHeader;
        String header;
        ExercisePlan plan;

        Item(boolean isHeader, String header, ExercisePlan plan) {
            this.isHeader = isHeader;
            this.header = header;
            this.plan = plan;
        }
    }

    public interface OnPlanGroupClickListener {
        void onPlanGroupClick(String planName);
    }
}