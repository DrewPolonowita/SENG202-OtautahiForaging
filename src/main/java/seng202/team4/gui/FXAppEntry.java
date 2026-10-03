package seng202.team4.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import seng202.team4.services.StateManager;

/**
 * Starts the JavaFX application thread
 */
public class FXAppEntry extends Application {

    /**
     * Creates an {@link StateManager} with a {@link WindowManager} for a given {@link Stage}
     *
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     */
    @Override
    public void start(Stage stage) {
        WindowManager windowManager = new WindowManager(stage);
        new StateManager(windowManager);
        windowManager.show();

    }
}
