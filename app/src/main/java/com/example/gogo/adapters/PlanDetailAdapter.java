package com.example.gogo.adapters;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.models.Exercise;
import com.example.gogo.models.ExerciseCompletion;
import com.example.gogo.models.ExercisePlan;
import com.example.gogo.repository.ExerciseCompletionRepository;
import com.example.gogo.repository.ExercisePlanRepository;

import java.util.List;
import java.util.Locale;

public class PlanDetailAdapter extends RecyclerView.Adapter<PlanDetailAdapter.PlanDetailViewHolder> {
    private List<ExercisePlan> planDetails;
    private ExercisePlanRepository planRepo;
    private ExerciseCompletionRepository completionRepo;
    private OnExerciseDeletedListener deleteListener;

    public PlanDetailAdapter(List<ExercisePlan> planDetails, ExercisePlanRepository planRepo, ExerciseCompletionRepository completionRepo, OnExerciseDeletedListener listener) {
        this.planDetails = planDetails;
        this.planRepo = planRepo;
        this.completionRepo = completionRepo;
        this.deleteListener = listener;
    }

    @Override
    public PlanDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan_detail, parent, false);
        return new PlanDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanDetailViewHolder holder, int position) {
        ExercisePlan plan = planDetails.get(position);
        Exercise exercise = planRepo.getExerciseById(plan.getExerciseID());
        holder.nameText.setText(exercise.getExerciseName());
        holder.durationText.setText("Thời gian: " + plan.getDuration() + " phút"); // Sử dụng Duration từ ExercisePlan
        holder.timerText.setText(formatTime(plan.getDuration() * 60 * 1000));

        holder.countDownTimer = new CountDownTimer(plan.getDuration() * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                holder.timerText.setText(formatTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                holder.timerText.setText("Hoàn thành!");
                holder.btnStartTimer.setEnabled(true);
                holder.btnStopTimer.setEnabled(false);

                ExerciseCompletion completion = new ExerciseCompletion(
                        0, plan.getPlanID(), plan.getUserID(), plan.getExerciseID(), plan.getCaloriesBurned(), null
                );
                completionRepo.addCompletion(completion);

                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("Hoàn thành bài tập")
                        .setMessage("Bạn đã hoàn thành bài tập " + exercise.getExerciseName() + "!")
                        .setPositiveButton("OK", null)
                        .show();
            }
        };

        holder.btnStartTimer.setOnClickListener(v -> {
            holder.btnStartTimer.setEnabled(false);
            holder.btnStopTimer.setEnabled(true);
            holder.countDownTimer.start();
        });

        holder.btnStopTimer.setOnClickListener(v -> {
            holder.countDownTimer.cancel();
            holder.timerText.setText(formatTime(plan.getDuration() * 60 * 1000));
            holder.btnStartTimer.setEnabled(true);
            holder.btnStopTimer.setEnabled(false);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Xóa bài tập")
                    .setMessage("Bạn có chắc chắn muốn xóa bài tập " + exercise.getExerciseName() + " khỏi kế hoạch không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        planRepo.deletePlan(plan.getPlanID());
                        planDetails.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, planDetails.size());
                        if (deleteListener != null) {
                            deleteListener.onExerciseDeleted();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    @Override
    public void onViewRecycled(PlanDetailViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.countDownTimer != null) {
            holder.countDownTimer.cancel();
        }
    }

    @Override
    public int getItemCount() {
        return planDetails.size();
    }

    private String formatTime(long millis) {
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    static class PlanDetailViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, durationText, timerText;
        Button btnStartTimer, btnStopTimer, btnDelete;
        CountDownTimer countDownTimer;

        PlanDetailViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.exercise_name);
            durationText = itemView.findViewById(R.id.exercise_duration);
            timerText = itemView.findViewById(R.id.exercise_timer);
            btnStartTimer = itemView.findViewById(R.id.btn_start_timer);
            btnStopTimer = itemView.findViewById(R.id.btn_stop_timer);
            btnDelete = itemView.findViewById(R.id.btn_delete_exercise);
        }
    }

    public interface OnExerciseDeletedListener {
        void onExerciseDeleted();
    }
}