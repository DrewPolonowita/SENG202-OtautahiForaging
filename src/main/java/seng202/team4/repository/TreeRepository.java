package seng202.team4.repository;

import seng202.team4.models.TreeModel;
import java.util.List;

/**
 * Interface for the tree specific data access object
 * @author Team 4
 */
public interface TreeRepository extends DAOInterface<TreeModel, Integer> {

    /**
     * retrieve all trees of a specified common name
     * @param food the common name specified
     * @return all trees with the common name
     */
    List<TreeModel> getTreesByFoodName(String food);

    /**
     * Method to get trees that are within a certain region and of a type
     * @param food Type of tree to look for, should be a mapped food from
     * {@link seng202.team4.services.FoodMapper} e.g "Apple" or "Walnut"
     * @param north north
     * @param east east
     * @param south south
     * @param west west
     * @return list of trees of type in the region
     */
    List<TreeModel> getTreesByRegion(String food, double north, double east, double south, double west);
}
