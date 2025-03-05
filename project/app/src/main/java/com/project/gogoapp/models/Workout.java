package com.project.gogoapp.models;

public class Workout {
    private int id;
    private String name;
    private String description;

    public Workout(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
