package com.example.gogo.adapters;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

    public PlanDetailAdapter(List<ExercisePlan> planDetails, ExercisePlanRepository planRepo,
                             ExerciseCompletionRepository completionRepo, OnExerciseDeletedListener listener) {
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
        holder.durationText.setText("Thời gian: " + plan.getDuration() + " phút");
        holder.timerText.setText(formatTime(plan.getDuration() * 60 * 1000));

        boolean isCompleted = completionRepo.isExerciseCompleted(plan.getPlanID());
        updateCompletionUI(holder, isCompleted, plan);

        setupTimerAndButton(holder, plan, isCompleted);

        holder.btnToggleComplete.setOnClickListener(v -> {
            if (isCompleted) {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("Đánh dấu chưa hoàn thành")
                        .setMessage("Bạn có chắc chắn muốn đánh dấu bài tập " + exercise.getExerciseName() + " là chưa hoàn thành không?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            completionRepo.deleteCompletion(plan.getPlanID());
                            updateCompletionUI(holder, false, plan);
                            setupTimerAndButton(holder, plan, false); // Cập nhật lại ngay
                        })
                        .setNegativeButton("Không", null)
                        .show();
            } else {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("Xác nhận hoàn thành")
                        .setMessage("Bạn có chắc chắn đã hoàn thành bài tập " + exercise.getExerciseName() + "?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            markExerciseAsCompleted(plan, holder, exercise);
                        })
                        .setNegativeButton("Không", null)
                        .show();
            }
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

    private void setupTimerAndButton(PlanDetailViewHolder holder, ExercisePlan plan, boolean isCompleted) {
        if (holder.countDownTimer != null) {
            holder.countDownTimer.cancel();
        }

        if (holder.remainingTime == 0) {
            holder.remainingTime = plan.getDuration() * 60 * 1000;
        }

        if (!isCompleted) {
            holder.btnTogglePlayPause.setEnabled(true);
            holder.btnTogglePlayPause.setImageResource(R.drawable.ic_play);

            holder.countDownTimer = new CountDownTimer(holder.remainingTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    holder.remainingTime = millisUntilFinished;
                    holder.timerText.setText(formatTime(millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    holder.timerText.setText("Hoàn thành!");
                    holder.btnTogglePlayPause.setEnabled(false);
                    holder.isTimerRunning = false;
                    markExerciseAsCompleted(plan, holder, planRepo.getExerciseById(plan.getExerciseID()));
                }
            };

            holder.btnTogglePlayPause.setOnClickListener(v -> {
                if (holder.isTimerRunning) {
                    holder.countDownTimer.cancel();
                    holder.btnTogglePlayPause.setImageResource(R.drawable.ic_play);
                    holder.isTimerRunning = false;
                } else {
                    holder.countDownTimer = new CountDownTimer(holder.remainingTime, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            holder.remainingTime = millisUntilFinished;
                            holder.timerText.setText(formatTime(millisUntilFinished));
                        }

                        @Override
                        public void onFinish() {
                            holder.timerText.setText("Hoàn thành!");
                            holder.btnTogglePlayPause.setEnabled(false);
                            holder.isTimerRunning = false;
                            markExerciseAsCompleted(plan, holder, planRepo.getExerciseById(plan.getExerciseID()));
                        }
                    };
                    holder.countDownTimer.start();
                    holder.btnTogglePlayPause.setImageResource(R.drawable.ic_stop); // Chuyển sang Stop
                    holder.isTimerRunning = true;
                }
            });
        } else {
            holder.btnTogglePlayPause.setEnabled(false);
            holder.btnTogglePlayPause.setImageResource(R.drawable.ic_play); // Giữ Play nhưng vô hiệu hóa
        }
    }

    private void markExerciseAsCompleted(ExercisePlan plan, PlanDetailViewHolder holder, Exercise exercise) {
        ExerciseCompletion completion = new ExerciseCompletion(
                0, plan.getPlanID(), plan.getUserID(), plan.getExerciseID(),
                plan.getCaloriesBurned(), null, plan.getDuration()
        );
        completionRepo.addCompletion(completion);
        updateCompletionUI(holder, true, plan);
        setupTimerAndButton(holder, plan, true);

        new AlertDialog.Builder(holder.itemView.getContext())
                .setTitle("Hoàn thành bài tập")
                .setMessage("Bạn đã hoàn thành bài tập " + exercise.getExerciseName() + "!")
                .setPositiveButton("OK", null)
                .show();
    }

    private void updateCompletionUI(PlanDetailViewHolder holder, boolean isCompleted, ExercisePlan plan) {
        if (isCompleted) {
            holder.timerText.setText("Đã hoàn thành");
            holder.btnTogglePlayPause.setEnabled(false);
            holder.btnToggleComplete.setImageResource(R.drawable.ic_undo);
        } else {
            holder.timerText.setText(formatTime(holder.remainingTime));
            holder.btnTogglePlayPause.setEnabled(true);
            holder.btnToggleComplete.setImageResource(R.drawable.ic_check);
        }
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
        ImageButton btnTogglePlayPause, btnToggleComplete, btnDelete;
        CountDownTimer countDownTimer;
        long remainingTime;
        boolean isTimerRunning;

        PlanDetailViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.exercise_name);
            durationText = itemView.findViewById(R.id.exercise_duration);
            timerText = itemView.findViewById(R.id.exercise_timer);
            btnTogglePlayPause = itemView.findViewById(R.id.btn_toggle_play_pause);
            btnToggleComplete = itemView.findViewById(R.id.btn_toggle_complete);
            btnDelete = itemView.findViewById(R.id.btn_delete_exercise);
            remainingTime = 0;
            isTimerRunning = false;
        }
    }

    public interface OnExerciseDeletedListener {
        void onExerciseDeleted();
    }
}