package seng202.team4.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
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
    private Label defaultLabel;

    @FXML
    private Button defaultButton;

    private CounterService counterService;

    private final WindowManager windowManager;

    /**
     * Initialise the window
     *
     * @param windowManager Reference for window manager
     */
    public MainController(WindowManager windowManager) {
        this.windowManager = windowManager;
        this.counterService = new CounterService();

    }



    /**
     * Method to call when our counter button is clicked
     *
     */
    @FXML
    public void onButtonClicked() {
        log.info("Button has been clicked");
        counterService.incrementCounter();

        int count = counterService.getCurrentCount();
        defaultLabel.setText(Integer.toString(count));
        windowManager.launchTableView();
        windowManager.closeMainScreen();

    }
}
