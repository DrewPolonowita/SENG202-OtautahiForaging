package seng202.team4.services;

import seng202.team4.models.Fruit;
import seng202.team4.models.TreeModel;
import seng202.team4.services.treeData.TreeDataManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A service class for the Map to handle getting trees and filtering the map
 */
public class MapService {

    private TreeDataManager treeDataManager;
    private ArrayList<Fruit> selectedFruits;

    /**
     * Creates a new instance of a MapService
     * @param treeDataManager the treeService to query for trees
     */
    public MapService(TreeDataManager treeDataManager) {
        this.treeDataManager = treeDataManager;
        this.selectedFruits = new ArrayList<>();
    }

    /**
     * Returns all the trees in a given region which are selected using the filter
     *
     * @param north the north-bound
     * @param east the east-bound
     * @param south the south-bound
     * @param west the west-bound
     * @return all the trees in a given region which are selected using the filter
     */
    public List<TreeModel> getAllTreesInRegion(
            double north, double east, double south, double west
    ) {
        ArrayList<TreeModel> trees = new ArrayList<>();

        for (Fruit fruit: selectedFruits) {
            trees.addAll(
                    treeDataManager.getTreesByRegion(fruit.toString(), north, east, south, west)
            );
        }

        return trees;
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

        System.out.println(selectedFruits);
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

        System.out.println(selectedFruits);
    }
}
