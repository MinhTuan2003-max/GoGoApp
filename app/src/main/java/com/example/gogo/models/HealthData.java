package com.example.gogo.models;

public class HealthData {
    public float bmi;
    public float bmr;

    public HealthData() {}

    public HealthData(float bmi, float bmr) {
        this.bmi = bmi;
        this.bmr = bmr;
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public float getBmr() {
        return bmr;
    }

    public void setBmr(float bmr) {
        this.bmr = bmr;
    }
}
