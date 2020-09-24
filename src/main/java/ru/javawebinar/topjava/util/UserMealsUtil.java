package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> userMealsWithExcesses = new ArrayList<>();

        // filter meals between startTime and endTime
        List<UserMeal> mealsAtTime = new ArrayList<>();
        for (UserMeal meal : meals) {
            LocalDateTime startDateTime = startTime.atDate(LocalDate.of(meal.getDateTime().getYear(), meal.getDateTime().getMonth(), meal.getDateTime().getDayOfMonth()));
            LocalDateTime endDateTime = endTime.atDate(LocalDate.of(meal.getDateTime().getYear(), meal.getDateTime().getMonth(), meal.getDateTime().getDayOfMonth()));
            if ((meal.getDateTime().isAfter(startDateTime) && meal.getDateTime().isBefore(endDateTime)) || meal.getDateTime().isEqual(startDateTime) || meal.getDateTime().isEqual(endDateTime)) {
                mealsAtTime.add(meal);
            }
        }

        // sum calories per day
        Map<LocalDate, Integer> dayCaloriesMap = new HashMap<>();
        for (UserMeal meal : mealsAtTime) {
            LocalDate mealDate = meal.getDateTime().toLocalDate();
            int dayCalories = dayCaloriesMap.get(mealDate) == null ? meal.getCalories() : dayCaloriesMap.get(mealDate) + meal.getCalories();
            dayCaloriesMap.put(mealDate, dayCalories);
        }

        // check calories exceed per day
        for (UserMeal meal : mealsAtTime) {
            LocalDate mealDate = meal.getDateTime().toLocalDate();
            boolean excess = dayCaloriesMap.get(mealDate) > caloriesPerDay;
            userMealsWithExcesses.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess));
        }
        return userMealsWithExcesses;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMeal> userMeals = meals.stream()
                .filter(meal -> {
                    LocalDateTime startDateTime = startTime.atDate(LocalDate.of(meal.getDateTime().getYear(), meal.getDateTime().getMonth(), meal.getDateTime().getDayOfMonth()));
                    LocalDateTime endDateTime = endTime.atDate(LocalDate.of(meal.getDateTime().getYear(), meal.getDateTime().getMonth(), meal.getDateTime().getDayOfMonth()));
                    return (meal.getDateTime().isAfter(startDateTime) && meal.getDateTime().isBefore(endDateTime)) || meal.getDateTime().isEqual(startDateTime) || meal.getDateTime().isEqual(endDateTime);
                })
                .collect(toList());

        Map<LocalDate, Integer> dateCaloriesMap = userMeals.stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));

        List<UserMealWithExcess> userMealsWithExcesses = userMeals.stream()
                .filter(meal -> dateCaloriesMap.get(meal.getDateTime().toLocalDate()) < caloriesPerDay)
                .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), dateCaloriesMap.get(meal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(toList());

        return userMealsWithExcesses;
    }

}
