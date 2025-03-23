package com.example.gogo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.models.Exercise;
import com.example.gogo.models.ExercisePlan;
import com.example.gogo.repository.ExercisePlanRepository;

import java.util.ArrayList;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
    private List<Exercise> exercises;
    private OnExerciseClickListener clickListener;
    private OnAddToPlanClickListener addToPlanListener;
    private ExercisePlanRepository planRepo;
    private int userId = 1; // Giả định userId

    public ExerciseAdapter(List<Exercise> exercises, OnExerciseClickListener clickListener, OnAddToPlanClickListener addToPlanListener) {
        this.exercises = exercises;
        this.clickListener = clickListener;
        this.addToPlanListener = addToPlanListener;
    }

    public void setPlanRepository(ExercisePlanRepository repo) {
        this.planRepo = repo;
    }

    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.nameText.setText(exercise.getExerciseName());
        holder.categoryText.setText("Danh mục: " + exercise.getCategory());
        holder.energyText.setText("Năng lượng: " + exercise.getEnergyConsumed() + " kcal/phút");

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onExerciseClick(exercise);
            }
        });

        holder.btnAddToPlan.setOnClickListener(v -> {
            if (planRepo == null) {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("Lỗi")
                        .setMessage("Không thể thêm bài tập vào kế hoạch. Vui lòng thử lại.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            // Lấy danh sách PlanName hiện có
            List<String> existingPlanNames = planRepo.getPlanNamesByUserId(userId);
            List<String> planOptions = new ArrayList<>();
            planOptions.add("Tạo kế hoạch mới...");
            planOptions.addAll(existingPlanNames);

            // Tạo layout cho hộp thoại
            View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_select_plan, null);
            Spinner planSpinner = dialogView.findViewById(R.id.plan_spinner);
            EditText planNameInput = dialogView.findViewById(R.id.plan_name_input);
            TextView durationText = dialogView.findViewById(R.id.duration_text);
            EditText durationInput = new EditText(holder.itemView.getContext());
            durationInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            durationInput.setHint("Số phút (VD: 30)");

            // Thay TextView bằng EditText để nhập số phút
            LinearLayout layout = (LinearLayout) durationText.getParent();
            int index = layout.indexOfChild(durationText);
            layout.removeView(durationText);
            layout.addView(durationInput, index);

            // Thiết lập Spinner
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(holder.itemView.getContext(), android.R.layout.simple_spinner_item, planOptions);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            planSpinner.setAdapter(spinnerAdapter);

            // Ẩn EditText nếu chọn kế hoạch hiện có
            planSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) { // "Tạo kế hoạch mới..."
                        planNameInput.setVisibility(View.VISIBLE);
                    } else {
                        planNameInput.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {
                    planNameInput.setVisibility(View.VISIBLE);
                }
            });

            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Thêm vào kế hoạch")
                    .setView(dialogView)
                    .setPositiveButton("Thêm", (dialog, which) -> {
                        String selectedPlan = (String) planSpinner.getSelectedItem();
                        String planName;

                        if (selectedPlan.equals("Tạo kế hoạch mới...")) {
                            planName = planNameInput.getText().toString();
                            if (planName.isEmpty()) {
                                planNameInput.setError("Vui lòng nhập tên kế hoạch");
                                return;
                            }
                        } else {
                            planName = selectedPlan;
                        }

                        String durationStr = durationInput.getText().toString();
                        if (durationStr.isEmpty()) {
                            durationInput.setError("Vui lòng nhập số phút");
                            return;
                        }

                        int duration;
                        try {
                            duration = Integer.parseInt(durationStr);
                            if (duration <= 0) {
                                durationInput.setError("Số phút phải lớn hơn 0");
                                return;
                            }
                        } catch (NumberFormatException e) {
                            durationInput.setError("Vui lòng nhập số hợp lệ");
                            return;
                        }

                        int caloriesBurned = (int) (exercise.getEnergyConsumed() * duration);
                        ExercisePlan plan = new ExercisePlan(0, userId, exercise.getExerciseID(), duration, caloriesBurned, planName, null);
                        planRepo.addPlan(plan);

                        if (addToPlanListener != null) {
                            addToPlanListener.onAddToPlanClick(exercise);
                        }

                        new AlertDialog.Builder(holder.itemView.getContext())
                                .setTitle("Thành công")
                                .setMessage("Đã thêm " + exercise.getExerciseName() + " vào kế hoạch " + planName)
                                .setPositiveButton("OK", null)
                                .show();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, categoryText, energyText;
        Button btnAddToPlan;

        ExerciseViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.exercise_name);
            categoryText = itemView.findViewById(R.id.exercise_category);
            energyText = itemView.findViewById(R.id.exercise_energy);
            btnAddToPlan = itemView.findViewById(R.id.btn_add_to_plan);
        }
    }

    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
    }

    public interface OnAddToPlanClickListener {
        void onAddToPlanClick(Exercise exercise);
    }
}