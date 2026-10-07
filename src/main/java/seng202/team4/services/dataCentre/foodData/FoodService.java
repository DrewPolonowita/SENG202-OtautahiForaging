package seng202.team4.services.dataCentre.foodData;

import seng202.team4.models.Food;
import seng202.team4.services.FoodMapper;
import seng202.team4.services.api.FoodClientInterface;
import seng202.team4.repository.FoodRepository;
import seng202.team4.services.dataCentre.LoadingCache;

/**
 * Service for handling food related business logic.
 * Retrieves food from database when up to date and available food is there
 * @author Team 4
 */
public class FoodService implements FoodDataManager {
    private final FoodMapper foodMapper;

    private final FoodRepository foodDao;
    private final FoodApiHandler foodApiHandler;

    private final LoadingCache<String, Food> foodCache;
    private final LoadingCache<String, Integer> foodIdByNameCache;

    /**
     * Constructor
     */
    public FoodService(FoodRepository foodDao, FoodClientInterface foodApiClient, FoodMapper foodMapper
    ) {
        this.foodDao = foodDao;
        this.foodApiHandler = new FoodApiHandler(foodApiClient);
        this.foodMapper = foodMapper;

        this.foodCache = new LoadingCache<>(
                this::getFoodFromDatabase,
                this::getFoodFromApi
        );
        this.foodIdByNameCache = new LoadingCache<>(
                this::getFoodIdFromDatabase,
                this::getFoodIdFromApi
        );
    }


    /**
     * Attempts to get up to date food details from the database.
     * @param foodName food to find
     * @return the food if it exists and is up to date, otherwise null so cache knows it has to try api
     */
    private Food getFoodFromDatabase(String foodName) {
        Integer foodId = foodDao.getFoodIdByFoodName(foodName);

        if (foodId != null && foodDao.foodDetailsUpToDateAndExist(foodId)) {
            return foodDao.getOne(foodId);
        }

        return null;
    }

    /**
     * Gets food details from the api and stores it in the database
     * @param foodName name of food being searched for
     * @return Found food, null id doesn't exist
     */
    private Food getFoodFromApi(String foodName) {
        Integer foodId = foodIdByNameCache.get(foodName);

        if (foodId == null || foodId == 0) {
            return null;
        }
        Food food = foodApiHandler != null ? foodApiHandler
                .getFoodById(foodId, foodName, foodMapper.getFoodType(foodName)) : null;

        if (food != null) {
            foodDao.replaceFood(food);
        }

        return food;

    }

    /**
     * Method to get a food id from the database
     * @param foodName food to find
     * @return the food id, or null if no food exists so cache knows to try the api
     */
    private Integer getFoodIdFromDatabase(String foodName) {
        return foodDao.getFoodIdByFoodName(foodName);
    }

    /**
     * Searches API for id
     * @param foodName food to find
     * @return food id or null if nothing is found
     */
    private Integer getFoodIdFromApi(String foodName) {
        return foodApiHandler.getFoodBySearchFromApi(foodName);
    }


    @Override
    /**
     * Looks for food details by name.
     * The local database is checked first, with the API tried if database doesn't have
     * up to date information
     * @param foodName food to find
     * @return the food or null if it can't be found
     */
    public Food getFood(String foodName) {
        return foodCache.get(foodName);
    }
}
