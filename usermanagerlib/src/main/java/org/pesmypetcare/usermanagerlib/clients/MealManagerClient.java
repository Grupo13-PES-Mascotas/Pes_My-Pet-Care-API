package org.pesmypetcare.usermanagerlib.clients;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.pesmypetcare.usermanagerlib.datacontainers.Date;
import org.pesmypetcare.usermanagerlib.datacontainers.Meal;
import org.pesmypetcare.usermanagerlib.datacontainers.MealData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MealManagerClient {
    public static final String MEALNAME = "mealName";
    public static final String KCAL = "kcal";
    private static final String BASE_URL = "https://pes-my-pet-care.herokuapp.com/meal/";
    private static final int POST = 0;
    private static final int GET = 1;
    private static final int DELETE = 2;
    private static final int PUT = 3;
    private static final Gson gson = new Gson();
    private TaskManager taskManager;


    /**
     * Creates a meal eaten by a pet on the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param meal The meal entity that contains the attributes of the meal eaten by the pet
     */
    public void createMeal(String owner, String petName, Meal meal) {
        JSONObject reqJson = meal.getBody().buildMealJson();
        taskManager = new TaskManager();
        taskManager.setTaskId(POST);
        taskManager.setReqBody(reqJson);
        taskManager.execute(BASE_URL + owner + "/" + petName + "/" + meal.getDate());
    }

    /**
     * Deletes the pet with the specified owner and name from the database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Date the meal was eaten
     */
    public void deleteByDate(String owner, String petName, Date date) {
        taskManager = new TaskManager();
        taskManager.setTaskId(DELETE);
        taskManager.execute(BASE_URL + owner + "/" + petName + "/" + date);
    }

    /**
     * Deletes all the meals of the specified pet from database.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     */
    public void deleteAllMeals(String owner, String petName) {
        taskManager = new TaskManager();
        taskManager.setTaskId(DELETE);
        taskManager.execute(BASE_URL + owner + "/" + petName);
    }

    /**
     * Gets a meal identified by its pet and date.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Date the meal was eaten
     * @return The MealData identified by the data
     */
    public MealData getMealData(String owner, String petName, Date date) throws ExecutionException, InterruptedException {
        taskManager = new TaskManager();
        taskManager.setTaskId(GET);
        StringBuilder json = taskManager.execute(BASE_URL + owner + "/" + petName + "/" + date).get();
        return gson.fromJson(json.toString(), MealData.class);
    }

    /**
     * Gets the data from all the specified meals from the database identified by its pet.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @return The List containing all the meals from the pet
     */
    public List<Meal> getAllMealData(String owner, String petName) throws ExecutionException, InterruptedException {
        taskManager = new TaskManager();
        taskManager.setTaskId(GET);
        StringBuilder response = taskManager.execute(BASE_URL + owner + "/" + petName).get();
        String jsonArray = response.substring(1, response.length()-1);
        String[] meals = jsonArray.split(",\\{");
        List<Meal> mealList = new ArrayList<>();
        mealList.add(gson.fromJson(meals[0], Meal.class));
        for (int i=0; i < meals.length; i++) {
            meals[i] = "{" + meals[i];
            mealList.add(gson.fromJson(meals[i], Meal.class));
        }
        return mealList;
    }

    /**
     * Gets the data from all the meals eaten by the pet between the initial and final date not including them.
     * @param owner Username of the owner of the pets
     * @param petName Name of the pet
     * @param initialDate Initial Date
     * @param finalDate Final Date
     * @return The List containing all the meals eaten by the pet in the specified time
     */
    public List<Meal> getAllMealsBetween(String owner, String petName, String initialDate,
                                                 String finalDate) throws ExecutionException, InterruptedException {
        taskManager = new TaskManager();
        taskManager.setTaskId(GET);
        StringBuilder response =
            taskManager.execute(BASE_URL + owner + "/" + petName + "/" + initialDate + "/" + finalDate).get();
        String jsonArray = response.substring(1, response.length()-1);
        String[] meals = jsonArray.split(",\\{");
        List<Meal> mealList = new ArrayList<>();
        mealList.add(gson.fromJson(meals[0], Meal.class));
        for (int i=0; i < meals.length; i++) {
            meals[i] = "{" + meals[i];
            mealList.add(gson.fromJson(meals[i], Meal.class));
        }
        return mealList;
    }

    /**
     * Updates the meal's field.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @param date Date the meal was eaten
     * @param field Name of the field to update
     * @param value Value the field will have
     */
    public void updateMealField(String owner, String petName, Date date, String field, Object value) {
        Map<String, Object> reqData = new HashMap<>();
        reqData.put("value", value);
        taskManager = new TaskManager();
        taskManager.setTaskId(PUT);
        taskManager.setReqBody(new JSONObject(reqData));
        taskManager.execute(BASE_URL + owner + "/" + petName + "/" + date + "/" + field);
    }


}
