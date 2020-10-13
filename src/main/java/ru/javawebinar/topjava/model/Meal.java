package ru.javawebinar.topjava.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Random;

@Getter
@Setter
public class Meal {
    private String id;

    private LocalDateTime dateTime;

    private String description;

    private int calories;

    public Meal(LocalDateTime dateTime, String description, int calories) {
        this.id = generateId();
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
    }

    public Meal(String id, LocalDateTime dateTime, String description, int calories) {
        this.id = id;
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
    }

    private String generateId() {
        Random random = new Random();
        return String.valueOf(random.nextInt());
    }
}
