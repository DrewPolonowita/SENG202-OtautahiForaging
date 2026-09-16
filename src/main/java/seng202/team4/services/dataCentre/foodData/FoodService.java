package seng202.team4.services.dataCentre.foodData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.Food;
import seng202.team4.services.FoodMapper;
import seng202.team4.services.api.FoodClientInterface;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.models.api.Food.FoodByID.FoodByIdRecord;
import seng202.team4.repository.FoodRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for handling food related business logic
 * @author Team 4
 */
public class FoodService implements FoodDataManager {
    private static final Logger log = LogManager.getLogger(FoodService.class);
    private final FoodMapper foodMapper;

    private final FoodRepository foodDao;
    private final FoodApiHandler foodApiHandler;

    /**
     * Constructor
     */
    public FoodService(FoodRepository foodDao, FoodClientInterface foodApiClient, FoodMapper foodMapper
    ) {
        this.foodDao = foodDao;
        this.foodApiHandler = new FoodApiHandler(foodApiClient);
        this.foodMapper = foodMapper;
    }

    /**
     * Test constructor
     */
    public FoodService(FoodRepository foodDao) {
        this.foodDao = foodDao;
        this.foodApiHandler = null;
        this.foodMapper = new FoodMapper();
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
            Integer foodId = foodDao.getFoodIdByFoodName(foodName);

            boolean noIdExists = foodId == null;
            if (noIdExists) {
                foodId = foodApiHandler.getFoodBySearchFromApi(foodName);
            }
            Food food;
            if (foodDao.foodDetailsUpToDateAndExist(foodId)) {
                food = foodDao.getOne(foodId);
            } else {
                food = foodApiHandler.getFoodById(foodId, foodName, foodMapper.getFoodType(foodName));
                foodDao.replaceFood(food);
            }
            foods.add(food);

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
        double fat = foodRecord.servings().serving().getFirst().carbohydrate();
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

    /**
     * Method to return all food from API
     * @return List of all food from API
     */
    public List<Food> getAllFoodFromApi() {
        return null;
    }

    /**
     * Method to get all the food in the database if the database is populated
     * otherwise populate it
     * @return all foods
     */
    public List<Food> getAllFood() {
        List<Food> databaseFood = foodDao.getAll();
        if (!databaseFood.isEmpty()) {
            log.info("Loaded {} food from database", databaseFood.size());
            return databaseFood;
        }
        List<Food> apiFood = getAllFoodFromApi();
        log.info("Loaded {} foods from api", apiFood.size());
        for (Food food : apiFood) {
            try {
                foodDao.add(food);
            } catch (DuplicateEntryException e) {
                log.warn("(All Food) Skip duplicate id: {}", food.getId());
            }
        }
        databaseFood = foodDao.getAll();
        return databaseFood;
    }

//    /**
//     * Method to return all food models of one shared type from database
//     * @param type the type of food model being returned (e.g. fruit or nut)
//     * @return food models of the given type
//     */
//    public List<Food> getFoodByType(String type) {
//        List<Food> databaseFood = foodDAO.getFoodByType(type);
//        if (!databaseFood.isEmpty()) {
//            log.info("Loaded {} {} type foods from database", databaseFood.size(), type);
//            return databaseFood;
//        }
//        populateFoodDatabase();
//        databaseFood = foodDAO.getFoodByType(type);
//        return databaseFood;
//    }


}
