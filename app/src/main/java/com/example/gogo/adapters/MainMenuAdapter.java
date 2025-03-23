package com.example.gogo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.models.MainMenuItem;

import java.util.List;

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.MenuViewHolder> {
    private List<MainMenuItem> menuItems;
    private OnMenuItemClickListener listener;

    public MainMenuAdapter(List<MainMenuItem> menuItems, OnMenuItemClickListener listener) {
        this.menuItems = menuItems;
        this.listener = listener;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        MainMenuItem item = menuItems.get(position);
        holder.titleTextView.setText(item.getTitle());
        holder.iconImageView.setImageResource(item.getIconResId());
        holder.itemView.setOnClickListener(v -> listener.onMenuItemClick(item.getActivityClass()));
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView iconImageView;

        MenuViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.menu_title);
            iconImageView = itemView.findViewById(R.id.menu_icon);
        }
    }

    public interface OnMenuItemClickListener {
        void onMenuItemClick(Class<?> activityClass);
    }
}