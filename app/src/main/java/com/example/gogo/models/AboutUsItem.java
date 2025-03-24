package com.example.gogo.models;

public class AboutUsItem {
    private int icon;
    private String title;
    private String description;

    public AboutUsItem(int icon, String title, String description) {
        this.icon = icon;
        this.title = title;
        this.description = description;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}

