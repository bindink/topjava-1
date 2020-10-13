package ru.javawebinar.topjava.repo;

import ru.javawebinar.topjava.model.Meal;

public interface MealRepository {
    Meal add(Meal meal);

    Meal update(Meal meal);

    void delete(String id);

    Meal get(String id);
}
