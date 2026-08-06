package seng202.team4.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.TreeModel;
import seng202.team4.models.api.ApiResponse;
import seng202.team4.models.api.Request;
import seng202.team4.models.api.Tree;

import java.net.HttpURLConnection;
import java.util.ArrayList;
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
    public List<TreeModel> getAllTrees() {
        List<TreeModel> allTrees = new ArrayList<>();
        allTrees.addAll(getAppleTrees());
        allTrees.addAll(getPearTrees());
        allTrees.addAll(getCherryTrees());
        allTrees.addAll(getPeachTrees());
        allTrees.addAll(getApricotTrees());

        return allTrees;

    }

    /**
     * Method to filter and return apple trees from API
     * @return List of apple trees from API
     */
    public List<TreeModel> getAppleTrees() {
        Request request = apiHandler.getTrees("CommonName='Apple'", "TreeID,Ownership,Genus,CommonName", 100);

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

    /**
     * Method to filter and return pear trees from API
     * @return List of pear trees from API
     */
    public List<TreeModel> getPearTrees() {
        Request request = apiHandler.getTrees("CommonName='Pear'", "TreeID,Ownership,Genus, CommonName", 100);

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

    /**
     * Method to filter and return cherry trees from API
     * @return List of cherry trees from API
     */
    public List<TreeModel> getCherryTrees() {
        Request request = apiHandler.getTrees("CommonName='Cherry'", "TreeID,Ownership,Genus,CommonName", 100);

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

    /**
     * Method to filter and return peach trees from API
     * @return List of peach trees from API
     */
    public List<TreeModel> getPeachTrees() {
        Request request = apiHandler.getTrees("CommonName='Peach'", "TreeID,Ownership,Genus,CommonName", 100);

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

    /**
     * Method to filter and return apricot trees from API
     * @return List of apricot trees from API
     */
    public List<TreeModel> getApricotTrees() {
        Request request = apiHandler.getTrees("CommonName='Apricot'", "TreeID,Ownership,Genus,CommonName", 100);

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
