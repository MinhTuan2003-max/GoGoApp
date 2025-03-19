package com.example.gogo.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.database.AccountDAO;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.User;
import com.example.gogo.ui.HomeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.ViewHolder> {
    private Context context;
    private User user;
    private DatabaseHelper dbHelper;
    private AccountDAO accountDAO;

    public UserInfoAdapter(Context context, User user) {
        this.context = context;
        this.user = user;
        this.dbHelper = new DatabaseHelper(context);
        this.accountDAO = new AccountDAO(dbHelper); // Initialize accountDAO here
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_info, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set up NumberPickers
        holder.numberPickerDay.setMinValue(1);
        holder.numberPickerDay.setMaxValue(31);

        holder.numberPickerMonth.setMinValue(1);
        holder.numberPickerMonth.setMaxValue(12);

        holder.numberPickerYear.setMinValue(1900);
        holder.numberPickerYear.setMaxValue(2025);

        // Set initial values from User object if available
        if (user != null) {
            if (user.getAge() > 0) {
                holder.numberPickerYear.setValue(2025 - user.getAge());
            }
            if (user.getWeight() > 0) {
                holder.editTextWeight.setText(String.valueOf(user.getWeight()));
            }
            if (user.getHeight() > 0) {
                holder.editTextHeight.setText(String.valueOf(user.getHeight()));
            }
            if (user.getGender() != null) {
                switch (user.getGender()) {
                    case "Nam":
                        holder.radioButtonMale.setChecked(true);
                        break;
                    case "Nữ":
                        holder.radioButtonFemale.setChecked(true);
                        break;
                    case "Khác":
                        holder.radioButtonOther.setChecked(true);
                        break;
                }
            }
        }

        holder.buttonSave.setOnClickListener(v -> {
            // Handle save action
            String gender = "";
            int selectedGenderId = holder.radioGroupGender.getCheckedRadioButtonId();
            if (selectedGenderId == R.id.radioButtonMale) {
                gender = "Nam";
            } else if (selectedGenderId == R.id.radioButtonFemale) {
                gender = "Nữ";
            } else if (selectedGenderId == R.id.radioButtonOther) {
                gender = "Khác";
            }

            // Safely parse weight and height, default to 0 if empty
            float weight = 0;
            float height = 0;
            try {
                String weightText = holder.editTextWeight.getText().toString();
                weight = weightText.isEmpty() ? 0 : Float.parseFloat(weightText);
                String heightText = holder.editTextHeight.getText().toString();
                height = heightText.isEmpty() ? 0 : Float.parseFloat(heightText);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Vui lòng nhập số hợp lệ cho cân nặng và chiều cao", Toast.LENGTH_SHORT).show();
                return;
            }

            int age = 2025 - holder.numberPickerYear.getValue();

            // Update database
            boolean success = accountDAO.updateUserData(user.getGoogleId(), age, gender, height, weight);

            if (success) {
                // Update user object
                user.setGender(gender);
                user.setWeight(weight);
                user.setHeight(height);
                user.setAge(age);
                Toast.makeText(context, "Đã cập nhật thông tin", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged(); // Update UI

                // Navigate to HomeActivity after successful save
                Intent intent = new Intent(context, HomeActivity.class);
                context.startActivity(intent);
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            } else {
                Toast.makeText(context, "Lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 1; // Since we're only showing one form
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public NumberPicker numberPickerDay, numberPickerMonth, numberPickerYear;
        public RadioGroup radioGroupGender;
        public RadioButton radioButtonMale;
        public RadioButton radioButtonFemale;
        public RadioButton radioButtonOther;
        public TextInputEditText editTextWeight, editTextHeight;
        public MaterialButton buttonSave;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            numberPickerDay = itemView.findViewById(R.id.numberPickerDay);
            numberPickerMonth = itemView.findViewById(R.id.numberPickerMonth);
            numberPickerYear = itemView.findViewById(R.id.numberPickerYear);
            radioGroupGender = itemView.findViewById(R.id.radioGroupGender);
            radioButtonMale = itemView.findViewById(R.id.radioButtonMale);
            radioButtonFemale = itemView.findViewById(R.id.radioButtonFemale);
            radioButtonOther = itemView.findViewById(R.id.radioButtonOther);
            editTextWeight = itemView.findViewById(R.id.editTextWeight);
            editTextHeight = itemView.findViewById(R.id.editTextHeight);
            buttonSave = itemView.findViewById(R.id.buttonSave);
        }
    }
}