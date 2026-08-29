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
     * Returns a {@link List<TreeModel>}
     *
     * @param type
     * @param north
     * @param east
     * @param south
     * @param west
     * @return
     */
    List<TreeModel> getTreesByRegion(String type, double north, double east, double south, double west);
}