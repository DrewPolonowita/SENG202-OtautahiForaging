package seng202.team4.services.treeData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.TreeModel;
import seng202.team4.models.api.TreeApi.TreeApiResponse;
import seng202.team4.models.api.Request;
import seng202.team4.models.api.TreeApi.TreeRecord;
import seng202.team4.services.FoodService;
import seng202.team4.services.api.TreeClientInterface;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Service class to handle business logic for managing trees
 * @author Team 4
 */
public class TreeApiHandler {
    private static final Logger LOG = LogManager.getLogger(TreeApiHandler.class);
    private final TreeClientInterface treeApiHandler;
    private final FoodService foodService;

    /**
     * Constructor for the TreeApiHandeler
     */
    public TreeApiHandler(TreeClientInterface treeApiClient, FoodService foodService) {
        this.treeApiHandler = treeApiClient;
        this.foodService = foodService;
    }

    /**
     * Method to take a Tree (record) object and represent it as TreeModel object
     * @param treeRecord tree record
     * @return TreeModel tree model object
     */
    private TreeModel createTreeObject(TreeRecord treeRecord) {
        String genus = treeRecord.properties().genus();
        String commonName = treeRecord.properties().commonName();
        String foodType = getFoodType(commonName);

        return new TreeModel(
                treeRecord.properties().treeId(),
                treeRecord.geometry().coordinates(),
                treeRecord.properties().ownership(),
                genus,
                foodType,
                commonName,
                null,
                null);
    }

    /**
     * Mapping the common name to a food type in a new column
     * @param commonName common name
     * @return the food type based on commonName
     */
    private String getFoodType(String commonName) {

        Map<String, List<String>> foodMap = foodService.getFoodMap();


        for (Map.Entry<String, List<String>> entry : foodMap.entrySet()) {
            List<String> commonNames = entry.getValue();
            for (String name : commonNames) {
                if (name.equals(commonName)) {
                    return entry.getKey();
                }
            }
        }
        LOG.warn("No food mapping available for {}", commonName);
        return "N/A";
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
     * Creates an API Query to find all the foods using the different common
     * names that a food can be under
     * @param food the food to find
     * @return the api query string for use by API Handler
     */
    private String createApiTypeQuery(String food) {
        Map<String, List<String>> foodMap = foodService.getFoodMap();
        List<String> commonNames;
        if (foodMap.get(food) == null) {
            commonNames = List.of();
        } else {
            commonNames = foodMap.get(food);
        }

        StringBuilder queryString = new StringBuilder();
        for (int i = 0; i < commonNames.size(); i++) {
            String commonName = commonNames.get(i);
            if (i > 0) {
                queryString.append(" OR ");
            }
            queryString.append("CommonName='");
            queryString.append(commonName);
            queryString.append("'");
        }

        return queryString.toString();

    }

    /**
     * Method to return all trees from API
     *
     * @return List of all trees from API
     */
    public List<TreeModel> getAllTreesFromApi() {

        List<TreeModel> allTrees = new ArrayList<>();
        allTrees.addAll(getTreesFromApiByFood("Apple"));
        allTrees.addAll(getTreesFromApiByFood("Pear"));
        allTrees.addAll(getTreesFromApiByFood("Cherry"));
        allTrees.addAll(getTreesFromApiByFood("Peach"));
        allTrees.addAll(getTreesFromApiByFood("Apricot"));

        return allTrees;

    }

    /**
     * Logic for requesting trees of a food from API
     * @param food tree food requested by common name
     * @return List of tree models of requested food from API
     */
    public List<TreeModel> getTreesFromApiByFood(String food) {
        Request request = treeApiHandler.getTrees(createApiTypeQuery(food),
                "TreeID,Ownership,Genus,CommonName",
                100);
        if (request.getResponseCode() != HttpURLConnection.HTTP_OK) {
            LOG.warn("API connection failed with a {}", request.getResponseCode());
            return List.of();
        }

        TreeApiResponse response = (TreeApiResponse) request.getApiResponse();

        LOG.info(response);

        List<TreeRecord> treeRecords = Arrays.asList(
                response.featureTrees()
        );

        return treeRecords.stream().map(this::createTreeObject)
                .toList();
    }

    /**
     * Constructs an API request that returns all trees of commonName = type and within the bounding box
     *
     * @param commonName the commonName of the tree being requested
     * @param north the max y value for the region
     * @param east the max x value for the region
     * @param south the min y value for the region
     * @param west the min x value for the region
     */
    public List<TreeModel> getTreesByRegion(String commonName, double north, double east, double south, double west) {
        Request request = treeApiHandler.getTrees(createApiTypeQuery(commonName),
                "TreeID,Ownership,Genus,CommonName",
                1000,
                north,
                east,
                south,
                west);

        if (request.getResponseCode() != HttpURLConnection.HTTP_OK) {
            LOG.warn("API connection failed with a {}", request.getResponseCode());
            return List.of();
        }

        TreeApiResponse response = (TreeApiResponse) request.getApiResponse();

        LOG.info(response);

        List<TreeRecord> treeRecords = Arrays.asList(
                response.featureTrees()
        );

        return treeRecords.stream().map(this::createTreeObject)
                .toList();
    }

}
