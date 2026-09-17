package seng202.team4.gui.screens;

import com.sun.javafx.webkit.WebConsoleListener;
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
import seng202.team4.models.Fruit;
import seng202.team4.models.TreeModel;
import seng202.team4.services.MapService;
import seng202.team4.services.StateManager;
import seng202.team4.services.ThemeService;
import seng202.team4.services.dataCentre.treeData.TreeDataManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the map_screen.fxml window which controls the map
 * @author Team 4
 */
public class MapController extends ScreenController {

    private static final Logger LOG = LogManager.getLogger(MapController.class);
    private final MapService mapService;

    private final TreeDataManager treeService;
    private static final double MAX_HEIGHT_FOR_DISPLAYING = 0.05;

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

    /**
     * Initialise the window
     *
     * @param stateManager Reference for window manager
     */
    public MapController(StateManager stateManager, ThemeService themeService) {
        super(stateManager);
        treeService = stateManager.getTreeService();
        this.mapService = new MapService(treeService);
        this.themeService = themeService;
        this.stateManager = stateManager;
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
                        stateManager.getWindowManager().openAddNoteMenu(tree, stateManager);
                    } else {
                        LOG.warn("Add to Journal clicked for unknown tree id {}", id);
                    }
                },
                tree -> { },
                id -> false, // TODO: replace with real handleTreeClicked + add-tree flow
                this::mapMoved
        );
        initMap();
        createFruitButtons();
        toggleAllFilterButtons();
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
                        javaScriptConnector.call("initMap");
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
            filterButton.getGraphic().getStyleClass().remove("icon-selected");
        } else {
            filterBox.setVisible(true);
            filterOpened = true;
            filterButton.getStyleClass().add("filters-selected");
            filterButton.getGraphic().getStyleClass().add("icon-selected");
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

        mapService.getAllTreesInRegion(
                        north + height / 2,
                        east + width / 2,
                        south - height / 2,
                        west - width / 2
                )

                .thenAccept(trees -> {
                    javafx.application.Platform.runLater(() -> {
                        removeTreeMarkers();
                        for (TreeModel tree : trees) {
                            addTreeMarker(tree);
                        }
                    });
                })
                .exceptionally(ex -> {
                    javafx.application.Platform.runLater(() -> {
                        System.err.println("Error fetching trees for map view: " + ex.getMessage());
                    });
                    return null;
                });
    }


    /**
     * Formatting the string popup for tree data points on Leaflet
     * Each tree added as a marker will have a popup description
     */
    private void addTreeMarker(TreeModel tree) {
        currentTrees.put(tree.getId(), tree);
        String popup = String.format(
                "<div class=\"tree-card\">"
                        + "<div class=\"tree-card-image\"></div>"
                        + "<div class=\"tree-card-body\">"
                        + "<b>%s</b><br>"
                            + "<br><button class=\"journal-btn\" onclick=\"javaScriptBridge.addTreeToJournal(%d)\">Add to Journal</button>"
                            + "</div></div>",
                tree.getCommonName(),
                tree.getId()
        );

        javaScriptConnector.call("addMarker",
                tree.getId(),
                popup,
                tree.getYCoord(),
                tree.getXCoord()
        );
    }

    /**
     * Removes all the tree markers on the screen
     */
    private void removeTreeMarkers() {
        currentTrees.clear();
        javaScriptConnector.call("removeMarkers");
    }

    /**
     *For each fruit name in the enum FRUIT, a filter button is creaated
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
        if (button.getStyleClass().contains("sideBar-button-selected")) {
            button.getStyleClass().remove("sideBar-button-selected");
            button.getGraphic().getStyleClass().remove("icon-selected");
        } else {
            button.getStyleClass().add("sideBar-button-selected");
            button.getGraphic().getStyleClass().add("icon-selected");
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
        if (buttons.getStyleClass().contains("sideBar-button-selected")) {
            buttons.getStyleClass().remove("sideBar-button-selected");
            buttons.getGraphic().getStyleClass().remove("icon-selected");

            mapService.removeSelectedFruit(fruit);
        } else {
            buttons.getStyleClass().add("sideBar-button-selected");
            buttons.getGraphic().getStyleClass().add("icon-selected");

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
