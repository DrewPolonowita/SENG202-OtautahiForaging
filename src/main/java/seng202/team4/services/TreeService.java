package seng202.team4.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.models.TreeModel;
import seng202.team4.models.api.ApiResponse;
import seng202.team4.models.api.Request;
import seng202.team4.models.api.Tree;
import seng202.team4.repository.TreeDAO;
import seng202.team4.repository.TreeRepository;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Service class to handle business logic for managing trees
 * @author Team 4
 */
public class TreeService {
    private static final Logger log = LogManager.getLogger(TreeService.class);
    private final ApiHandler apiHandler;
    private final TreeRepository treeDAO;
    private final FoodService foodService;

    /**
     * Constructor
     */
    public TreeService(TreeRepository treeRepository) {
        this.apiHandler = new ApiHandler();
        this.treeDAO = treeRepository;
        this.foodService = new FoodService();
    }

    /**
     * Constructor for testing
     * @param apiHandler mock apiHandler
     */
    public TreeService(ApiHandler apiHandler, TreeDAO treeDAO) {
        this.apiHandler = apiHandler;
        this.treeDAO = treeDAO;
        this.foodService = new FoodService();
    }

    /**
     * Method to take a Tree (record) object and represent it as TreeModel object
     * @param treeRecord tree record
     * @return TreeModel tree model object
     */
    private TreeModel createTreeObject(Tree treeRecord) {
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
        log.warn("No fruit mapping available for {}", commonName);
        return "N/A";
    }

    /**
     * Method to return all trees from API
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
     * Return all the trees in the database if the database is populated
     * otherwise populate it
     * @return return all trees
     */
    public List<TreeModel> getAllTrees() {

        List<TreeModel> databaseTrees = treeDAO.getAll();

        if (!databaseTrees.isEmpty()) {
            log.info("Loaded {} trees from database", databaseTrees.size());
            return databaseTrees;
        }

        List<TreeModel> apiTrees = getAllTreesFromApi();
        log.info("Loaded {} trees from api", apiTrees.size());
        for (TreeModel tree: apiTrees) {
            try {
                treeDAO.add(tree);
            } catch (DuplicateEntryException e) {
                log.warn("(All Trees) Skip duplicate id: {}", tree.getId());
            }
        }
        databaseTrees = treeDAO.getAll();

        return databaseTrees;
    }


    /**
     * Return all trees of a particular type in the database (using food, derived from common name)
     * If the database has none of the type, use the api to populate database
     * @param food Tree type by food
     * @return List of trees of specified type
     */
    public List<TreeModel> getTreesByFood(String food) {
        List<TreeModel> databaseTrees = treeDAO.getTreesByFoodName(food);

        if (!databaseTrees.isEmpty()) {
            log.info("Loaded {} {} trees from database", databaseTrees.size(), food);
            return databaseTrees;
        }

        List<TreeModel> apiTreesByType = getTreesFromApiByFood(food);
        for (TreeModel tree : apiTreesByType) {
            try {
                treeDAO.add(tree);
            } catch (DuplicateEntryException e) {
                log.warn("(Tree by food: {}) Skip duplicate id: {}", tree.getFood(), tree.getId());
            }
        }
        databaseTrees = treeDAO.getTreesByFoodName(food);
        return databaseTrees;
    }

    /**
     * Creates an API Query to find all the foods using the different common
     * names that a food can be under
     * @param food the food to find
     * @return the api query string for use by API Handler
     */
    private String createApiTypeQuery(String food) {
        Map<String, List<String>> foodMap = foodService.getFoodMap();

        List<String> commonNames = foodMap.get(food);

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
     * Logic for requesting trees with a certain food from API
     * @param food tree type requested by the food on the tree
     * @return List of tree models with requested food from API
     */
    private List<TreeModel> getTreesFromApiByFood(String food) {
        String queryString = createApiTypeQuery(food);
        Request request = apiHandler.getTrees(queryString,
                "TreeID,Ownership,Genus,CommonName",
                100);

        if (request.getResponseCode() != HttpURLConnection.HTTP_OK) {
            log.info("API connection failed with a " + request.getResponseCode());
            return List.of();
        }

        ApiResponse response = (ApiResponse) request.getApiResponse();

        log.info(response);

        List<Tree> treeRecords = Arrays.asList(
                response.featureTrees()
        );

        return treeRecords.stream().map(this::createTreeObject)
                .toList();
    }


}
