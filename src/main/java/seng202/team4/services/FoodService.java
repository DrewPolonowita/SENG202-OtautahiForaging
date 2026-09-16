package seng202.team4.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.Food;
import seng202.team4.services.api.FoodClientInterface;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.models.api.Food.FoodByID.FoodByIdRecord;
import seng202.team4.repository.FoodRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for handling food related business logic
 * @author Team 4
 */
public class FoodService {
    private static final Logger log = LogManager.getLogger(FoodService.class);
    private Map<String, List<String>> foodMap = new HashMap<>();
    private Map<String, List<String>> typeMap = new HashMap<>();

    private final FoodRepository foodDao;
    private final FoodApiHandler foodApiHandler;

    /**
     * Constructor
     */
    public FoodService(FoodRepository foodDao, FoodClientInterface foodApiClient) {
        this.foodDao = foodDao;
        this.foodApiHandler = new FoodApiHandler(foodApiClient);
        createFoodMap();
        createTypeMap();
    }

    /**
     * Test constructor
     */
    public FoodService(FoodRepository foodDao) {
        this.foodDao = foodDao;
        this.foodApiHandler = null;
        createFoodMap();
        createTypeMap();
    }

    /**
     * Method to get all the foods we have mapped for the trees with api data
     * First searches the FatSecret API for food ids using each food map key (foodName)
     * and then uses those ids to search by specific id
     * @return List of food objects that exist in the project
     */
    public List<Food> getFoods() {
        List<Food> foods = new ArrayList<>();
        for (String foodName : foodMap.keySet()) {
            Integer foodId = foodDao.getFoodIdByFoodName(foodName);

            boolean noIdExists = foodId == null;
            if (noIdExists) {
                foodId = foodApiHandler.getFoodBySearchFromApi(foodName);
            }
            Food food;
            if (foodDao.foodDetailsUpToDateAndExist(foodId)) {
                food = foodDao.getOne(foodId);
            } else {
                food = foodApiHandler.getFoodById(foodId, foodName, getFoodType(foodName));
                foodDao.replaceFood(food);
            }
            foods.add(food);

        }

        return foods;
    }

    private String getFoodType(String foodName) {
        for (Map.Entry<String, List<String>> entry : typeMap.entrySet()) {
            if (entry.getValue().contains(foodName)) {
                return entry.getKey();
            }
        }
        return "N/A";
    }
    /**
     * Creates the map of each type of food that can be found in the database
     * mapped to the common name of foods within that type.
     * Created when FoodService class is created
     */
    private void createTypeMap() {
        typeMap.put("Fruit", List.of(
                "Apple",
                "Strawberry",
                "Peach",
                "Olive",
                "Honey Locust",
                "Pear",
                "Apricot",
                "Mulberry",
                "Loquat",
                "Quince",
                "Cherry",
                "Elderberry",
                "Juniper"));
        typeMap.put("Nut", List.of(
                "Walnut",
                "Sweet Chestnut",
                "Hazel",
                "Almond",
                "Beech"
        ));
        typeMap.put("Leaves", List.of(
                "Sweet bay"
        ));
    }

    /**
     * Creates the map of each common name that a tree growing a particular food can be known by,
     * mapped to the particular food. Created when FoodService class is created
     */
    private void createFoodMap() {
        foodMap.put("Apple", List.of(
                "Chinese Crab Apple",
                "Midget Crab Apple",
                "Pillar Crab Apple",
                "Siberian Crab",
                "Southern crabapple",
                "Hall''s Crab Apple",
                "Zumi Crab Apple",
                "Common Apple"
        ));
        foodMap.put("Beech", List.of(
                "European Beech",
                "Copper Beech"
        ));
        foodMap.put("Walnut", List.of(
                "Common Walnut",
                "Black Walnut",
                "Walnut"
        ));
        foodMap.put("Strawberry", List.of(
                "Strawberry Tree",
                "Cyprus Strawberry Tree"
        ));
        foodMap.put("Sweet Chestnut", List.of(
                "Sweet Chestnut"
        ));
        foodMap.put("Honey Locust", List.of(
                "Honeylocust",
                "Honey Locust"
        ));
        foodMap.put("Olive", List.of(
                "Olive"
        ));
        foodMap.put("Peach", List.of(
                "Peach"
        ));
        foodMap.put("Pear", List.of(
                "Pear"
        ));
        foodMap.put("Hazel", List.of(
                "Hazel"
        ));
        foodMap.put("Apricot", List.of(
                "Apricot",
                "Flowering Apricot",
                "Japanese Apricot"
        ));
        foodMap.put("Sweet bay", List.of(
                "Bay laurel"
        ));
        foodMap.put("Mulberry", List.of(
                "Common Mulberry",
                "Weeping Mulberry"
        ));
        foodMap.put("Loquat", List.of(
                "Loquat Tree"
        ));
        foodMap.put("Quince", List.of(
                "Quince",
                "Chinese Quince"
        ));
        foodMap.put("Almond", List.of(
                "Almond"
        ));
        foodMap.put("Cherry", List.of(
                "Cherry",
                "Bird Cherry"
        ));
        foodMap.put("Elderberry", List.of(
                "Common Elder"
        ));
        foodMap.put("Juniper", List.of(
                "Common Juniper"
        ));
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
        for (Map.Entry<String, List<String>> typeEntry : getTypeMap().entrySet()) {
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

    /**
    * Getter for food map
    * @return the food map
    */
    public Map<String, List<String>> getFoodMap () {
        return foodMap;
    }

    /**
    * Getter for the type map
    * @return the type map
    */
    public Map<String, List<String>> getTypeMap () {
        return typeMap;
    }
}
