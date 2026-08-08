package seng202.team4.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.services.StateManager;

import java.io.IOException;

/**
 * Handles requests from GUI controllers to change scenes
 * @author Team 4
 */
public class WindowManager {

    private static final Logger LOG = LogManager.getLogger(WindowManager.class);

    private final Stage stage;
    private final BorderPane rootPane;
    private final BorderPane navPane;

    /**
     * Launches the application and creates a base border pane for the screens to be built with
     * This method should eventually own its own FXML screen instead of constructing the element
     *
     * @param primaryStage The JavaFX stage
     */
    public WindowManager(Stage primaryStage) {
        this.stage = primaryStage;
        stage.setResizable(true);

        // Creates a GridPane and sets it as the primaryStage
        GridPane gridPane = new GridPane();
        stage.setScene(new Scene(gridPane));

        // Initial sizes for the GUI
        gridPane.setPrefHeight(400);
        gridPane.setPrefWidth(600);

        // Creates the BorderPane for the navBar and the mainScreen
        rootPane = new BorderPane();
        navPane = new BorderPane();

        // Places the navBar and the mainScreen into the primaryStage
        gridPane.add(navPane, 0, 0);
        gridPane.add(rootPane, 1, 0);

        stage.setMaximized(true);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));

        stage.show();
    }

    /**
     * Launches the nav screen
     *
     * @param stateManager the state manager for the application
     */
    public void launchNavScreen(StateManager stateManager) {
        ScreenController controller = new NavController(stateManager);
        launchScreen(controller, navPane);
    }

    /**
     * Launches the main screen
     *
     * @param stateManager the state manager for the application
     */
    public void launchMapScreen(StateManager stateManager) {
        ScreenController controller = new MapController(stateManager);
        launchScreen(controller, rootPane);
    }

    /**
     * Launches Table View
     */
    public void launchTableView(StateManager stateManager) {
        ScreenController controller = new TableViewController(stateManager);
        launchScreen(controller, rootPane);
    }

    /**
     * Launches a screen using a given screen controller. This is done by replacing the
     * root border pane with the screen defined by {@link ScreenController}
     *
     * @param controller the screen being launched
     * @param pane the {@link BorderPane} being displayed onto
     */
    private void launchScreen(ScreenController controller, BorderPane pane) {
        try {
            FXMLLoader setupLoader = new FXMLLoader(getClass().getResource(controller.getFxmlFile()));
            setupLoader.setControllerFactory(param -> controller);
            Parent setupParent = setupLoader.load();

            pane.setCenter(setupParent);
            stage.setTitle(controller.getTitle());
        } catch (IOException e) {
            LOG.error(e);
        }
    }
}
