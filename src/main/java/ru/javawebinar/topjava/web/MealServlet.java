package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repo.MealRepository;
import ru.javawebinar.topjava.repo.impl.InMemoryMealRepositoryImpl;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.print.Book;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);

    private MealRepository mealRepository = new InMemoryMealRepositoryImpl();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getServletPath();

        switch (action) {
            case "/meals/list":
                listMeals(request, response);
                break;
            case "/meals/delete":
                deleteMeal(request, response);
                break;
            case "/meals/new":
                showNewForm(request, response);
                break;
            case "/meals/edit":
                showEditForm(request, response);
                break;
            case "/meals/update":
                updateMeal(request, response);
                break;
            case "/meals/insert":
                insertMeal(request, response);
                break;
            default:
                listMeals(request, response);
                break;
        }
    }

    private void listMeals(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("forward to meals");
        List<MealTo> mealsTo = MealsUtil.filteredByCalories(MealsUtil.getMeals().values(), MealsUtil.CALORIES_LIMIT);
        request.setAttribute("mealsTo", mealsTo);
        request.getRequestDispatcher("/meals.jsp").forward(request, response);
    }

    private void deleteMeal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");

        mealRepository.delete(id);
        response.sendRedirect("list");
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/mealForm.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        Meal existingMeal = mealRepository.get(id);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/mealForm.jsp");
        request.setAttribute("meal", existingMeal);
        dispatcher.forward(request, response);

    }

    private void insertMeal(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));

        Meal newMeal = new Meal(dateTime, description, calories);
        mealRepository.add(newMeal);
        response.sendRedirect("list");
    }

    private void updateMeal(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String id = request.getParameter("id");
        Meal mealUpdated = mealRepository.get(id);

        if (request.getParameter("dateTime") != null) {
            mealUpdated.setDateTime(LocalDateTime.parse(request.getParameter("dateTime")));
        }

        String description = request.getParameter("description");
        if (description != null) {
            mealUpdated.setDescription(description);
        }

        if (request.getParameter("calories") != null) {
            mealUpdated.setCalories(Integer.parseInt(request.getParameter("calories")));
        }

        mealRepository.update(mealUpdated);
        response.sendRedirect("list");
    }
}
