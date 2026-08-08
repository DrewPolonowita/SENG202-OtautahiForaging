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

    @FXML private Button mapButton;
    @FXML private Button journalsButton;
    @FXML private Button recipesButton;
    @FXML private Button tableButton;
    @FXML private Button settingsButton;

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
    private Button selectedButton;
    @FXML
    public void initialize() {
        setSelectedButton(mapButton);
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
        setSelectedButton(mapButton);
    }

    /**
     * Method to call when our counter button is clicked
     *
     */
    @FXML
    public void onJournalsButtonClicked() {
        StateManager stateManager = getStateManager();
        stateManager.getWindowManager().launchJournalScreen(stateManager);
        setSelectedButton(journalsButton);

    }

    /**
     * Method to call when our counter button is clicked
     *
     */
    @FXML
    public void onRecipesButtonClicked() {
        StateManager stateManager = getStateManager();
        stateManager.getWindowManager().launchRecipesScreen(stateManager);
        setSelectedButton(recipesButton);

    }

    /**
     * Method to call when our counter button is clicked
     *
     */
    @FXML
    public void onTableButtonClicked() {
        StateManager stateManager = getStateManager();
        stateManager.getWindowManager().launchTableScreen(stateManager);
        setSelectedButton(tableButton);

    }

    /**
     * Method to call when our counter button is clicked
     *
     */
    @FXML
    public void onSettingsButtonClicked() {
        StateManager stateManager = getStateManager();
        stateManager.getWindowManager().launchSettingsScreen(stateManager);
        setSelectedButton(settingsButton);

    }

    /**
     * Updates sidebar styling to reflect which tab is currently active.
     */
    private void setSelectedButton(Button newSelection) {
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("sideBar-button-selected");
            selectedButton.getGraphic().getStyleClass().remove("icon-selected");
        }
        newSelection.getStyleClass().add("sideBar-button-selected");
        newSelection.getGraphic().getStyleClass().add("icon-selected");
        selectedButton = newSelection;
    }
}
