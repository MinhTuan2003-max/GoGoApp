package com.example.gogo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.repository.HealthRepository;

public class BodyMeasurementsAdapter extends RecyclerView.Adapter<BodyMeasurementsAdapter.BodyMeasurementsViewHolder> {
    private Context context;
    private HealthRepository healthRepository;
    private String googleId;

    public BodyMeasurementsAdapter(Context context, HealthRepository healthRepository, String googleId) {
        this.context = context;
        this.healthRepository = healthRepository;
        this.googleId = googleId;
    }

    @NonNull
    @Override
    public BodyMeasurementsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_body_measurements, parent, false);
        return new BodyMeasurementsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BodyMeasurementsViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class BodyMeasurementsViewHolder extends RecyclerView.ViewHolder {
        Context context;

        BodyMeasurementsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.context = itemView.getContext();
        }

        void bind() {
        }
    }
}