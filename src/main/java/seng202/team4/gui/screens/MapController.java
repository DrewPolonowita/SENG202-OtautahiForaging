package seng202.team4.gui.screens;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.Node;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.gui.ScreenController;
import seng202.team4.gui.bridge.JavaScriptBridge;
import seng202.team4.gui.utilities.FatSecretAttributionUtilities;
import seng202.team4.gui.utilities.FruitFilterUtilities;
import seng202.team4.gui.FilterManager;
import seng202.team4.models.FilterCategory;
import seng202.team4.models.Food;
import seng202.team4.models.TreeModel;
import seng202.team4.services.MapService;
import seng202.team4.services.StateManager;
import seng202.team4.services.ThemeService;
import seng202.team4.services.TreePopupBuilder;
import seng202.team4.services.dataCentre.foodData.FoodDataManager;
import seng202.team4.services.dataCentre.treeData.TreeDataManager;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Controller for the map_screen.fxml window which controls the map
 * @author Team 4
 */
public class MapController extends ScreenController {

    private static final Logger LOG = LogManager.getLogger(MapController.class);
    private static final String ICON_SELECTED = "icon-selected";
    private static final String UPDATE_POPUP_MARKER = "updateMarkerPopup";
    private static final double MAX_HEIGHT_FOR_DISPLAYING = 1;
    private final MapService mapService;
    private final FilterManager filterManager;
    private final FoodDataManager foodService;

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

    @FXML
    private VBox mapLoadingOverlay;
    @FXML
    private ProgressBar mapLoadingBar;
    @FXML
    private Label mapLoadingLabel;

    @FXML
    private StackPane attributionPane;

    @FXML
    private HBox filterActions;

    private WebEngine webEngine;
    private JavaScriptBridge javaScriptBridge;
    private JSObject javaScriptConnector;

    private final StateManager stateManager;

    private final ThemeService themeService;

    private final Map<Integer, TreeModel> currentTrees = new HashMap<>();

    private final TreeModel targetTree;




    /**
     * Initialise the window
     *
     * @param stateManager Reference for window manager
     */
    public MapController(StateManager stateManager, ThemeService themeService, TreeModel targetTree) {
        super(stateManager);
        this.stateManager = stateManager;
        TreeDataManager treeService = stateManager.getTreeService();
        this.mapService = new MapService(treeService);
        this.themeService = themeService;
        this.targetTree = targetTree;
        this.foodService = stateManager.getFoodService();
        this.filterManager = stateManager.getFilterManager();
    }

