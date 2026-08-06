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

    /**
     * Method to filter and return apple trees from API
     * @return List of apple trees from API
     */
    public List<TreeModel> getAppleTrees() {
        Request request = apiHandler.getTrees("CommonName='Apple'", "TreeID,Ownership,Genus", 100);

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
        Request request = apiHandler.getTrees("CommonName='Pear'", "TreeID,Ownership,Genus", 100);

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
        Request request = apiHandler.getTrees("CommonName='Cherry'", "TreeID,Ownership,Genus", 100);

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
        Request request = apiHandler.getTrees("CommonName='Peach'", "TreeID,Ownership,Genus", 100);

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
        Request request = apiHandler.getTrees("CommonName='Apricot'", "TreeID,Ownership,Genus", 100);

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
