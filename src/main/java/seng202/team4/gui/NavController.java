package seng202.team4.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.services.StateManager;

/**
 * Controller for the nav_bar.fxml window which controls the navigation
 * @author Team 4
 */
public class NavController extends ScreenController {

    private static final Logger LOG = LogManager.getLogger(NavController.class);

    @FXML private Label titleLabel;
    @FXML private Button closeButton;

    @FXML private BorderPane navBar;
    @FXML private VBox sideBar;
    @FXML private Button menuOpenButton;
    @FXML private StackPane sideBarPane;

    /**
     * Initialise the window
     *
     * @param stateManager Reference for window manager
     */
    public NavController(StateManager stateManager) {

        super(stateManager);
    }

    public String getFxmlFile() {
        return "/fxml/nav_bar.fxml";
    }

    public String getTitle() {
        return "Map";
    }

    /**
     * Method to call to close sidebar
     *
     */
    @FXML
    public void closeClicked() {
        sideBar.setVisible(false);
        sideBar.setManaged(false);

        menuOpenButton.setVisible(true);
        menuOpenButton.setManaged(true);

        setWidth(50);
    }

    /**
     * Method to call to open sidebar
     *
     */
    @FXML
    public void openMenuClicked() {
        sideBar.setVisible(true);
        sideBar.setManaged(true);

        menuOpenButton.setVisible(false);
        menuOpenButton.setManaged(false);

        setWidth(300);
    }

    /**
     * Sets the width of the navBar
     *
     * @param width the width of the navBar
     */
    private void setWidth(int width) {
        sideBarPane.setMinWidth(width);
        sideBarPane.setPrefWidth(width);
        sideBarPane.setMaxWidth(width);

        navBar.setMinWidth(width);
        navBar.setPrefWidth(width);
        navBar.setMaxWidth(width);
    }

    /**
     * Method to call when our counter button is clicked
     *
     */
    @FXML
    public void onMapButtonClicked() {
        StateManager stateManager = getStateManager();
        stateManager.getWindowManager().launchMapScreen(stateManager);

    }

    /**
     * Method to call when our counter button is clicked
     *
     */
    @FXML
    public void onJournalsButtonClicked() {
        StateManager stateManager = getStateManager();
        stateManager.getWindowManager().launchJournalScreen(stateManager);

    }

    /**
     * Method to call when our counter button is clicked
     *
     */
    @FXML
    public void onRecipesButtonClicked() {
        StateManager stateManager = getStateManager();
        stateManager.getWindowManager().launchRecipesScreen(stateManager);

    }

    /**
     * Method to call when our counter button is clicked
     *
     */
    @FXML
    public void onTableButtonClicked() {
        StateManager stateManager = getStateManager();
        stateManager.getWindowManager().launchTableScreen(stateManager);

    }

    /**
     * Method to call when our counter button is clicked
     *
     */
    @FXML
    public void onSettingsButtonClicked() {
        StateManager stateManager = getStateManager();
        stateManager.getWindowManager().launchSettingsScreen(stateManager);

    }
}
