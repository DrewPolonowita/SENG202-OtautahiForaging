package seng202.team4.repository;


import seng202.team4.models.Food;


import java.util.List;


/**
 * Interface for the food specific data access object
 * @author Team 4
 */
public interface FoodRepository extends DAOInterface<Food, Integer> {


    /**
     * Retrieves all food of a specified food type
     * @param type the common name of the group specified
     * @return all food models that share the given type
     */
    List<Food> getFoodByType(String type);


    /**
     * Gets a food model matching the food grown on a particular tree
     * @param treeId the id of the specified tree
     * @return the food model corresponding to the food grown on the tree
     */
    Food getFoodbyTreeId(int treeId);

    /**
     * Retrieves the food id for a given food with a specific common name
     * @param foodName the common name of a food
     * @return food id for the food with the specified common name if successful, otherwise null
     */
    Integer getFoodIdByFoodName(String foodName);

    /**
     * Method to check that the details stored for a specific food in the database are up to date as data can only be stored for up to 24 hours
     * @param foodId the id of the food being checked
     * @return true if up details are up-to-date and exist, false otherwise
     */
    boolean foodDetailsUpToDateAndExist(int foodId);

    /**
     * Replaces food data that exists in the database but have to be updated to comply with FatSecret API data storage time requirements
     * @param food food needing to be replaced
     */
    void replaceFood(Food food);

}

