package seng202.team4.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import seng202.team4.services.CounterService;
import seng202.team4.services.StateManager;

/**
 * Controller for the main.fxml window
 * @author seng202 teaching team
 */
public class MainScreenController extends ScreenController {

    // FXML variables
    @FXML private Label defaultLabel;
    @FXML private Button defaultButton;

    // Services
    private final CounterService counterService;

    /**
     * Creates an instance of a MainScreenController with the given {@link StateManager}
     * @param stateManager The game manager for this screen
     */
    public MainScreenController(StateManager stateManager) {
        super(stateManager);
        this.counterService = new CounterService();
    }

    public String getFxmlFile() {
        return "/fxml/main.fxml";
    }

    public String getTitle() {
        return "Main Window";
    }

    /**
     * Method to call when our counter button is clicked
     */
    @FXML
    public void onButtonClicked() {
        LOG.info("Button has been clicked");
        counterService.incrementCounter();

        int count = counterService.getCurrentCount();
        defaultLabel.setText(Integer.toString(count));

    }
}
