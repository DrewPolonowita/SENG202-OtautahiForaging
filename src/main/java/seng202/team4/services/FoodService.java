package seng202.team4.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.Food;
import seng202.team4.repository.FoodDAO;

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
    private final FoodDAO foodDAO;

    /**
     * Constructor
     */
    public FoodService(FoodDAO foodDAO) {
        this.foodDAO = foodDAO;
        createFoodMap();
        createTypeMap();
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
     * Method to populate the food in the database
     */
    public void populateFoodDatabase() {
        String type;
        List<Food> foods = new ArrayList<>();
        for (Map.Entry<String, List<String>> foodEntry : getFoodMap().entrySet()) {
            String commonName = foodEntry.getKey();
            for (Map.Entry<String, List<String>> typeEntry : getTypeMap().entrySet()) {
                List<String> commonNames = typeEntry.getValue();
                for (String name : commonNames) {
                    if (name.equals(commonName)) {
                        type = typeEntry.getKey();
                        foods.add(new Food(
                                commonName,
                                type,
                                null
                        ));
                    }
                }
            }
        }
        for (Food food : foods) {
            foodDAO.add(food);
        }
    }

    /**
     * Method to get all the food in the database if the database is populated
     * otherwise populate it
     * @return all foods
     */
    public List<Food> getAllFood() {
        List<Food> databaseFood = foodDAO.getAll();
        if (!databaseFood.isEmpty()) {
            log.info("Loaded {} food from database", databaseFood.size());
            return databaseFood;
        }
        populateFoodDatabase();
        databaseFood = foodDAO.getAll();
        return databaseFood;
    }

    /**
     * Method to return all food models of one shared type from database
     * @param type the type of food model being returned
     * @return food models of the given type
     */
    public List<Food> getFoodByType(String type) {
        List<Food> databaseFood = foodDAO.getByType(type);
        if (!databaseFood.isEmpty()) {
            log.info("Loaded {} {} type foods from database", databaseFood.size(), type);
            return databaseFood;
        }
        populateFoodDatabase();
        databaseFood = foodDAO.getByType(type);
        return databaseFood;
    }


    /**
     * Getter for food map
     * @return the food map
     */
    public Map<String, List<String>> getFoodMap() {
        return foodMap;
    }

    /**
     * Getter for the type map
     * @return the type map
     */
    public Map<String, List<String>> getTypeMap() {
        return typeMap;
    }
}
