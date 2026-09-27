package seng202.team4.services.dataCentre.foodData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.Food;
import seng202.team4.services.FoodMapper;
import seng202.team4.services.api.FoodClientInterface;
import seng202.team4.models.api.Food.FoodByID.FoodByIdRecord;
import seng202.team4.repository.FoodRepository;
import seng202.team4.services.dataCentre.LoadingCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for handling food related business logic
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
        Food food = foodApiHandler.getFoodById(
                foodId,
                foodName,
                foodMapper.getFoodType(foodName)
        );

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
        List<Food> foods = new ArrayList<>();
        for (String foodName : foodMapper.getFoodMap().keySet()) {
            Food food = foodCache.get(foodName);

            if (food != null) {
                foods.add(food);
            }

        }

        return foods;
    }

    /**
     * Create a food model using a FoodByIdRecord
     * @param foodRecord a foodByIdRecord
     * @return a food model
     */
    public Food createFoodObjectById(FoodByIdRecord foodRecord) {
        int foodId = foodRecord.foodId();
        String name = foodRecord.foodName();
        String type = null;
        for (Map.Entry<String, List<String>> typeEntry : foodMapper.getTypeMap().entrySet()) {
            List<String> commonNames = typeEntry.getValue();
            for (String commonName : commonNames) {
                if (name.equals(commonName)) {
                    type = typeEntry.getKey();
                    break;
                }
            }
        }
        double calories = foodRecord.servings().serving().getFirst().calories();
        double fat = foodRecord.servings().serving().getFirst().fat();
        double protein = foodRecord.servings().serving().getFirst().protein();
        double carbohydrate = foodRecord.servings().serving().getFirst().carbohydrate();
        String servingDescription = foodRecord.servings().serving().getFirst().servingDescription();
        int servingId = foodRecord.servings().serving().getFirst().servingId();

        return new Food(
                foodId,
                name,
                type,
                calories,
                carbohydrate,
                protein,
                fat,
                servingDescription,
                servingId
        );
    }
}
