package com.example.gogo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private Context context;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onDeleteUser(int userId);
        void onToggleAdmin(int userId, boolean isAdmin);
    }

    public UserAdapter(Context context, List<User> userList, OnUserActionListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getFullName());
        holder.emailTextView.setText(user.getEmail());
        holder.adminStatusTextView.setText(user.isAdmin() ? "Admin" : "User");

        if ("minhtuanha2829@gmail.com".equals(user.getEmail())) {
            holder.deleteButton.setEnabled(false);
            holder.deleteButton.setAlpha(0.5f);
            holder.toggleAdminButton.setEnabled(false);
            holder.toggleAdminButton.setAlpha(0.5f);
        } else {
            holder.deleteButton.setEnabled(true);
            holder.deleteButton.setAlpha(1.0f);
            holder.deleteButton.setOnClickListener(v -> listener.onDeleteUser(user.getUserId()));

            holder.toggleAdminButton.setEnabled(true);
            holder.toggleAdminButton.setAlpha(1.0f);
            holder.toggleAdminButton.setText(user.isAdmin() ? "Remove Admin" : "Make Admin");
            holder.toggleAdminButton.setOnClickListener(v -> listener.onToggleAdmin(user.getUserId(), user.isAdmin()));
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateList(List<User> newList) {
        userList = newList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView, adminStatusTextView;
        Button deleteButton, toggleAdminButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.user_name);
            emailTextView = itemView.findViewById(R.id.user_email);
            adminStatusTextView = itemView.findViewById(R.id.user_admin_status);
            deleteButton = itemView.findViewById(R.id.delete_button);
            toggleAdminButton = itemView.findViewById(R.id.toggle_admin_button);
        }
    }
}