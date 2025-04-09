package com.example.gogo.models;

public class NutrientPlanCardItem {
        private String title;
        private String description;

        public NutrientPlanCardItem(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }
