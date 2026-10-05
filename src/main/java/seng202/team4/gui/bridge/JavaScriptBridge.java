package seng202.team4.gui.bridge;

import seng202.team4.models.TreeModel;

/**
 * The class that bridges from javascript to java
 * The functions within can be called from javascript in the map view when we set an object of this class
 * as a member of the javascript
 *
 * @author Team 4
 */

public class JavaScriptBridge {
    private final AddTreeInterface addTreeInterface;
    private final GoToRecipesInterface goToRecipesInterface;
    private final AddToJournalInterface addToJournalInterface;
    private final LoadNutritionInterface loadNutritionInterface;

    private final MapMovedInterface mapMovedInterface;

    /**
     * Creates JavaScript bridge object with a callback lambda function for displaying the tree on the map after creation
     * @param addToJournalLambda add a note to the journal for the given tree id
     * @param addTreeLambda add tree lambda function to add tree to js map
     */
    public JavaScriptBridge(AddToJournalInterface addToJournalLambda, GoToRecipesInterface goToRecipesInterface, AddTreeInterface addTreeLambda,  LoadNutritionInterface loadNutritionInterface, MapMovedInterface mapMovedInterface) {
        this.addToJournalInterface = addToJournalLambda;
        this.goToRecipesInterface = goToRecipesInterface;
        this.addTreeInterface = addTreeLambda;
        this.loadNutritionInterface = loadNutritionInterface;
        this.mapMovedInterface = mapMovedInterface;
    }

    /**
     * Creates a JavaScript bridge with default no-op callbacks
     */
    public JavaScriptBridge() {
        addToJournalInterface = (int i) -> { };
        goToRecipesInterface = (String name) -> { };
        addTreeInterface = (TreeModel tree) -> { };
        loadNutritionInterface = id -> { };
        mapMovedInterface = null;
    }

    public void mapMoved(double north, double east, double south, double west, double latitude, double longitude, double zoom) {
        assert mapMovedInterface != null;
        mapMovedInterface.mapMoved(north, east, south, west, latitude, longitude, zoom);
    }

    /**
     * Takes the id of a tree and passes this to the AddToJournalInterface
     * @param id id of tree to add to journal
     */
    public void addTreeToJournal(int id) {
        addToJournalInterface.operation(id);
    }

    public void goToRecipes(String foodName) {
        goToRecipesInterface.operation(foodName);
    }


    public void loadTreeNutrition(int id) {
        loadNutritionInterface.accept(id);
    }

}
