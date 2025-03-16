package com.example.gogo.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gogo.R;
import com.example.gogo.models.User;
import com.example.gogo.ui.UpdateProfileInfoActivity;
import com.example.gogo.ui.ViewProfileActivity;
import com.example.gogo.utils.HealthUtils;
import com.example.gogo.respository.HealthRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_PROFILE_HEADER = 0;
    private static final int VIEW_TYPE_BODY_MEASUREMENTS = 1;
    private static final int VIEW_TYPE_ACCOUNT_SETTINGS = 2;
    private static final int VIEW_TYPE_PRIVACY = 3;

    private Context context;
    private HealthRepository healthRepository;
    private String googleId;

    public ViewProfileAdapter(Context context, String googleId) {
        this.context = context;
        this.googleId = googleId;
        this.healthRepository = new HealthRepository(context);
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0: return VIEW_TYPE_PROFILE_HEADER;
            case 1: return VIEW_TYPE_BODY_MEASUREMENTS;
            case 2: return VIEW_TYPE_ACCOUNT_SETTINGS;
            case 3: return VIEW_TYPE_PRIVACY;
            default: return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case VIEW_TYPE_PROFILE_HEADER:
                return new ProfileHeaderViewHolder(inflater.inflate(R.layout.item_profile_header, parent, false));
            case VIEW_TYPE_BODY_MEASUREMENTS:
                return new BodyMeasurementsViewHolder(inflater.inflate(R.layout.item_body_measurements, parent, false));
            case VIEW_TYPE_ACCOUNT_SETTINGS:
                return new AccountSettingsViewHolder(inflater.inflate(R.layout.item_account_settings, parent, false));
            case VIEW_TYPE_PRIVACY:
                return new PrivacyViewHolder(inflater.inflate(R.layout.item_privacy_notice, parent, false));
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_PROFILE_HEADER:
                ((ProfileHeaderViewHolder) holder).bind();
                break;
            case VIEW_TYPE_BODY_MEASUREMENTS:
                ((BodyMeasurementsViewHolder) holder).bind();
                break;
            case VIEW_TYPE_ACCOUNT_SETTINGS:
                ((AccountSettingsViewHolder) holder).bind();
                break;
            case VIEW_TYPE_PRIVACY:
                ((PrivacyViewHolder) holder).bind();
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 4; // 4 phần: Profile Header, Body Measurements, Account Settings, Privacy
    }

    public void refreshData() {
        notifyDataSetChanged(); // Làm mới toàn bộ dữ liệu
    }

    class ProfileHeaderViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardProfileHeader, cardViewAvatar;
        ImageView imageProfile;
        TextView textFullName, textAge, textGender, textMember, textMemberSince;

        ProfileHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            cardProfileHeader = itemView.findViewById(R.id.cardProfileHeader);
            cardViewAvatar = itemView.findViewById(R.id.cardViewAvatar);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            textFullName = itemView.findViewById(R.id.textFullName);
            textAge = itemView.findViewById(R.id.textAge);
            textGender = itemView.findViewById(R.id.textGender);
            textMember = itemView.findViewById(R.id.textMember);
            textMemberSince = itemView.findViewById(R.id.textMemberSince);
        }

        void bind() {
            User user = healthRepository.getUserData(googleId);
            if (user != null) {
                textFullName.setText(user.getFullName() != null ? user.getFullName() : "N/A");
                textAge.setText("Tuổi: " + (user.getAge() > 0 ? user.getAge() : "N/A"));
                textGender.setText("Giới tính: " + (user.getGender() != null ? user.getGender() : "N/A"));
                String createdAt = user.getCreatedAt();
                if (createdAt != null && !createdAt.isEmpty()) {
                    try {
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date date = inputFormat.parse(createdAt);
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));
                        String formattedDate = outputFormat.format(date);
                        textMemberSince.setText(formattedDate);
                    } catch (ParseException e) {
                        textMemberSince.setText("N/A");
                        e.printStackTrace();
                    }
                } else {
                    textMemberSince.setText("N/A");
                }
                Glide.with(context).load(user.getProfileImageUrl()).into(imageProfile);
            } else {
                textFullName.setText("N/A");
                textAge.setText("Tuổi: N/A");
                textGender.setText("Giới tính: N/A");
                textMemberSince.setText("N/A");
            }
        }
    }

    class BodyMeasurementsViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardBodyMeasurements, cardHeight, cardWeight, cardBMI, cardBMR;
        ImageView iconBodyMeasurements;
        TextView textBodyMeasurementsTitle, textHeight, textWeight, textBMI, textBMICategory, textBMR, textBMRCategory;
        MaterialButton buttonUpdateMeasurements;

        BodyMeasurementsViewHolder(@NonNull View itemView) {
            super(itemView);
            cardBodyMeasurements = itemView.findViewById(R.id.cardBodyMeasurements);
            cardHeight = itemView.findViewById(R.id.cardHeight);
            cardWeight = itemView.findViewById(R.id.cardWeight);
            cardBMI = itemView.findViewById(R.id.cardBMI);
            cardBMR = itemView.findViewById(R.id.cardBMR);
            iconBodyMeasurements = itemView.findViewById(R.id.iconBodyMeasurements);
            textBodyMeasurementsTitle = itemView.findViewById(R.id.textBodyMeasurementsTitle);
            textHeight = itemView.findViewById(R.id.textHeight);
            textWeight = itemView.findViewById(R.id.textWeight);
            textBMI = itemView.findViewById(R.id.textBMI);
            textBMICategory = itemView.findViewById(R.id.textBMICategory);
            textBMR = itemView.findViewById(R.id.textBMR);
            textBMRCategory = itemView.findViewById(R.id.textBMRCategory);
            buttonUpdateMeasurements = itemView.findViewById(R.id.buttonUpdateMeasurements);

            buttonUpdateMeasurements.setOnClickListener(v -> {
                Intent intent = new Intent(context, UpdateProfileInfoActivity.class);
                intent.putExtra("GOOGLE_ID", googleId);
                if (context instanceof AppCompatActivity) {
                    Log.d("ViewProfile", "Starting UpdateProfileInfoActivity with request code " + ViewProfileActivity.REQUEST_UPDATE_PROFILE);
                    ((AppCompatActivity) context).startActivityForResult(intent, ViewProfileActivity.REQUEST_UPDATE_PROFILE);
                } else {
                    Log.w("ViewProfile", "Context is not an AppCompatActivity, cannot start activity for result");
                }
            });
        }

        void bind() {
            User user = healthRepository.getUserData(googleId);
            Log.d("ViewProfile", "Binding BodyMeasurements with user: " + (user != null ? user.toString() : "null"));
            if (user != null) {
                float height = user.getHeight();
                float weight = user.getWeight();
                int age = user.getAge();
                String gender = user.getGender();

                textHeight.setText(String.format("%.1f cm", height));
                textWeight.setText(String.format("%.1f kg", weight));

                if (height > 0 && weight > 0) {
                    float bmi = HealthUtils.calculateBMI(height, weight);
                    textBMI.setText(String.format("%.1f", bmi));
                    String bmiCategory = HealthUtils.getBMICategory(bmi);
                    textBMICategory.setText(bmiCategory);

                    int bmiColor;
                    switch (bmiCategory) {
                        case "Thiếu cân":
                            bmiColor = ContextCompat.getColor(context, R.color.bmi_underweight);
                            break;
                        case "Bình thường":
                            bmiColor = ContextCompat.getColor(context, R.color.bmi_normal);
                            break;
                        case "Thừa cân":
                            bmiColor = ContextCompat.getColor(context, R.color.bmi_overweight);
                            break;
                        case "Béo phì độ I":
                            bmiColor = ContextCompat.getColor(context, R.color.bmi_obese1);
                            break;
                        case "Béo phì độ II":
                            bmiColor = ContextCompat.getColor(context, R.color.bmi_obese2);
                            break;
                        default:
                            bmiColor = ContextCompat.getColor(context, R.color.card_background);
                            break;
                    }
                    cardBMI.setCardBackgroundColor(bmiColor);
                } else {
                    textBMI.setText("N/A");
                    textBMICategory.setText("Chưa có dữ liệu");
                    cardBMI.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_background));
                }

                if (height > 0 && weight > 0 && age > 0 && gender != null) {
                    float bmr = HealthUtils.calculateBMR(height, weight, age, gender);
                    textBMR.setText(String.format("%.0f kcal", bmr));
                    float tdee = HealthUtils.calculateTDEE(bmr, "moderate");
                    textBMRCategory.setText(String.format("TDEE: %.0f kcal", tdee));

                    int tdeeColor;
                    if (tdee < 1800) {
                        tdeeColor = ContextCompat.getColor(context, R.color.tdee_low);
                    } else if (tdee <= 2500) {
                        tdeeColor = ContextCompat.getColor(context, R.color.tdee_normal);
                    } else {
                        tdeeColor = ContextCompat.getColor(context, R.color.tdee_high);
                    }
                    cardBMR.setCardBackgroundColor(tdeeColor);
                } else {
                    textBMR.setText("N/A");
                    textBMRCategory.setText("Chưa có dữ liệu");
                    cardBMR.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_background));
                }
            } else {
                textHeight.setText("N/A");
                textWeight.setText("N/A");
                textBMI.setText("N/A");
                textBMICategory.setText("Chưa có dữ liệu");
                cardBMI.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_background));
                textBMR.setText("N/A");
                textBMRCategory.setText("Chưa có dữ liệu");
                cardBMR.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_background));
            }
        }
    }

    class AccountSettingsViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardAccountSettings;
        ImageView iconAccount;
        TextView textAccountSettingsTitle, textEmailLabel, textEmail, textAuthProviderLabel, textAuthProvider;
        MaterialButton buttonChangePassword, buttonLogout;

        AccountSettingsViewHolder(@NonNull View itemView) {
            super(itemView);
            cardAccountSettings = itemView.findViewById(R.id.cardAccountSettings);
            iconAccount = itemView.findViewById(R.id.iconAccount);
            textAccountSettingsTitle = itemView.findViewById(R.id.textAccountSettingsTitle);
            textEmailLabel = itemView.findViewById(R.id.textEmailLabel);
            textEmail = itemView.findViewById(R.id.textEmail);
            textAuthProviderLabel = itemView.findViewById(R.id.textAuthProviderLabel);
            textAuthProvider = itemView.findViewById(R.id.textAuthProvider);
            buttonChangePassword = itemView.findViewById(R.id.buttonChangePassword);
            buttonLogout = itemView.findViewById(R.id.buttonLogout);

        }

        void bind() {
            User user = healthRepository.getUserData(googleId);
            if (user != null) {
                textEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
                textAuthProvider.setText("Google");
            } else {
                textEmail.setText("N/A");
                textAuthProvider.setText("N/A");
            }
        }
    }

    class PrivacyViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardPrivacy;

        PrivacyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardPrivacy = itemView.findViewById(R.id.cardPrivacy);
        }

        void bind() {
            // Privacy logic if needed
        }
    }
}