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
import com.example.gogo.repository.HealthRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class UpdateProfileInfoAdapter extends RecyclerView.Adapter<UpdateProfileInfoAdapter.UpdateProfileInfoViewHolder> {
    private Context context;
    private String googleId;
    private HealthRepository healthRepository;
    private String originalFullName;
    private float originalHeight;
    private float originalWeight;
    private int originalDay, originalMonth, originalYear;
    private String originalGender;

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
        return 1;
    }


    class UpdateProfileInfoViewHolder extends RecyclerView.ViewHolder {
        TextInputEditText editName, editHeight, editWeight;
        NumberPicker dayPicker, monthPicker, yearPicker;
        MaterialAutoCompleteTextView dropdownGender;
        MaterialButton buttonSave;
        UpdateProfileInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            editName = itemView.findViewById(R.id.editName);
            editHeight = itemView.findViewById(R.id.editHeight);
            editWeight = itemView.findViewById(R.id.editWeight);
            dayPicker = itemView.findViewById(R.id.dayPicker);
            monthPicker = itemView.findViewById(R.id.monthPicker);
            yearPicker = itemView.findViewById(R.id.yearPicker);
            dropdownGender = itemView.findViewById(R.id.dropdownGender);
            buttonSave = itemView.findViewById(R.id.buttonSave);

            dayPicker.setMinValue(1);
            dayPicker.setMaxValue(31);
            monthPicker.setMinValue(1);
            monthPicker.setMaxValue(12);
            yearPicker.setMinValue(1900);
            yearPicker.setMaxValue(2025);

            String[] genderOptions = {"Nam", "Nữ", "Khác"};
            ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, genderOptions);
            dropdownGender.setAdapter(genderAdapter);

            buttonSave.setOnClickListener(v -> saveUserData(this));
        }

        void bind() {
            User user = healthRepository.getUserData(googleId);
            if (user != null) {
                originalFullName = user.getFullName();
                originalHeight = user.getHeight();
                originalWeight = user.getWeight();
                originalGender = user.getGender() != null ? user.getGender() : "Nam";

                Calendar today = Calendar.getInstance();
                int age = user.getAge();
                int year = today.get(Calendar.YEAR) - age;
                originalYear = year;
                originalDay = today.get(Calendar.DAY_OF_MONTH);
                originalMonth = today.get(Calendar.MONTH) + 1;

                editName.setText(originalFullName);
                editHeight.setText(String.valueOf(originalHeight));
                editWeight.setText(String.valueOf(originalWeight));
                dropdownGender.setText(originalGender, false);
                dayPicker.setValue(originalDay);
                monthPicker.setValue(originalMonth);
                yearPicker.setValue(originalYear);
            }
        }

    }

    private boolean isDataChanged(UpdateProfileInfoViewHolder holder) {
        String newFullName = holder.editName.getText().toString();
        float newHeight, newWeight;

        try {
            newHeight = Float.parseFloat(holder.editHeight.getText().toString());
            newWeight = Float.parseFloat(holder.editWeight.getText().toString());
        } catch (NumberFormatException e) {
            return false;
        }

        int newDay = holder.dayPicker.getValue();
        int newMonth = holder.monthPicker.getValue();
        int newYear = holder.yearPicker.getValue();
        String newGender = holder.dropdownGender.getText().toString();

        return !newFullName.equals(originalFullName) ||
                newHeight != originalHeight ||
                newWeight != originalWeight ||
                newDay != originalDay ||
                newMonth != originalMonth ||
                newYear != originalYear ||
                !newGender.equals(originalGender);
    }

    private void saveUserData(UpdateProfileInfoViewHolder holder) {

        if (!isDataChanged(holder)) {
            Toast.makeText(context, "Bạn chưa cập nhật thông tin. Vui lòng chỉnh sửa trước khi lưu!", Toast.LENGTH_SHORT).show();
            return;
        }

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