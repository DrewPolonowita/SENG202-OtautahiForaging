package seng202.team4.services.dataCentre.treeData;


import seng202.team4.models.TreeModel;

import java.util.List;

/**
 * An interface to encapsulate the logic behind requesting from a database verses an api
 */
public interface TreeDataManager {

    /**
     * Returns a list of all the trees of a given CommonName within the given region.
     * This method will update the database with trees from the api for future retrieval.
     * This method will prioritize taking information from the database where applicable.
     *
     * @param type the commonName of the tree being requested
     * @param north the max y value for the region
     * @param east the max x value for the region
     * @param south the min y value for the region
     * @param west the min x value for the region
     */
    List<TreeModel> getTreesByRegion(String type, double north, double east, double south, double west);

    /**
    /**
     * For use in table view, get trees by food without regard for region
     * @param food food to look for
     * @return list of trees
     */
    List<TreeModel> getTreesByFood(String food);
}
