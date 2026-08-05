package seng202.team4.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.services.StateManager;

import java.io.IOException;

/**
 * Handles requests from GUI controllers to change scenes
 */
public class WindowManager {

    private static final Logger LOG = LogManager.getLogger(WindowManager.class);

    private final Stage stage;

    private final BorderPane rootPane;

    /**
     * Launches the application and creates a base border pane for the screens to be built with
     * This method should eventually own its own FXML screen instead of constructing the element
     *
     * @param primaryStage The JavaFX stage
     */
    public WindowManager(Stage primaryStage) {
        this.stage = primaryStage;
        stage.setResizable(true);

        // Use a border pane as the root component to allow children to be resizable.
        rootPane = new BorderPane();
        rootPane.setPrefHeight(400);
        rootPane.setPrefWidth(600);
        stage.setScene(new Scene(rootPane));
        stage.setMaximized(true);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
        stage.show();
    }

    /**
     * Launches the main screen
     *
     * @param stateManager the state manager for the application
     */
    public void launchMainScreen(StateManager stateManager) {
        ScreenController controller = new MainController(stateManager);
        launchScreen(controller);
    }

    /**
     * Launches Table View
     */
    public void launchTableView(StateManager stateManager) {
        ScreenController controller = new TableViewController(stateManager);
        launchScreen(controller);
    }

    /**
     * Launches a screen using a given screen controller. This is done by replacing the
     * root border pane with the screen defined by {@link ScreenController}
     *
     * @param controller the screen being launched
     */
    private void launchScreen(ScreenController controller) {
        try {
            FXMLLoader setupLoader = new FXMLLoader(getClass().getResource(controller.getFxmlFile()));

            // Set a controller factory that returns the given ScreenController.
            // This allows us to have screen controllers that take argument(s) in their constructor.
            setupLoader.setControllerFactory(param -> controller);
            Parent setupParent = setupLoader.load();

            rootPane.setCenter(setupParent);
            stage.setTitle(controller.getTitle());
        } catch (IOException e) {
            LOG.error(e);
        }
    }
}
