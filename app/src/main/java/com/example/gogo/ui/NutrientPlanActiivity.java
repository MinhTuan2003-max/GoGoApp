package com.example.gogo.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.adapters.NutrientCardAdapter;
import com.example.gogo.models.NutrientPlanCardItem;

import java.util.ArrayList;
import java.util.List;

public class NutrientPlanActiivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrient_sugget_plan);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<NutrientPlanCardItem> cardItems = new ArrayList<>();
        cardItems.add(new NutrientPlanCardItem("Card 1", "Description 1"));
        cardItems.add(new NutrientPlanCardItem("Card 2", "Description 2"));
        cardItems.add(new NutrientPlanCardItem("Card 3", "Description 3"));
        cardItems.add(new NutrientPlanCardItem("Card 4", "Description 4"));
        cardItems.add(new NutrientPlanCardItem("Card 5", "Description 5"));
        cardItems.add(new NutrientPlanCardItem("Card 6", "Description 6"));
        cardItems.add(new NutrientPlanCardItem("Card 7", "Description 7"));
        cardItems.add(new NutrientPlanCardItem("Card 8", "Description 8"));
        cardItems.add(new NutrientPlanCardItem("Card 9", "Description 9"));
        cardItems.add(new NutrientPlanCardItem("Card 10", "Description 10"));
        // Thêm các card khác nếu cần

        NutrientCardAdapter adapter = new NutrientCardAdapter(cardItems, this);
        recyclerView.setAdapter(adapter);
    }
}
