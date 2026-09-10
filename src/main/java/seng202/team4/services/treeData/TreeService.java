package seng202.team4.services.treeData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.models.TreeModel;

import seng202.team4.repository.TreeRepository;
import seng202.team4.services.api.ApiClientInterface;
import seng202.team4.services.api.TreeApiClient;

import java.util.List;

/**
 * Handles retrieving cached trees and minimizing api usage for trees
 */
public class TreeService implements TreeDataManager {
    private static final Logger log = LogManager.getLogger(TreeDataManager.class);
    private final TreeApiHandler treeApiHandler;
    private final TreeRepository treeRepository;

    /**
     * Creates a new instance and initializes private variables
     */
    public TreeService(TreeRepository treeRepository, ApiClientInterface apiClient) {
        TreeApiClient treeApiClient = new TreeApiClient(apiClient);
        this.treeApiHandler = new TreeApiHandler(treeApiClient);
        this.treeRepository = treeRepository;
    }

    /**
     * Returns a list of trees from the database within a given region
     *
     * @param type the commonName of the tree being requested
     * @param north the max y value for the region
     * @param east the max x value for the region
     * @param south the min y value for the region
     * @param west the min x value for the region
     */
    public List<TreeModel> getTreesByRegion(String type, double north, double east, double south, double west) {
        return treeApiHandler.getTreesByRegion(type, north, east, south, west);
    }

    /**
     * Returns a list of trees from the api within a given region and updates the database
     *
     * @param type the commonName of the tree being requested
     * @param north the max y value for the region
     * @param east the max x value for the region
     * @param south the min y value for the region
     * @param west the min x value for the region
     */
    public List<TreeModel> updateTreesByRegion(String type, double north, double east, double south, double west) {
        return treeApiHandler.getTreesByRegion(type, north, east, south, west);

    }

    /**
     * Returns a list of trees all trees from the api
     */
    public List<TreeModel> getAllTrees() {
        return treeApiHandler.getAllTreesFromApi();
    }

    /**
     * Returns true if all intersecting regions have been updated within the buffer time
     *
     * @param type the commonName of the tree being requested
     * @param north the max y value for the region
     * @param east the max x value for the region
     * @param south the min y value for the region
     * @param west the min x value for the region
     */
    public boolean isRegionRecent(String type, double north, double east, double south, double west) {
        return true;
    }

    /**
     * For use in table view, get trees by food without regard for region
     * @param food
     * @return
     */
    public List<TreeModel> getTreesByFood(String food) {
        List<TreeModel> databaseTrees = treeRepository.getTreesByFoodName(food);

        if (!databaseTrees.isEmpty()) {
            log.info("Loaded {} {} trees from database", databaseTrees.size(), food);
            return databaseTrees;
        }

        List<TreeModel> apiTreesByType = treeApiHandler.getTreesFromApiByFood(food);
        for (TreeModel tree : apiTreesByType) {
            try {
                treeRepository.add(tree);
            } catch (DuplicateEntryException e) {
                log.warn("(Tree by food: {}) Skip duplicate id: {}", tree.getFood(), tree.getId());
            }
        }
        return treeRepository.getTreesByFoodName(food);
    }
}