    /**
     * Initialises the bridge and loads the map once FXML is loaded with lambda functionality passed in.
     * Also creates filter buttons from enum FRUIT values
     */
    @FXML
    private void initialize() {
        LOG.info("MapController initialize() called");
        ImageView attribution = FatSecretAttributionUtilities.createStandardAttributionImage(stateManager.getWindowManager().getThemeService().isDarkMode());
        attributionPane.getChildren().add(attribution);
        StackPane.setAlignment(attribution, Pos.BOTTOM_RIGHT);

        showMapLoadingBar("Loading map...");
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
                this::loadNutritionForTree,
                this::mapMoved
        );
        createFruitButtons();
        initMap();

    }



    /**
     * Loads the Leaflet map HTML into the WebView and wires up the JS bridge
     */
    private void initMap() {
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.load(Objects.requireNonNull(getClass().getClassLoader().getResource("html/foraging_map.html")).toExternalForm());
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
                            javaScriptConnector.call("initMap",
                                    stateManager.getMapLatitude(),
                                    stateManager.getMapLongitude(),
                                    stateManager.getMapZoom());
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
            filterActions.setVisible(false);
            filterActions.setManaged(false);
            filterOpened = false;
            filterButton.getStyleClass().remove("filters-selected");
            filterButton.getGraphic().getStyleClass().remove(ICON_SELECTED);
        } else {
            filterBox.setVisible(true);
            filterActions.setVisible(true);
            filterActions.setManaged(true);
            filterOpened = true;
            filterButton.getStyleClass().add("filters-selected");
            filterButton.getGraphic().getStyleClass().add(ICON_SELECTED);

        }
    }

    /**
     * For all buttons in FilterCategory, loop through to find buttons not selected
     * and select all.
     * Update the map accordingly to the filters.
     */
    @FXML
    private void selectAllFilters() {
        filterManager.selectAllFilters();
        for (FilterCategory filterCategory : FilterCategory.values()) {
            mapService.addSelectedFruit(filterCategory);
        }
        for (int i = 0; i < filterBox.getChildren().size(); i++) {
            Button button = (Button) filterBox.getChildren().get(i);
            if (!FruitFilterUtilities.isSelected(button)) {
                FruitFilterUtilities.toggleButtonStyle(button);
            }
        }

        mapMoved(northBound, eastBound, southBound, westBound,
                stateManager.getMapLatitude(),
                stateManager.getMapLongitude(),
                stateManager.getMapZoom());
    }

    /**
     * For all buttons in FilterCategory, loop through to find buttons selected
     * and deselect all.
     * Update the map accordingly to the filters.
     */
    @FXML
    private void deselectAllFilters() {
        filterManager.deselectAllFilters();
        for (FilterCategory filterCategory : FilterCategory.values()) {
            mapService.removeSelectedFruit(filterCategory);
        }
        for (int i = 0; i < filterBox.getChildren().size(); i++) {
            Button button = (Button) filterBox.getChildren().get(i);
            if (FruitFilterUtilities.isSelected(button)) {
                FruitFilterUtilities.toggleButtonStyle(button);
            }
        }
        mapMoved(northBound, eastBound, southBound, westBound,
                stateManager.getMapLatitude(),
                stateManager.getMapLongitude(),
                stateManager.getMapZoom());
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
    private void mapMoved(double north, double east, double south, double west, double latitude, double longitude, double zoom) {
        double height = Math.abs(north - south);
        double width = Math.abs(east - west);

        stateManager.setMapView(latitude, longitude, zoom);

        if (height > MAX_HEIGHT_FOR_DISPLAYING) {
            removeTreeMarkers();
            return;
        }

        setCurrentBounds(north, east, south, west);
        showMapLoadingBar("Loading nearby trees...");


        mapService.getAllTreesInRegion(
                north + height / 4,
                east + width / 4,
                south - height / 4,
                west - width / 4
        ).thenAccept(trees -> {
            javafx.application.Platform.runLater(() -> {
                addTreeMarkersAfterTreeRequest(trees);
            });
        }).exceptionally(ex -> {
            if (!(ex instanceof java.util.concurrent.CancellationException)) {
                javafx.application.Platform.runLater(() -> {
                    LOG.error("Error fetching trees for map view: " + ex);
                    hideMapLoadingBar();
                });
            }
            return null;
        });
    }

    /**
     * Turn on the loading bar with a message
     * @param loadingMessage message to display to user
     */
    private void showMapLoadingBar(String loadingMessage) {
        mapLoadingLabel.setText(loadingMessage);
        mapLoadingBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        mapLoadingOverlay.setVisible(true);
        mapLoadingOverlay.setManaged(true);
    }

    /**
     * Hide loading bar
     */
    private void hideMapLoadingBar() {
        mapLoadingOverlay.setVisible(false);
        mapLoadingOverlay.setManaged(false);
    }

    /**
     * To run once trees have been returned
     * @param trees list of trees to make markers for
     */
    private void addTreeMarkersAfterTreeRequest(List<TreeModel> trees) {
        if (trees != null) {
            LOG.info(MessageFormat.format("Completed request for {0} trees.", trees.size()));
            removeTreeMarkers();
            for (TreeModel tree : trees) {
                addTreeMarker(tree);
            }
        }
        hideMapLoadingBar();

    }

    /**
     * Formatting the string popup for tree data points on Leaflet
     * Each tree added as a marker will have a popup description
     */
    private void addTreeMarker(TreeModel tree) {
        currentTrees.put(tree.getId(), tree);

        javaScriptConnector.call("addMarker",
                tree.getId(),
                new TreePopupBuilder.Builder(tree)
                        .message("Loading nutrition...")
                        .build(),
                tree.getFood(),
                tree.getYCoord(),
                tree.getXCoord()
        );
    }

    /**
     * Loads nutrition for a particular tree asynchronously,
     * using different HTML for different stages of the loading
     * @param id tree to be loaded
     */
    private void loadNutritionForTree(int id) {
        TreeModel tree = currentTrees.get(id);

        if (tree == null) {
            return;
        }

        Task<Food> task = new Task<>() {
            @Override
            protected Food call() {
                return foodService.getFood(tree.getFood());
            }
        };

        task.setOnSucceeded(event -> updatePopupWithFood(id, task, tree));
        task.setOnFailed(event -> updatePopupWithFailedFood(id, task, tree));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Given getFood succeeds, update the popup using the correct html
     * @param id marker being updated
     * @param task task that succeeded
     * @param tree tree the marker is for
     */
    private void updatePopupWithFood(int id, Task<Food> task, TreeModel tree) {
        Food food = task.getValue();

        String popupHtml;
        if (food == null) {
            popupHtml = new TreePopupBuilder.Builder(tree)
                    .message("Nutrition info unavailable.")
                    .build();
        } else {
            popupHtml = new TreePopupBuilder.Builder(tree)
                    .food(food)
                    .build();
        }

        javaScriptConnector.call(UPDATE_POPUP_MARKER, id, popupHtml);
    }

    /**
     * Nutrition didn't load, so update marker with corresponding message
     * @param id marker being updated
     * @param task task that failed
     * @param tree tree the marker is for
     */
    private void updatePopupWithFailedFood(int id, Task<Food> task, TreeModel tree) {
        LOG.error("Failed to load nutrition for tree {}", id, task.getException());

        String popupHtml = new TreePopupBuilder.Builder(tree)
                .message("Nutrition info unavailable.")
                .build();

        javaScriptConnector.call(UPDATE_POPUP_MARKER, id, popupHtml);
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
        for (FilterCategory filterCategory : FilterCategory.values()) {
            Button filterButton = FruitFilterUtilities.createFruitButton(filterCategory);
            if (filterManager.isSelected(filterCategory)) {
                FruitFilterUtilities.toggleButtonStyle(filterButton);
                mapService.addSelectedFruit(filterCategory);
            }
            filterButton.setOnAction(event -> filterButtonClicked(filterButton, filterCategory));
            filterBox.getChildren().add(filterButton);
        }
    }

    /**
     * Toggles the fruit button being clicked, and updates the
     * map to show only the relevant fruit trees
     * @param button
     * @param filterCategory
     */
    public void filterButtonClicked(Button button, FilterCategory filterCategory) {
        FruitFilterUtilities.toggleButtonStyle(button);
        filterManager.toggleFilters(filterCategory);

        if (filterManager.isSelected(filterCategory)) {
            mapService.addSelectedFruit(filterCategory);
        } else {
            mapService.removeSelectedFruit(filterCategory);
        }
        mapMoved(northBound, eastBound, southBound, westBound,
                stateManager.getMapLatitude(),
                stateManager.getMapLongitude(),
                stateManager.getMapZoom());
    }


}
