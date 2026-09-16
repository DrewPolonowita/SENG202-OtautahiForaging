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
import seng202.team4.services.StateManager;
import seng202.team4.services.treeData.TreeDataManager;
import seng202.team4.models.TreeModel;
/**
 * Controller for the map_screen.fxml window which controls the map
 * @author Team 4
 */
public class MapController extends ScreenController {

    private static final Logger LOG = LogManager.getLogger(MapController.class);

    private final TreeDataManager treeService; // TODO: used once tree-click lookup is implemented

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

    //private final ThemeService themeService;
    /**
     * Initialise the window
     *
     * @param stateManager Reference for window manager
     */
    public MapController(StateManager stateManager) {

        super(stateManager);
        treeService = stateManager.getTreeService();
        //this.themeService = themeService; , ThemeService themeService
    }

    /**
     * Initialises the bridge and loads the map once FXML is loaded
     */
    @FXML
    private void initialize() {
        LOG.info("MapController initialize() called");
        javaScriptBridge = new JavaScriptBridge(tree -> { }, id -> false); // TODO: replace with real handleTreeClicked + add-tree flow
        initMap();

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
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaScriptBridge", javaScriptBridge);
                        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");

                        javaScriptConnector.call("initMap");
                        showTreeDescription();
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

    /**
     * Formatting the string popup for tree data points on Leaflet
     * Each tree added as a marker will have a popup description
     * Potential space for development: addition of "add to Journal" button
     * on the popup.
     */
    private void showTreeDescription() {
        for (TreeModel tree : treeService.getAllTrees()) {
            String popup = String.format(
                    "<b>Common Name: </b>%s<br>"
                            + "<b>Ownership: </b>%s<br>"
                            + "<b>Genus: </b>%s<br>"
                            + "<b>Tree ID:</b> %d", tree.getCommonName(),
                    tree.getOwnership(), tree.getGenus(), tree.getId()
            );
            javaScriptConnector.call("addMarker", tree.getId(), popup,
                    tree.getYCoord(), tree.getXCoord());
        }
    }
    public void filterButtonClicked(Button buttons) {
        if (buttons.getStyleClass().contains("sideBar-button-selected")) {
            buttons.getStyleClass().remove("sideBar-button-selected");
            buttons.getGraphic().getStyleClass().remove("icon-selected");
        } else {
            buttons.getStyleClass().add("sideBar-button-selected");
            buttons.getGraphic().getStyleClass().add("icon-selected");
        }
    }
    @FXML
    public void appleButtonClicked() {
        filterButtonClicked(appleButton);
    }
    @FXML
    public void pearButtonClicked() {
        filterButtonClicked(pearButton);
    }
    @FXML
    public void cherryButtonClicked() {
        filterButtonClicked(cherryButton);
    }
    @FXML
    public void peachButtonClicked() {
        filterButtonClicked(peachButton);
    }
    @FXML
    public void apricotButtonClicked() {
        filterButtonClicked(apricotButton);
    }

}
