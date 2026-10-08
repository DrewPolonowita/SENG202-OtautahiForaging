package seng202.team4.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.FilterCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles the mapping of Food common names to
 * more general food names
 * @author Team 4
 */
public class FoodMapper {

    private static final Logger log = LogManager.getLogger(FoodMapper.class);
    private final Map<String, List<String>> foodMap = new HashMap<>();
    private final Map<String, List<String>> typeMap = new HashMap<>();

    /**
     * constructor
     */
    public FoodMapper() {
        createFoodMap();
        createTypeMap();
        validateFoodMap();
    }


    /**
     * Creates the map of each common name that a tree growing a particular food can be known by,
     * mapped to the particular food. Created when FoodService class is created
     */
    private void createFoodMap() {
        foodMap.put("Apple", List.of(
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
                "Strawberry Tree"
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
                "Flowering Apricot"
        ));
        foodMap.put("Sweet bay", List.of(
                "Bay laurel"
        ));
        foodMap.put("Mulberry", List.of(
                "Common Mulberry"
        ));
        foodMap.put("Loquat", List.of(
                "Loquat Tree"
        ));
        foodMap.put("Quince", List.of(
                "Quince"
        ));
        foodMap.put("Almond", List.of(
                "Almond"
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

    /**
     * Method to confirm that nothing in the filter category enum doesn't exist in FoodMapper
     */
    private void validateFoodMap() {
        List<String> missing = new ArrayList<>();

        for (FilterCategory filterCategory : FilterCategory.values()) {
            String foodName = filterCategory.toString();

            if (!foodMap.containsKey(foodName)) {
                missing.add(foodName);
            }
        }

        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                    "Missing FoodMapper entries for: " + missing
            );
        }
    }

    /**
     * Given a food name get its type
     * @param foodName a name that is the key of foodMap
     * @return food type
     */
    public String getFoodType(String foodName) {
        for (Map.Entry<String, List<String>> entry : typeMap.entrySet()) {
            if (entry.getValue().contains(foodName)) {
                return entry.getKey();
            }
        }
        return "N/A";
    }

    /**
     * Mapping the common name to a food type in a new column
     * @param commonName common name
     * @return the food type based on commonName
     */
    public String getFoodName(String commonName) {
        if (commonName == null) {
            return "N/A";
        }
        for (Map.Entry<String, List<String>> entry : foodMap.entrySet()) {
            List<String> commonNames = entry.getValue();
            String trimmedCommonName = commonName.trim();
            for (String name : commonNames) {
                if (name.trim().equalsIgnoreCase(trimmedCommonName)) {
                    return entry.getKey();
                }
            }
        }
        log.warn("No food mapping for {}", commonName);
        return "N/A";
    }

    /**
     * Getter for food map
     * @return the food map
     */
    public Map<String, List<String>> getFoodMap () {
        return Collections.unmodifiableMap(foodMap);
    }

    /**
     * Getter for the type map
     * @return the type map
     */
    public Map<String, List<String>> getTypeMap () {
        return Collections.unmodifiableMap(typeMap);
    }
}
