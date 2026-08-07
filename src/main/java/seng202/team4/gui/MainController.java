package seng202.team4.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.services.StateManager;

/**
 * Controller for the main.fxml window
 * @author Team 4
 */
public class MainController extends ScreenController {

    private static final Logger log = LogManager.getLogger(MainController.class);

    @FXML
    private Label titleLabel;
    @FXML private Button closeButton;
    @FXML private VBox sideBar;

    /**
     * Initialise the window
     *
     * @param stateManager Reference for window manager
     */
    public MainController(StateManager stateManager) {

        super(stateManager);
    }


    public String getFxmlFile() {
        return "/fxml/main.fxml";
    }

    public String getTitle() {
        return "Main Window";
    }

    /**
     * Method to call when our counter button is clicked
     *
     */
    @FXML
    public void onButtonClicked() {

        StateManager stateManager = getStateManager();
        stateManager.getWindowManager().launchTableView(stateManager);

    }

    /**
     * Method to call to close sidebar
     *
     */
    @FXML
    public void closeClicked() {
        sideBar.setVisible(false);
    }
}
