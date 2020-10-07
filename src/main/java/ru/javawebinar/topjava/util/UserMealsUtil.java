package ru.javawebinar.topjava.util;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

//@BenchmarkMode(Mode.AverageTime)
//@OutputTimeUnit(TimeUnit.MILLISECONDS)
//@State(Scope.Benchmark)
//@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
//@Warmup(iterations = 3)
//@Measurement(iterations = 8)
public class UserMealsUtil {
    public static void main(String[] args) throws RunnerException {

//        Benchmark runner
//        Options opt = new OptionsBuilder()
//                .include(UserMealsUtil.class.getSimpleName())
//                .forks(1)
//                .build();
//
//        new Runner(opt).run();

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

    static List<UserMeal> meals;
    static LocalTime startTime;
    static LocalTime endTime;
    static int caloriesPerDay;

    @Setup
    public void setup() {
        meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        startTime = LocalTime.of(7, 0);
        endTime = LocalTime.of(12, 0);
        caloriesPerDay = 2000;
    }

//    @Benchmark
//    public static List<UserMealWithExcess> filteredByCycles(Blackhole bh) {
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
//            dayCaloriesMap.merge(mealDate, dayCaloriesMap.get(mealDate), (oldVal, newVal) -> oldVal + newVal);
        }

        // check if calories exceed per day
        for (UserMeal meal : mealsAtTime) {
            LocalDate mealDate = meal.getDateTime().toLocalDate();
            boolean excess = dayCaloriesMap.get(mealDate) > caloriesPerDay;
            userMealsWithExcesses.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess));
        }

//        bh.consume(userMealsWithExcesses);
        return userMealsWithExcesses;
    }

    //    @Benchmark
//    public static List<UserMealWithExcess> filteredByStreams(Blackhole bh) {
    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDate, Integer> dateCaloriesMap = meals.stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));

        List<UserMealWithExcess> userMealsWithExcesses = meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), dateCaloriesMap.get(meal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(toList());

//        bh.consume(userMealsWithExcesses);
        return userMealsWithExcesses;
    }

}
