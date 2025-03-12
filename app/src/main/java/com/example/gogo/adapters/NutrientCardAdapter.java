package com.example.gogo.adapters;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.models.NutrientPlanCardItem;
import com.example.gogo.ui.NutrientPlanDetailActivity;

import java.util.List;

public class NutrientCardAdapter extends RecyclerView.Adapter<NutrientCardAdapter.ViewHolder> {

    private List<NutrientPlanCardItem> cardItems;
    private Context context;

    public NutrientCardAdapter(List<NutrientPlanCardItem> cardItems, Context context) {
        this.cardItems = cardItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nutrient_item_card_sugget_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NutrientPlanCardItem item = cardItems.get(position);
        holder.cardTitle.setText(item.getTitle());
        holder.cardDescription.setText(item.getDescription());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NutrientPlanDetailActivity.class);
            intent.putExtra("title", item.getTitle());
            intent.putExtra("description", item.getDescription());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView cardTitle;
        public TextView cardDescription;

        public ViewHolder(View view) {
            super(view);
            cardTitle = view.findViewById(R.id.cardTitle);
            cardDescription = view.findViewById(R.id.cardDescription);
        }
    }
}
