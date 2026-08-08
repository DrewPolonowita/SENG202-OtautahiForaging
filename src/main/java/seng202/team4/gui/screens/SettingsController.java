package seng202.team4.gui.screens;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.gui.ScreenController;
import seng202.team4.services.StateManager;

/**
 * Controller for the map_screen.fxml window which controls the map
 * @author Team 4
 */
public class SettingsController extends ScreenController {

    private static final Logger LOG = LogManager.getLogger(SettingsController.class);

    /**
     * Initialise the window
     *
     * @param stateManager Reference for window manager
     */
    public SettingsController(StateManager stateManager) {

        super(stateManager);
    }

    public String getFxmlFile() {
        return "/fxml/settings_screen.fxml";
    }

    public String getTitle() {
        return "Map";
    }
}
