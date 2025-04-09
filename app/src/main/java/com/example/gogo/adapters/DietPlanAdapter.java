//package com.example.gogo.adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.gogo.R;
//import com.example.gogo.models.DietPlan;
//import java.util.List;
//
//public class DietPlanAdapter extends RecyclerView.Adapter<DietPlanAdapter.DietPlanViewHolder> {
//
//    private final Context context;
//    private final List<DietPlan> dietPlans;
//    private final OnItemClickListener onItemClickListener;
//
//    public interface OnItemClickListener {
//        void onItemClick(DietPlan dietPlan);
//    }
//
//    public DietPlanAdapter(Context context, List<DietPlan> dietPlans, OnItemClickListener onItemClickListener) {
//        this.context = context;
//        this.dietPlans = dietPlans;
//        this.onItemClickListener = onItemClickListener;
//    }
//
//    @NonNull
//    @Override
//    public DietPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nutrient_item_card_sugget_plan, parent, false);
//        return new DietPlanViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull DietPlanViewHolder holder, int position) {
//        DietPlan dietPlan = dietPlans.get(position);
//        holder.cardTitle.setText(dietPlan.getPlanName());
//        holder.cardDescription.setText(dietPlan.getDescription());
//        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(dietPlan));
//    }
//
//    @Override
//    public int getItemCount() {
//        return dietPlans.size();
//    }
//
//    static class DietPlanViewHolder extends RecyclerView.ViewHolder {
//        TextView cardTitle;
//        TextView cardDescription;
//
//        DietPlanViewHolder(View itemView) {
//            super(itemView);
//            cardTitle = itemView.findViewById(R.id.cardTitle);
//            cardDescription = itemView.findViewById(R.id.cardDescription);
//        }
//    }
//}
package com.example.gogo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.models.DietPlan;

import java.util.List;

public class DietPlanAdapter extends RecyclerView.Adapter<DietPlanAdapter.DietPlanViewHolder> {

    private final Context context;
    private final List<DietPlan> dietPlans;
    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(DietPlan dietPlan);
    }

    public DietPlanAdapter(Context context, List<DietPlan> dietPlans, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.dietPlans = dietPlans;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public DietPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nutrient_item_card_sugget_plan, parent, false);
        return new DietPlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DietPlanViewHolder holder, int position) {
        DietPlan dietPlan = dietPlans.get(position);
        holder.cardTitle.setText(dietPlan.getPlanName());
        holder.cardDescription.setText(dietPlan.getDescription());
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(dietPlan));
    }

    @Override
    public int getItemCount() {
        return dietPlans.size();
    }

    static class DietPlanViewHolder extends RecyclerView.ViewHolder {
        TextView cardTitle;
        TextView cardDescription;

        DietPlanViewHolder(View itemView) {
            super(itemView);
            cardTitle = itemView.findViewById(R.id.cardTitle);
            cardDescription = itemView.findViewById(R.id.cardDescription);
        }
    }
}