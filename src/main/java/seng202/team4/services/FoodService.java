package seng202.team4.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    /**
     * Constructor
     */
    public FoodService() {
        createFoodMap();
    }

    /**
     * Creates the map of each common name that a tree growing a particular fruit can be known by,
     * mapped to the particular fruit. Created when FoodService class is created
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
     * Getter for food map
     * @return the food map
     */
    public Map<String, List<String>> getFoodMap() {
        return foodMap;
    }
}
