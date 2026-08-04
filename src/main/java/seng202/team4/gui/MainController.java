package seng202.team4.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.services.CounterService;

/**
 * Controller for the main.fxml window
 * @author seng202 teaching team
 */
public class MainController {

    private static final Logger log = LogManager.getLogger(MainController.class);

    @FXML
    private Label titleLabel;


    private final WindowManager windowManager;

    /**
     * Initialise the window
     *
     * @param windowManager Reference for window manager
     */
    public MainController(WindowManager windowManager) {
        this.windowManager = windowManager;
    }



    /**
     * Method to call when our counter button is clicked
     *
     */
    @FXML
    public void onButtonClicked() {
        windowManager.closeScreen();
        windowManager.launchTableView();


    }
}
