package seng202.team4.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.TreeModel;
import seng202.team4.models.api.ApiResponse;
import seng202.team4.models.api.Request;
import seng202.team4.models.api.Tree;
import seng202.team4.repository.TreeDAO;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service class to handle business logic for managing trees
 * @author Team 4
 */
public class TreeService {
    private static final Logger log = LogManager.getLogger(TreeService.class);
    private final ApiHandler apiHandler;
    private final StateManager stateManager;
    private final TreeDAO treeDAO;

    /**
     * Constructor
     */
    public TreeService(StateManager stateManager) {
        this.apiHandler = new ApiHandler();
        this.stateManager = stateManager;
        this.treeDAO = new TreeDAO(this.stateManager.getDatabaseManager());
    }

    /**
     * Constructor for testing
     * @param apiHandler mock apiHandler
     */
    public TreeService(ApiHandler apiHandler) {
        this.apiHandler = apiHandler;
        this.stateManager = null;
        this.treeDAO = null;
    }

    /**
     * Method to take a Tree (record) object and represent it as TreeModel object
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
            treeDAO.add(tree);
        }
        databaseTrees = treeDAO.getAll();

        return databaseTrees;
    }



    /**
     * Method to filter and return apple trees
     * @return List of apple trees
     */
    public List<TreeModel> getAppleTrees() {
        return getTreesByType("Apple");
    }

    /**
     * Method to filter and return pear trees
     * @return List of pear trees
     */
    public List<TreeModel> getPearTrees() {
        return getTreesByType("Pear");
    }

    /**
     * Method to filter and return cherry trees
     * @return List of cherry trees
     */
    public List<TreeModel> getCherryTrees() {
        return getTreesByType("Cherry");
    }

    /**
     * Method to filter and return peach trees
     * @return List of peach trees
     */
    public List<TreeModel> getPeachTrees() {
        return getTreesByType("Peach");
    }

    /**
     * Method to filter and return apricot trees
     * @return List of apricot trees
     */
    public List<TreeModel> getApricotTrees() {
        return getTreesByType("Apricot");
    }

    /**
     * Return all trees of a particular type in the database (using common name)
     * If the database has none of the type, use the api to populate database
     * @param type Tree type by common name
     * @return List of trees of specified type
     */
    public List<TreeModel> getTreesByType(String type) {
        List<TreeModel> databaseTrees = treeDAO.getTreesByCommonName(type);

        if (!databaseTrees.isEmpty()) {
            log.info("Loaded {} {} trees from database", databaseTrees.size(), type);
            return databaseTrees;
        }

        List<TreeModel> apiTreesByType = getTreesFromApiByType(type);
        for (TreeModel tree : apiTreesByType) {
            treeDAO.add(tree);
        }
        databaseTrees = treeDAO.getTreesByCommonName(type);
        return databaseTrees;
    }
    /**
     * Logic for requesting trees of a type from API
     * @param type tree type requested by common name
     * @return List of tree models of requested type from API
     */
    public List<TreeModel> getTreesFromApiByType(String type) {
        Request request = apiHandler.getTrees("CommonName='" + type + "'",
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
