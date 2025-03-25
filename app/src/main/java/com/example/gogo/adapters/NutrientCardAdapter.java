package com.example.gogo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gogo.R;
import com.example.gogo.models.DietPlan;
import java.util.List;

public class NutrientCardAdapter extends RecyclerView.Adapter<NutrientCardAdapter.ViewHolder> {
    private List<DietPlan> dietPlans;
    private Context context;

    public NutrientCardAdapter(List<DietPlan> dietPlans, Context context) {
        this.dietPlans = dietPlans;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DietPlan plan = dietPlans.get(position);
        holder.text1.setText(plan.getPlanName());
//        holder.text2.setText("Completed: " + (plan.isCompleted() ? "Yes" : "No"));
    }

    @Override
    public int getItemCount() {
        return dietPlans.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;
        ViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}