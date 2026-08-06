package seng202.team4.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.TreeModel;
import seng202.team4.models.api.ApiResponse;
import seng202.team4.models.api.Request;
import seng202.team4.models.api.Tree;

import java.net.HttpURLConnection;
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
        String genus = treeRecord.properties().genus();
        String fruitType = getFruitType(genus);
        return new TreeModel(
                treeRecord.properties().treeId(),
                treeRecord.geometry().coordinates(),
                treeRecord.properties().ownership(),
                genus,
                fruitType);
    }

    /**
     * Mapping the genus to a fruit tyoe in a new coloumn
     * @param genus
     * @return the fruit type based on genus
     */
    private String getFruitType(String genus) {
        return switch (genus) {
            case "Malus" -> "Apple";
            case "Pyrus" -> "Pear";
            case "Prunus" -> "Cherry";
            case "Juglans" -> "Walnut";
            default -> "N/A";
        };
    }
    /**
     * Method to return all trees from API
     * @return List of all trees from API
     */
    public List<TreeModel> getAllTrees() {
        Request request = apiHandler.getTrees("CommonName like '%apple%'", "TreeID,Ownership,Genus", 100);

        if (request.getResponseCode() != HttpURLConnection.HTTP_OK) {
            log.info("API connection failed with a " + request.getResponseCode());
            return List.of();
        }

        ApiResponse response = (ApiResponse) request.getApiResponse();

        log.info(response);

        List<Tree> treeRecords = Arrays.asList(
                response.featureTrees()
        );

        return treeRecords.stream()
                .map(this::createTreeObject)
                .toList();

    }
}
