package seng202.team4.gui.screens;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.Node;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.gui.ScreenController;
import seng202.team4.gui.bridge.JavaScriptBridge;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import seng202.team4.models.Food;
import seng202.team4.models.FoodServing;
import seng202.team4.models.Fruit;
import seng202.team4.models.TreeModel;
import seng202.team4.services.MapService;
import seng202.team4.services.StateManager;
import seng202.team4.services.ThemeService;
import seng202.team4.services.dataCentre.foodData.FoodDataManager;
import seng202.team4.services.dataCentre.treeData.TreeDataManager;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for the map_screen.fxml window which controls the map
 * @author Team 4
 */
public class MapController extends ScreenController {

    private static final Logger LOG = LogManager.getLogger(MapController.class);
    private final MapService mapService;

    private final TreeDataManager treeService;
    private final FoodDataManager foodService;
    private static final double MAX_HEIGHT_FOR_DISPLAYING = 1;

    private double northBound;
    private double eastBound;
    private double southBound;
    private double westBound;

    @FXML
    private WebView webView;
    @FXML
    private VBox filterBox;
    private boolean filterOpened;
    @FXML
    private Button filterButton;

    private WebEngine webEngine;
    private JavaScriptBridge javaScriptBridge;
    private JSObject javaScriptConnector;

    private StateManager stateManager;

    private final ThemeService themeService;

    private final Map<Integer, TreeModel> currentTrees = new HashMap<>();

    private final TreeModel targetTree;
    private List<Food> foods = new ArrayList<>();
    private boolean foodsLoaded = false;

    private static final String SIDEBAR_BUTTON_SELECTED = "sideBar-button-selected";
    private static final String ICON_SELECTED = "icon-selected";


    /**
     * Initialise the window
     *
     * @param stateManager Reference for window manager
     */
    public MapController(StateManager stateManager, ThemeService themeService, TreeModel targetTree) {
        super(stateManager);
        this.stateManager = stateManager;
        this.treeService = stateManager.getTreeService();
        this.mapService = new MapService(treeService);
        this.themeService = themeService;
        this.targetTree = targetTree;
        this.foodService = stateManager.getFoodService();
    }

    /**
     * Initialises the bridge and loads the map once FXML is loaded with lambda functionality passed in.
     * Also creates filter buttons from enum FRUIT values
     */
    @FXML
    private void initialize() {
        LOG.info("MapController initialize() called");
        javaScriptBridge = new JavaScriptBridge(
                id -> {
                    TreeModel tree = currentTrees.get(id);
                    if (tree != null) {
                        stateManager.getWindowManager().openAddNoteMenu(tree, stateManager, null);
                    } else {
                        LOG.warn("Add to Journal clicked for unknown tree id {}", id);
                    }
                },
                tree -> { },
                id -> false, // TODO: replace with real handleTreeClicked + add-tree flow
                this::mapMoved
        );
        createFruitButtons();
        toggleAllFilterButtons();

        initMap();
        CompletableFuture
                .supplyAsync(foodService::getFoods)
                .thenAccept(loadedFoods -> Platform.runLater(() -> {
                    foods = loadedFoods;
                    foodsLoaded = true;
                    LOG.info("Found {} foods", foods.size());

                    if (northBound != 0 || eastBound != 0 || southBound != 0 || westBound != 0) {
                        mapMoved(northBound, eastBound, southBound, westBound);
                    }
                }))
                .exceptionally(ex -> {
                    LOG.error("Failed to load foods", ex);
                    return null;
                });
    }

