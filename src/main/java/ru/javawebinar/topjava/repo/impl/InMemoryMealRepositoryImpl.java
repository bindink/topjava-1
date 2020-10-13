package ru.javawebinar.topjava.repo.impl;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repo.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.UserServlet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.slf4j.LoggerFactory.getLogger;

public class InMemoryMealRepositoryImpl implements MealRepository {

    private static final Logger log = getLogger(InMemoryMealRepositoryImpl.class);

    @Override
    public Meal add(Meal meal) {
        log.debug("created meal with id = " + meal.getId());
        return MealsUtil.getMeals().put(meal.getId(), meal);
    }

    @Override
    public Meal update(Meal meal) {
        log.debug("updated meal with id = " + meal.getId());
        return MealsUtil.getMeals().put(meal.getId(), meal);
    }

    @Override
    public void delete(String id) {
        MealsUtil.getMeals().remove(id);
        log.debug("removed meal with id = " + id);
    }

    @Override
    public Meal get(String id) {
        Meal meal = MealsUtil.getMeals().get(id);
        log.debug("found meal with id = " + id);
        return meal;
    }


}
