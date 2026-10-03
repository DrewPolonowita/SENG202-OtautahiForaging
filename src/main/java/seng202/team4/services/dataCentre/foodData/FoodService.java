package seng202.team4.services.dataCentre.foodData;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.Food;
import seng202.team4.services.FoodMapper;
import seng202.team4.services.api.FoodClientInterface;
import seng202.team4.repository.FoodRepository;
import seng202.team4.services.dataCentre.LoadingCache;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for handling food related business logic
 * @author Team 4
 */
public class FoodService implements FoodDataManager {
    private static final Logger LOG = LogManager.getLogger(FoodService.class);
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



    private Food getFoodFromDatabase(String foodName) {
        Integer foodId = foodDao.getFoodIdByFoodName(foodName);

        if (foodId != null && foodDao.foodDetailsUpToDateAndExist(foodId)) {
            return foodDao.getOne(foodId);
        }

        return null;
    }

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

    private Integer getFoodIdFromDatabase(String foodName) {
        return foodDao.getFoodIdByFoodName(foodName);
    }

    private Integer getFoodIdFromApi(String foodName) {
        return foodApiHandler.getFoodBySearchFromApi(foodName);
    }



    /**
     * Method to get all the foods we have mapped for the trees with api data
     * First searches the FatSecret API for food ids using each food map key (foodName)
     * and then uses those ids to search by specific id
     * @return List of food objects that exist in the project
     */
    @Override
    public List<Food> getFoods() {
        foodCache.resetStats();
        foodIdByNameCache.resetStats();

        long startTime = System.currentTimeMillis();

        List<Food> foods = new ArrayList<>();
        for (String foodName : foodMapper.getFoodMap().keySet()) {
            Food food = foodCache.get(foodName);
            if (food != null) {
                foods.add(food);
            }
        }
        long durationMs = System.currentTimeMillis() - startTime;

        LOG.info("Loaded {} foods in {} ms", foods.size(), durationMs);
        LOG.info("Food cache stats: {}", foodCache.stats());
        LOG.info("Food ID cache stats: {}", foodIdByNameCache.stats());


        return foods;
    }

    @Override
    public Food getFood(String foodName) {
        Food foodToReturn;
        foodCache.resetStats();
        foodIdByNameCache.resetStats();

        long startTime = System.currentTimeMillis();
        foodToReturn = foodCache.get(foodName);
        long durationMs = System.currentTimeMillis() - startTime;

        LOG.info("Loaded food in {} ms", durationMs);
        LOG.info("Food cache stats: {}", foodCache.stats());
        LOG.info("Food ID cache stats: {}", foodIdByNameCache.stats());
        return foodToReturn;
    }
}
