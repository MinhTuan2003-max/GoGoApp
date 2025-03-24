package com.example.gogo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.models.SettingOption;

import java.util.List;

public class SettingOptionAdapter extends RecyclerView.Adapter<SettingOptionAdapter.ViewHolder> {

    private final List<SettingOption> options;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SettingOption option);
    }

    public SettingOptionAdapter(List<SettingOption> options, OnItemClickListener listener) {
        this.options = options;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView iconImageView;
        public TextView titleTextView;
        public ImageView arrowImageView;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.optionCardView);
            iconImageView = view.findViewById(R.id.optionIconImageView);
            titleTextView = view.findViewById(R.id.optionTitleTextView);
            arrowImageView = view.findViewById(R.id.arrowImageView);
        }

        public void bind(final SettingOption option, final OnItemClickListener listener) {
            iconImageView.setImageResource(option.getIconResId());
            titleTextView.setText(option.getTitle());
            cardView.setOnClickListener(v -> listener.onItemClick(option));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.setting_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(options.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return options.size();
    }
}