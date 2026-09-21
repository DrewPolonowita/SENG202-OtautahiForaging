package seng202.team4.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.gui.screens.MapController;
import seng202.team4.gui.screens.SettingsController;
import seng202.team4.models.Fruit;
import seng202.team4.models.TreeModel;
import seng202.team4.services.dataCentre.treeData.TreeDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A service class for the Map to handle getting trees and filtering the map
 */
public class MapService {
    private static final Logger LOG = LogManager.getLogger(MapService.class);
    private final TreeDataManager treeDataManager;
    private final ArrayList<Fruit> selectedFruits;
    private final ExecutorService executor = Executors.newFixedThreadPool(8, runnable -> {
        Thread thread = new Thread(runnable, "map-service-worker");
        thread.setDaemon(true);
        return thread;
    });

    /**
     * Creates a new instance of a MapService
     * @param treeDataManager the treeService to query for trees
     */
    public MapService(TreeDataManager treeDataManager) {
        this.treeDataManager = treeDataManager;
        this.selectedFruits = new ArrayList<>();
    }

    /**
     * Returns all the trees in a given region which are selected using the filter gets all the trees
     * from the api/database on different threads then gives all of them using a Future
     *
     * @param north the north-bound
     * @param east the east-bound
     * @param south the south-bound
     * @param west the west-bound
     * @return a future of all the trees in a given region which are selected using the filter
     */
    public CompletableFuture<List<TreeModel>> getAllTreesInRegion(
            double north, double east, double south, double west
    ) {
        LOG.info(selectedFruits);
        List<CompletableFuture<List<TreeModel>>> futures = selectedFruits.stream()
                .map(fruit -> CompletableFuture.supplyAsync(() ->
                                treeDataManager.getTreesByRegion(fruit.toString(), north, east, south, west),
                        executor
                ))
                .toList();

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        return allTasks.thenApply(v -> {
            List<TreeModel> combinedTrees = new ArrayList<>();
            for (CompletableFuture<List<TreeModel>> future : futures) {
                combinedTrees.addAll(future.join());
            }
            return combinedTrees;
        });
    }

    /**
     * Adds a selected fruit to the filter
     * @param fruit the fruit to add to the filter
     */
    public void addSelectedFruit(Fruit fruit) {
        if (fruit == null) {
            throw new NullPointerException("fruit parameter is null");
        }

        if (selectedFruits.contains(fruit)) {
            String message = "Fruit: " + fruit.toString() + " is already selected";
            throw new IllegalArgumentException(message);
        }

        selectedFruits.add(fruit);
    }

    /**
     * Removes a fruit from the filter
     * @param fruit the friut to be unselected
     */
    public void removeSelectedFruit(Fruit fruit) {
        if (fruit == null) {
            throw new NullPointerException("fruit parameter is null");
        }

        if (!selectedFruits.contains(fruit)) {
            String message = "Fruit: " + fruit.toString() + " is not currently selected";
            throw new IllegalArgumentException(message);
        }

        selectedFruits.remove(fruit);
    }
}
