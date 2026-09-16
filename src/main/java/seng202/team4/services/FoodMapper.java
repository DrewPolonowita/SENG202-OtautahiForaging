package seng202.team4.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.services.dataCentre.foodData.FoodService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodMapper {

    private static final Logger log = LogManager.getLogger(FoodMapper.class);
    private Map<String, List<String>> foodMap = new HashMap<>();
    private Map<String, List<String>> typeMap = new HashMap<>();

    public FoodMapper() {
        createFoodMap();
        createTypeMap();
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

    public String getFoodType(String foodName) {
        for (Map.Entry<String, List<String>> entry : typeMap.entrySet()) {
            if (entry.getValue().contains(foodName)) {
                return entry.getKey();
            }
        }
        return "N/A";
    }
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
