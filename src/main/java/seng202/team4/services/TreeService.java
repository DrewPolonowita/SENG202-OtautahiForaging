package seng202.team4.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.TreeModel;
import seng202.team4.models.api.ApiResponse;
import seng202.team4.models.api.Tree;

import java.util.Arrays;
import java.util.List;

/**
 * Service class to handle business logic for managing trees
 * @author George Mounce
 */
public class TreeService {
    private static final Logger log = LogManager.getLogger(TreeService.class);
    private final ApiHandler apiHandler;

    /**
     * Constructor
     */
    public TreeService() {
        this.apiHandler = new ApiHandler();
    }

    /**
     * Constructor for testing
     * @param apiHandler mock apiHandler
     */
    public TreeService(ApiHandler apiHandler) {
        this.apiHandler = apiHandler;
    }

    /**
     * Method to take a Tree (record) object and represent it as TreeModel object
     * @param treeRecord tree record
     * @return TreeModel tree model object
     */
    private TreeModel createTreeObject(Tree treeRecord) {
        return new TreeModel(
                treeRecord.properties().treeId(),
                treeRecord.geometry().coordinates(),
                treeRecord.properties().ownership(),
                treeRecord.properties().genus()
        );
    }

    /**
     * Method to return all trees from API
     * @return List of all trees from API
     */
    public List<TreeModel> getAllTrees() {
        ApiResponse response = apiHandler.getTrees("", "").getApiResponse();

        if (response == null) {
            log.info("API Response null");
            return List.of();
        }


        List<Tree> treeRecords = Arrays.asList(
                response.featureTrees()
        );

        return treeRecords.stream()
                .map(this::createTreeObject)
                .toList();

    }
}
