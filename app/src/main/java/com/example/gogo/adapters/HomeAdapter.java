package com.example.gogo.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.ui.SettingActivity;
import com.example.gogo.ui.SleepActivity;
import com.example.gogo.ui.ViewProfileActivity;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
    private String[] functions = {"Quản lý giấc ngủ", "Quản lý dinh dưỡng", "Quản lý vận động"};
    private String[] descriptions = {
            "Theo dõi thời gian và chất lượng giấc ngủ",
            "Ghi lại chế độ ăn uống và lượng dinh dưỡng",
            "Lưu lại các bài tập và hoạt động thể chất"
    };
    private int userId = -1;


    private int[] icons = {R.drawable.ic_sleep, R.drawable.ic_nutrition, R.drawable.ic_exercise};
    private Context context;

    public HomeAdapter(Context context) {
        this.context = context;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_function, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.functionText.setText(functions[position]);
        holder.descriptionText.setText(descriptions[position]);
        holder.iconView.setImageResource(icons[position]);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (position) {
                    case 0:
                        intent = new Intent(context, SleepActivity.class);
                        intent.putExtra("USER_ID", userId);
                        context.startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(context, ViewProfileActivity.class);
                        break;
                    case 2:
                        intent = new Intent(context, SettingActivity.class);
                        break;
                    default:
                        return;
                }
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return functions.length;
    }

    static class HomeViewHolder extends RecyclerView.ViewHolder {
        ImageView iconView;
        TextView functionText;
        TextView descriptionText;
        ImageView arrowIcon;
        CardView cardView;

        HomeViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            iconView = itemView.findViewById(R.id.function_icon);
            functionText = itemView.findViewById(R.id.function_text);
            descriptionText = itemView.findViewById(R.id.function_description);
            arrowIcon = itemView.findViewById(R.id.arrow_icon);
        }
    }
}