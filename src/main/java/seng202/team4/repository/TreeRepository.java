package seng202.team4.repository;

import seng202.team4.models.TreeModel;
import java.util.List;

/**
 * Interface for the tree specific data access object
 * @author Team 4
 */
public interface TreeRepository extends DAOInterface<TreeModel> {

    /**
     * retrieve all trees of a specified common name
     * @param food the common name specified
     * @return all trees with the common name
     */
    List<TreeModel> getTreesByFoodName(String food);
}