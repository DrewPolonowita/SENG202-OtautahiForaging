package seng202.team4.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.services.StateManager;

/**
 * An abstract parent class for {@link StateManager}  UI Controller classes
 * @author seng201 teaching team
 */
public abstract class ScreenController {

    protected static final Logger LOG = LogManager.getLogger(ScreenController.class);

    private final StateManager stateManager;

    /**
     * Creates an instance of a ScreenController with the given {@link StateManager}
     * @param stateManager The game manager for this screen
     */
    protected ScreenController(final StateManager stateManager) {
        this.stateManager = stateManager;
    }

    /**
     * Gets the FXML file of this screen
     * @return the fxml file of this screen
     */
    protected abstract String getFxmlFile();

    /**
     * Gets the title of the screen
     * @return the title of the screen
     */
    protected abstract String getTitle();

    /**
     * Gets the state manager for this controller
     * @return the state manager for this controller
     */
    protected StateManager getStateManager() {
        return stateManager;
    }
}
