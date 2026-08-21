package seng202.team4.services.treeData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.TreeModel;
import seng202.team4.models.api.ApiResponse;
import seng202.team4.models.api.Request;
import seng202.team4.models.api.Tree;
import seng202.team4.services.ApiHandler;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service class to handle business logic for managing trees
 * @author Team 4
 */
public class TreeApiHandler {
    private static final Logger LOG = LogManager.getLogger(TreeApiHandler.class);
    private final ApiHandler apiHandler;

    /**
     * Constructor for the TreeApiHandeler
     */
    public TreeApiHandler(ApiHandler apiHandler) {
        this.apiHandler = apiHandler;
    }

    /**
     * Method to take a Tree (record) object and convert it to a TreeModel object
     *
     * @param treeRecord tree record
     * @return TreeModel tree model object
     */
    private TreeModel createTreeObject(Tree treeRecord) {
        String genus = treeRecord.properties().genus();
        String commonName = treeRecord.properties().commonName();
        String fruitType = getFruitType(commonName);

        return new TreeModel(
                treeRecord.properties().treeId(),
                treeRecord.geometry().coordinates(),
                treeRecord.properties().ownership(),
                genus,
                fruitType,
                commonName);
    }

    /**
     * Mapping the genus to a fruit type in a new column
     *
     * @param commonName common name
     * @return the fruit type based on genus
     */
    private String getFruitType(String commonName) {
        return switch (commonName) {
            case "Apple" -> "Apple";
            case "Pear" -> "Pear";
            case "Cherry" -> "Cherry";
            case "Peach" -> "Peach";
            case "Apricot" -> "Apricot";
            case "Juglans" -> "Walnut";
            default -> "N/A";
        };
    }

    /**
     * Method to return all trees from API
     *
     * @return List of all trees from API
     */
    public List<TreeModel> getAllTreesFromApi() {

        List<TreeModel> allTrees = new ArrayList<>();
        allTrees.addAll(getTreesFromApiByType("Apple"));
        allTrees.addAll(getTreesFromApiByType("Pear"));
        allTrees.addAll(getTreesFromApiByType("Cherry"));
        allTrees.addAll(getTreesFromApiByType("Peach"));
        allTrees.addAll(getTreesFromApiByType("Apricot"));

        return allTrees;

    }

    /**
     * Logic for requesting trees of a type from API
     * @param type tree type requested by common name
     * @return List of tree models of requested type from API
     */
    private List<TreeModel> getTreesFromApiByType(String type) {
        Request request = apiHandler.getTrees("CommonName='" + type + "'",
                "TreeID,Ownership,Genus,CommonName",
                100);

        if (request.getResponseCode() != HttpURLConnection.HTTP_OK) {
            LOG.warn("API connection failed with a {}", request.getResponseCode());
            return List.of();
        }

        ApiResponse response = (ApiResponse) request.getApiResponse();

        LOG.info(response);

        List<Tree> treeRecords = Arrays.asList(
                response.featureTrees()
        );

        return treeRecords.stream().map(this::createTreeObject)
                .toList();
    }

    /**
     * Constructs an API request that returns all trees of commonName = type and within the bounding box
     *
     * @param type the commonName of the tree being requested
     * @param north the max y value for the region
     * @param east the max x value for the region
     * @param south the min y value for the region
     * @param west the min x value for the region
     */
    public List<TreeModel> getTreesByRegion(String type, double north, double east, double south, double west) {
        Request request = apiHandler.getTrees(String.format(
                "CommonName='%s'", type
                ),
                "TreeID,Ownership,Genus,CommonName",
                100,
                north,
                east,
                south,
                west);

        if (request.getResponseCode() != HttpURLConnection.HTTP_OK) {
            LOG.warn("API connection failed with a {}", request.getResponseCode());
            return List.of();
        }

        ApiResponse response = (ApiResponse) request.getApiResponse();

        LOG.info(response);

        List<Tree> treeRecords = Arrays.asList(
                response.featureTrees()
        );

        return treeRecords.stream().map(this::createTreeObject)
                .toList();
    }

}
