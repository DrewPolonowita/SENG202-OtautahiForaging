package seng202.team4.gui;

import seng202.team4.services.StateManager;

/**
 * An abstract parent class for {@link StateManager}  UI Controller classes
 * @author seng201 teaching team
 */
public abstract class ScreenController {


    private final StateManager stateManager;

    /**
     * Creates an instance of a ScreenController with the given {@link StateManager}
     * @param stateManager The state manager for this screen
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
