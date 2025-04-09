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
        this.sections = sections != null ? sections : new ArrayList<>();
        this.listener = listener;
        this.repo = repo;
    }

    @Override
    public int getItemViewType(int position) {
        Item item = getItem(position);
        return item != null && item.isHeader ? TYPE_HEADER : TYPE_ITEM;
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
        if (item == null) return;

        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.headerText.setText(item.header);
            int totalCalories = (sections != null && !sections.isEmpty() && !sections.get(0).items.isEmpty())
                    ? repo.getTotalCaloriesByPlanName(item.header, sections.get(0).items.get(0).getUserID())
                    : 0;
            headerHolder.totalCaloriesText.setText("Tổng calories: " + totalCalories + " kcal");
            headerHolder.itemView.setOnClickListener(v -> listener.onPlanGroupClick(item.header));
        } else if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            ExercisePlan plan = item.plan;
            if (plan != null) {
                Exercise exercise = repo.getExerciseById(plan.getExerciseID());
                itemHolder.nameText.setText(exercise != null ? exercise.getExerciseName() : "Bài tập không xác định");
                itemHolder.durationText.setText("Thời gian: " + plan.getDuration() + " phút");
                itemHolder.itemView.setOnClickListener(v -> listener.onPlanGroupClick(item.header));
            } else {
                itemHolder.nameText.setText("Kế hoạch không hợp lệ");
                itemHolder.durationText.setText("Thời gian: N/A");
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (sections != null) {
            for (Section section : sections) {
                count += (section.items != null ? section.items.size() : 0) + 1;
            }
        }
        return count;
    }

    private Item getItem(int position) {
        if (sections == null || position < 0) return null;

        int currentPos = 0;
        for (Section section : sections) {
            if (section == null) continue;
            if (position == currentPos) {
                return new Item(true, section.header, null);
            }
            currentPos++;
            if (section.items != null && position < currentPos + section.items.size()) {
                return new Item(false, section.header, section.items.get(position - currentPos));
            }
            currentPos += section.items != null ? section.items.size() : 0;
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
            this.items = items != null ? items : new ArrayList<>();
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