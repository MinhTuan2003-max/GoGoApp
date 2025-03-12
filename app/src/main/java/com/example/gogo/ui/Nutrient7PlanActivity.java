package com.example.gogo.ui;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.adapters.Nutrient7PlanCardAdapter;
import com.example.gogo.models.NutritionDayPlan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Nutrient7PlanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrient_7plan);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Inside MainActivity's onCreate method
        List<NutritionDayPlan> dayPlans = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            NutritionDayPlan dayPlan = new NutritionDayPlan();
            dayPlan.setDay("Day " + i);
            dayPlan.setBreakfast(Arrays.asList("Bánh mì", "Sữa", "Trứng"));
            dayPlan.setLunch(Arrays.asList("Cơm", "Thịt kho", "Canh chua"));
            dayPlan.setDinner(Arrays.asList("Phở", "Gà rán", "Salad"));
            dayPlan.setCompleted(i % 2 == 0);
            dayPlans.add(dayPlan);
        }

        Nutrient7PlanCardAdapter adapter = new Nutrient7PlanCardAdapter(dayPlans);
        recyclerView.setAdapter(adapter);


    }
}