    /**
     * Loads the Leaflet map HTML into the WebView and wires up the JS bridge
     */
    private void initMap() {
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.load(getClass().getClassLoader().getResource("html/foraging_map.html").toExternalForm());
        WebConsoleListener.setDefaultListener((view, message, lineNumber, sourceId) ->
                LOG.info(String.format("Map WebView console log line: %d, message : %s", lineNumber, message)));

        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        themeService.setWebEngine(webEngine);
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaScriptBridge", javaScriptBridge);
                        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");
                        if (targetTree == null) {
                            javaScriptConnector.call("initMap");
                        } else {
                            javaScriptConnector.call("initMap", targetTree.getYCoord(), targetTree.getXCoord(), 17, targetTree.getId());
                        }
                    }
                });
    }

    public String getFxmlFile() {
        return "/fxml/map_screen.fxml";
    }

    public String getTitle() {
        return "Map";
    }

    @FXML
    public void filterClicked() {
        if (filterOpened) {
            filterBox.setVisible(false);
            filterOpened = false;
            filterButton.getStyleClass().remove("filters-selected");
            filterButton.getGraphic().getStyleClass().remove(ICON_SELECTED);
        } else {
            filterBox.setVisible(true);
            filterOpened = true;
            filterButton.getStyleClass().add("filters-selected");
            filterButton.getGraphic().getStyleClass().add(ICON_SELECTED);
        }
    }

    private void setCurrentBounds(double north, double east, double south, double west) {
        this.northBound = north;
        this.eastBound = east;
        this.southBound = south;
        this.westBound = west;
    }

    /**
     * Triggered when the map is moved by the user and queries a region of trees in an area of 2x the screen
     * width and height centered on the screen. Also removes previous tree markers and adds the new ones.
     *
     * @param north the north-bound
     * @param east the east-bound
     * @param south the south-bound
     * @param west the west-bound
     */
    private void mapMoved(double north, double east, double south, double west) {
        double height = Math.abs(north - south);
        double width = Math.abs(east - west);

        if (height > MAX_HEIGHT_FOR_DISPLAYING) {
            removeTreeMarkers();
            return;
        }

        setCurrentBounds(north, east, south, west);
        if (!foodsLoaded) {
            return;
        }
        mapService.getAllTreesInRegion(
                north + height / 4,
                east + width / 4,
                south - height / 4,
                west - width / 4
        ).thenAccept(trees -> {
            javafx.application.Platform.runLater(() -> {
                if (trees != null) {
                    LOG.info(MessageFormat.format("Completed request for {0} trees.", trees.size()));
                    removeTreeMarkers();
                    for (TreeModel tree : trees) {
                        Food treeFood = findTreeFood(tree.getFood());
                        if (treeFood != null) {
                            addTreeMarker(tree, treeFood);
                        }
                    }
                }
            });
        }).exceptionally(ex -> {
            if (!(ex instanceof java.util.concurrent.CancellationException)) {
                javafx.application.Platform.runLater(() -> {
                    LOG.error("Error fetching trees for map view: " + ex);
                });
            }
            return null;
        });
    }

    private Food findTreeFood(String foodName) {
        for (Food food : foods) {
            if (food.getName().equalsIgnoreCase(foodName)) {
                return food;
            }
        }
        return null;
    }


    /**
     * Formatting the string popup for tree data points on Leaflet
     * Each tree added as a marker will have a popup description
     */
    private void addTreeMarker(TreeModel tree, Food food) {
        currentTrees.put(tree.getId(), tree);
        String popup = buildMarkerHtml(tree, food);

        javaScriptConnector.call("addMarker",
                tree.getId(),
                popup,
                tree.getFood(),
                tree.getYCoord(),
                tree.getXCoord()
        );
    }


    private String buildMarkerHtml(TreeModel tree, Food food) {
        String popup = String.format(
                "<div class=\"tree-card\">"
                        + "<div class=\"tree-card-body\">"
                        + "<b>%s</b><br>" //food
                        + "<span style=\"font-size: 0.8em; opacity: 0.7;\">Genus: %s</span>" //genus
                        + "<select class=\"serving-select\" onchange=\"updateNutrition(this)\">"
                        + buildServingOptions(food)
                        + "</select>"
                        + "<div class=\"nutrition-info\">"
                        + "<div class=\"calories\">Calories: %f kcal </div>" //cal
                        + "<div class=\"carbohydrates\">Carbohydrates: %f g</div>" //carb
                        + "<div class=\"protein\">Protein: %f g</div>" //protein
                        + "<div class=\"fat\">Fat: %f g </div>" // fat
                        + "</div>"
                        + "<button class=\"journal-btn\" onclick=\"javaScriptBridge.addTreeToJournal(%d)\">Add to Journal</button>" // id
                        + "</div></div>",
                tree.getFood(),
                tree.getGenus(),
                food.getDefaultServing().getCalories(),
                food.getDefaultServing().getCarbohydrates(),
                food.getDefaultServing().getProtein(),
                food.getDefaultServing().getFat(),
                tree.getId()
        );

        return popup;
    }

    private String buildServingOptions(Food food) {
        StringBuilder servingOptions = new StringBuilder();

        for (FoodServing serving : food.getServings()) {
            servingOptions.append(String.format(
                    "<option data-calories=\"%f\" data-carbohydrates=\"%f\" data-protein=\"%f\" data-fat=\"%f\">%s</option>",
                    serving.getCalories(),
                    serving.getCarbohydrates(),
                    serving.getProtein(),
                    serving.getFat(),
                    serving.getDescription()
            ));
        }

        return servingOptions.toString();
    }

    /**
     * Removes all the tree markers on the screen
     */
    private void removeTreeMarkers() {
        currentTrees.clear();
        javaScriptConnector.call("removeMarkers");
    }

    /**
     * For each fruit name in the enum FRUIT, a filter button is created
     */
    private void createFruitButtons() {
        filterBox.getChildren().clear();
        for (Fruit fruit : Fruit.values()) {
            Button filterButton = new Button("  " + formatFruitName(fruit));
            filterButton.setAlignment(Pos.TOP_LEFT);
            filterButton.setMaxWidth(Double.MAX_VALUE);
            filterButton.setPrefHeight(35);
            Region icon = new Region();
            icon.getStyleClass().add("icon");
            icon.getStyleClass().add("icon-" + fruit.name().toLowerCase());
            filterButton.setGraphic(icon);
            filterButton.getStyleClass().add("sideBar-button");
            filterButton.setOnAction(event -> filterButtonClicked(filterButton, fruit));
            filterBox.getChildren().add(filterButton);
        }
    }

    /**
     * Toggles styling of the fruit filter buttons on the Map screen
     */
    public static void toggleButtonStyle(Button button) {
        if (button.getStyleClass().contains(SIDEBAR_BUTTON_SELECTED)) {
            button.getStyleClass().remove(SIDEBAR_BUTTON_SELECTED);
            button.getGraphic().getStyleClass().remove(ICON_SELECTED);
        } else {
            button.getStyleClass().add(SIDEBAR_BUTTON_SELECTED);
            button.getGraphic().getStyleClass().add(ICON_SELECTED);
        }
    }

    /**
     * Formats the enum FRUIT values to a string format
     * which will be shown to the user when filter button is clicked
     *
     */
    private static String formatFruitName(Fruit fruit) {
        String name = fruit.name().toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * Toggles the fruit button being clicked, and updates the
     * map to show only the relevant fruit trees
     * @param buttons
     * @param fruit
     */
    public void filterButtonClicked(Button buttons, Fruit fruit) {
        if (buttons.getStyleClass().contains(SIDEBAR_BUTTON_SELECTED)) {
            buttons.getStyleClass().remove(SIDEBAR_BUTTON_SELECTED);
            buttons.getGraphic().getStyleClass().remove(ICON_SELECTED);

            mapService.removeSelectedFruit(fruit);
        } else {
            buttons.getStyleClass().add(SIDEBAR_BUTTON_SELECTED);
            buttons.getGraphic().getStyleClass().add(ICON_SELECTED);

            mapService.addSelectedFruit(fruit);
        }
        mapMoved(northBound, eastBound, southBound, westBound);
    }

    /**
     * Toggles on all the fruits which are displayed on the map by default
     * when the application is first run
     */
    public void toggleAllFilterButtons() {
        for (Node node : filterBox.getChildren()) {
            Button button = (Button) node;
            toggleButtonStyle(button);
        }
        for (Fruit fruit : Fruit.values()) {
            mapService.addSelectedFruit(fruit);
        }
    }

}
