package com.example.gogo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.respository.HealthRepository;

public class BodyMeasurementsAdapter extends RecyclerView.Adapter<BodyMeasurementsAdapter.BodyMeasurementsViewHolder> {
    private Context context; // Thêm context
    private HealthRepository healthRepository;
    private String googleId;

    // Constructor nhận context
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
        return 1; // Chỉ có 1 item
    }

    class BodyMeasurementsViewHolder extends RecyclerView.ViewHolder {
        // Các view đã khai báo
        Context context; // Thêm context

        BodyMeasurementsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.context = itemView.getContext(); // Lấy context từ itemView
            // Khởi tạo các view
        }

        void bind() {
            // Sử dụng context trong bind()
        }
    }
}