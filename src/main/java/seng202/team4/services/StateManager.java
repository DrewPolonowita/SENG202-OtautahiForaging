package seng202.team4.services;

import seng202.team4.gui.WindowManager;

/**
 * The class which stores information related to the state of the entire app in a given instance
 * This class also has the ability to launch screens using the {@link WindowManager}
 * @author Team 4
 */
public class StateManager {

    private final WindowManager windowManager;

    /**
     * A constructor which launches the mainScreen of the application
     *
     * @param windowManager the window manager for this application
     */
    public StateManager(WindowManager windowManager) {
        this.windowManager = windowManager;
        this.windowManager.launchMainScreen(this);

    }

    public WindowManager getWindowManager() {
        return windowManager;
    }
}
