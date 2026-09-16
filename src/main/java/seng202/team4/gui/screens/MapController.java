package seng202.team4.gui.screens;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.gui.ScreenController;
import seng202.team4.gui.bridge.JavaScriptBridge;
import seng202.team4.models.Fruit;
import seng202.team4.services.MapService;
import seng202.team4.services.StateManager;
import seng202.team4.services.ThemeService;
import seng202.team4.services.treeData.TreeService;
import seng202.team4.models.TreeModel;

import java.util.List;

/**
 * Controller for the map_screen.fxml window which controls the map
 * @author Team 4
 */
public class MapController extends ScreenController {

    private static final Logger LOG = LogManager.getLogger(MapController.class);
    private final MapService mapService;

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

    @FXML
    private Button appleButton;
    @FXML
    private Button pearButton;
    @FXML
    private Button cherryButton;
    @FXML
    private Button peachButton;
    @FXML
    private Button apricotButton;

    private final ThemeService themeService;

    /**
     * Initialise the window
     *
     * @param stateManager Reference for window manager
     */
    public MapController(StateManager stateManager, ThemeService themeService) {
        super(stateManager);
        this.mapService = new MapService(new TreeService());
        this.themeService = themeService;
    }

    /**
     * Initializes the bridge and loads the map once FXML is loaded
     */
    @FXML
    private void initialize() {
        LOG.info("MapController initialize() called");
        javaScriptBridge = new JavaScriptBridge(
                tree -> { }, id -> false, this::mapMoved
        ); // TODO: replace with real handleTreeClicked + add-tree flow
        initMap();
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

        setCurrentBounds(north, east, south, west);

        List<TreeModel> trees =
                mapService.getAllTreesInRegion(
                        north + height / 2,
                        east + width / 2,
                        south - height / 2,
                        west - width / 2
                );

        removeTreeMarkers();
        for (TreeModel tree : trees) {
            addTreeMarker(tree);
        }
    }

    /**
     * Creates a marker for a given tree and then adds it the list of javascript markers to add to the map
     *
     * @param tree the tree to be added to the map
     */
    private void addTreeMarker(TreeModel tree) {
        String popup = String.format(
                "<b>Common Name: </b>%s<br>"
                        + "<b>Ownership: </b>%s<br>"
                        + "<b>Genus: </b>%s<br>"
                        + "<b>Tree ID:</b> %d",
                tree.getCommonName(),
                tree.getOwnership(),
                tree.getGenus(),
                tree.getId()
        );

        javaScriptConnector.call(
                "addMarker",
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
        javaScriptConnector.call("removeMarkers");
    }

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
    }

    /**
     * Toggles the apple filter button
     */
    @FXML
    public void appleButtonClicked() {
        filterButtonClicked(appleButton, Fruit.APPLE);
        mapMoved(northBound, eastBound, southBound, westBound);
    }

    /**
     * Toggles the pear filter button
     */
    @FXML
    public void pearButtonClicked() {
        filterButtonClicked(pearButton, Fruit.PEAR);
        mapMoved(northBound, eastBound, southBound, westBound);
    }

    /**
     * Toggles the cherry filter button
     */
    @FXML
    public void cherryButtonClicked() {
        filterButtonClicked(cherryButton, Fruit.CHERRY);
        mapMoved(northBound, eastBound, southBound, westBound);
    }

    /**
     * Toggles the peach filter button
     */
    @FXML
    public void peachButtonClicked() {
        filterButtonClicked(peachButton, Fruit.PEACH);
        mapMoved(northBound, eastBound, southBound, westBound);
    }

    /**
     * Toggles the apricot filter button
     */
    @FXML
    public void apricotButtonClicked() {
        filterButtonClicked(apricotButton, Fruit.APRICOT);
        mapMoved(northBound, eastBound, southBound, westBound);
    }

    /**
     * Toggles all the fruits to be included in the map by default
     */
    public void toggleAllFilterButtons() {
        filterButtonClicked(appleButton, Fruit.APPLE);
        filterButtonClicked(pearButton, Fruit.PEAR);
        filterButtonClicked(cherryButton, Fruit.CHERRY);
        filterButtonClicked(peachButton, Fruit.PEACH);
        filterButtonClicked(apricotButton, Fruit.APRICOT);
    }

}
