package seng202.team4.repository;

import seng202.team4.models.Food;

import java.util.List;

/**
 * Interface for the food specific data access object
 * @author Team 4
 */
public interface FoodRepository extends DAOInterface<Food, String> {

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


}
