package com.example.gogo.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.models.User;
import com.example.gogo.respository.HealthRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class UpdateProfileInfoAdapter extends RecyclerView.Adapter<UpdateProfileInfoAdapter.UpdateProfileInfoViewHolder> {
    private Context context;
    private String googleId;
    private HealthRepository healthRepository;

    public UpdateProfileInfoAdapter(Context context, String googleId, HealthRepository healthRepository) {
        this.context = context;
        this.googleId = googleId;
        this.healthRepository = healthRepository;
    }

    @NonNull
    @Override
    public UpdateProfileInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_update_profile_info, parent, false);
        return new UpdateProfileInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateProfileInfoViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return 1; // Chỉ có 1 item
    }

    class UpdateProfileInfoViewHolder extends RecyclerView.ViewHolder {
        TextInputEditText editName, editHeight, editWeight;
        NumberPicker dayPicker, monthPicker, yearPicker; // Sửa thành NumberPicker
        MaterialAutoCompleteTextView dropdownGender;
        MaterialButton buttonSave;

        UpdateProfileInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            editName = itemView.findViewById(R.id.editName);
            editHeight = itemView.findViewById(R.id.editHeight);
            editWeight = itemView.findViewById(R.id.editWeight);
            dayPicker = itemView.findViewById(R.id.dayPicker); // Ánh xạ NumberPicker
            monthPicker = itemView.findViewById(R.id.monthPicker); // Ánh xạ NumberPicker
            yearPicker = itemView.findViewById(R.id.yearPicker); // Ánh xạ NumberPicker
            dropdownGender = itemView.findViewById(R.id.dropdownGender);
            buttonSave = itemView.findViewById(R.id.buttonSave);

            // Khởi tạo NumberPicker
            dayPicker.setMinValue(1);
            dayPicker.setMaxValue(31);
            monthPicker.setMinValue(1);
            monthPicker.setMaxValue(12);
            yearPicker.setMinValue(1900);
            yearPicker.setMaxValue(2025);

            // Khởi tạo dropdown cho gender
            String[] genderOptions = {"Nam", "Nữ", "Khác"};
            ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, genderOptions);
            dropdownGender.setAdapter(genderAdapter);

            buttonSave.setOnClickListener(v -> saveUserData(this));
        }

        void bind() {
            User user = healthRepository.getUserData(googleId);
            if (user != null) {
                editName.setText(user.getFullName());
                editHeight.setText(String.valueOf(user.getHeight()));
                editWeight.setText(String.valueOf(user.getWeight()));
                dropdownGender.setText(user.getGender() != null ? user.getGender() : "Nam", false); // Default "Nam"
                // Giả sử tuổi được tính từ ngày sinh
                Calendar today = Calendar.getInstance();
                int age = user.getAge();
                int year = today.get(Calendar.YEAR) - age;
                dayPicker.setValue(today.get(Calendar.DAY_OF_MONTH));
                monthPicker.setValue(today.get(Calendar.MONTH) + 1);
                yearPicker.setValue(year);
            }
        }
    }

    private void saveUserData(UpdateProfileInfoViewHolder holder) {
        try {
            String fullName = holder.editName.getText().toString();
            float height = Float.parseFloat(holder.editHeight.getText().toString());
            float weight = Float.parseFloat(holder.editWeight.getText().toString());
            int day = holder.dayPicker.getValue(); // Sử dụng getValue() thay vì getText()
            int month = holder.monthPicker.getValue();
            int year = holder.yearPicker.getValue();
            String gender = holder.dropdownGender.getText().toString();

            Calendar birthDate = Calendar.getInstance();
            birthDate.set(year, month - 1, day);
            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            Log.d("UpdateProfile", "Saving: googleId=" + googleId + ", fullName=" + fullName + ", age=" + age + ", gender=" + gender + ", height=" + height + ", weight=" + weight);
            boolean updated = healthRepository.updateUserDataForFullName(googleId, age, gender, height, weight, fullName);
            Log.d("UpdateProfile", "Update result: " + updated);

            if (updated) {
                healthRepository.saveHealthIndex(googleId);
                Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                if (context instanceof AppCompatActivity) {
                    AppCompatActivity activity = (AppCompatActivity) context;
                    activity.setResult(AppCompatActivity.RESULT_OK);
                    activity.finish();
                    Log.d("UpdateProfile", "Set result OK and finished activity");
                } else {
                    Log.w("UpdateProfile", "Context is not an AppCompatActivity, cannot set result");
                }
            } else {
                Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(context, "Vui lòng nhập dữ liệu hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
}