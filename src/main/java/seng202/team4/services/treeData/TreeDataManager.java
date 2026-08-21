package seng202.team4.services.treeData;

import seng202.team4.models.TreeModel;

import java.util.List;

/**
 * An interface to encapsulate the logic behind requesting from a database verses an api
 */
public interface TreeDataManager {

    /**
     * Returns a list of trees from the database within a given region
     *
     * @param type the commonName of the tree being requested
     * @param north the max y value for the region
     * @param east the max x value for the region
     * @param south the min y value for the region
     * @param west the min x value for the region
     */
    void getTreesByRegion(String type, double north, double east, double south, double west);

    /**
     * Returns a list of trees from the api within a given region and updates the database
     *
     * @param type the commonName of the tree being requested
     * @param north the max y value for the region
     * @param east the max x value for the region
     * @param south the min y value for the region
     * @param west the min x value for the region
     */
    List<TreeModel> updateTreesByRegion(String type, double north, double east, double south, double west);

    /**
     * Returns a list of all trees from the api
     */
    List<TreeModel> getAllTrees();

    /**
     * Returns true if all intersecting regions have been updated within the buffer time
     *
     * @param type the commonName of the tree being requested
     * @param north the max y value for the region
     * @param east the max x value for the region
     * @param south the min y value for the region
     * @param west the min x value for the region
     */
    boolean isRegionRecent(String type, double north, double east, double south, double west);
}
