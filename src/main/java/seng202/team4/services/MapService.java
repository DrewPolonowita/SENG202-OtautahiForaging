package seng202.team4.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.FilterCategory;
import seng202.team4.models.TreeModel;
import seng202.team4.services.dataCentre.treeData.TreeDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A service class for the Map to handle getting trees and filtering the map
 */
public class MapService {
    private static final Logger LOG = LogManager.getLogger(MapService.class);
    private final TreeDataManager treeDataManager;
    private final ArrayList<FilterCategory> selectedFilterCategories;
    private final ExecutorService executor = Executors.newFixedThreadPool(8, runnable -> {
        Thread thread = new Thread(runnable, "map-service-worker");
        thread.setDaemon(true);
        return thread;
    });
    private final AtomicInteger currentRequestCounter = new AtomicInteger(0);

    private boolean isRequestRunning = false;
    private PendingRequest pendingRequest = null;

    // Inner class to act as our stored function pointer + future
    private class PendingRequest {
        double n;
        double e;
        double s;
        double w;
        CompletableFuture<List<TreeModel>> future;

        PendingRequest(double n, double e, double s, double w, CompletableFuture<List<TreeModel>> future) {
            this.n = n;
            this.e = e;
            this.s = s;
            this.w = w;
            this.future = future;
        }
    }

    /**
     * Creates a new instance of a MapService
     * @param treeDataManager the treeService to query for trees
     */
    public MapService(TreeDataManager treeDataManager) {
        this.treeDataManager = treeDataManager;
        this.selectedFilterCategories = new ArrayList<>();
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
    public CompletableFuture<List<TreeModel>> actualBackendCall(
            double north, double east, double south, double west
    ) {
        int thisRequest = currentRequestCounter.incrementAndGet();

        LOG.info(selectedFilterCategories);

        List<CompletableFuture<List<TreeModel>>> futures = selectedFilterCategories.stream()
                .map(fruit -> CompletableFuture.supplyAsync(() ->
                                treeDataManager.getTreesByRegion(fruit.toString(), north, east, south, west),
                        executor
                ))
                .toList();

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        return allTasks.thenApply(v -> {
            if (thisRequest != currentRequestCounter.get()) {
                return null;
            }

            List<TreeModel> combinedTrees = new ArrayList<>();
            for (CompletableFuture<List<TreeModel>> future : futures) {
                combinedTrees.addAll(future.join());
            }
            return combinedTrees;
        });
    }

    /**
     * Adds a selected fruit to the filter
     * @param filterCategory the fruit to add to the filter
     */
    public void addSelectedFruit(FilterCategory filterCategory) {
        if (filterCategory == null) {
            throw new NullPointerException("fruit parameter is null");
        }

        if (selectedFilterCategories.contains(filterCategory)) {
            String message = "Fruit: " + filterCategory.toString() + " is already selected";
            throw new IllegalArgumentException(message);
        }

        selectedFilterCategories.add(filterCategory);
    }

    /**
     * Removes a fruit from the filter
     * @param filterCategory the friut to be unselected
     */
    public void removeSelectedFruit(FilterCategory filterCategory) {
        if (filterCategory == null) {
            throw new NullPointerException("fruit parameter is null");
        }

        if (!selectedFilterCategories.contains(filterCategory)) {
            String message = "Fruit: " + filterCategory.toString() + " is not currently selected";
            throw new IllegalArgumentException(message);
        }

        selectedFilterCategories.remove(filterCategory);
    }

    public synchronized CompletableFuture<List<TreeModel>> getAllTreesInRegion(double north, double east, double south, double west) {
        CompletableFuture<List<TreeModel>> resultFuture = new CompletableFuture<>();

        if (isRequestRunning) {
            if (pendingRequest != null) {
                pendingRequest.future.cancel(false);
            }
            pendingRequest = new PendingRequest(north, east, south, west, resultFuture);
        } else {
            executeNetworkCall(north, east, south, west, resultFuture);
        }

        return resultFuture;
    }

    private void executeNetworkCall(double n, double e, double s, double w, CompletableFuture<List<TreeModel>> futureToComplete) {
        isRequestRunning = true;

        actualBackendCall(n, e, s, w)
                .thenAccept(trees -> {
                    futureToComplete.complete(trees);
                    checkQueue();
                })
                .exceptionally(ex -> {
                    futureToComplete.completeExceptionally(ex);
                    checkQueue();
                    return null;
                });
    }

    private synchronized void checkQueue() {
        if (pendingRequest != null) {
            PendingRequest next = pendingRequest;
            pendingRequest = null;
            executeNetworkCall(next.n, next.e, next.s, next.w, next.future);
        } else {
            isRequestRunning = false;
        }
    }
}
