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
import seng202.team4.gui.screens.JournalController;
import seng202.team4.gui.screens.MapController;
import seng202.team4.gui.screens.RecipesController;
import seng202.team4.gui.screens.SettingsController;
import seng202.team4.gui.screens.TableViewController;
import seng202.team4.services.StateManager;
import seng202.team4.services.ThemeService;

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
    private final GridPane gridPane;
    private final ThemeService themeService;

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
        gridPane = new GridPane();
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

        themeService = new ThemeService(stage.getScene(), gridPane);
        stage.setMaximized(true);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
        gridPane.getScene().getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        gridPane.getScene().getStylesheets().add(getClass().getResource("/css/icons.css").toExternalForm());
        stage.show();
    }

    /**
     * Gets the ThemeService used to control dark mode and primary color.
     *
     * @return the ThemeService instance for this window
     */
    public ThemeService getThemeService() {
        return themeService;
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
     * Launches the journals screen
     *
     * @param stateManager the state manager for the application
     */
    public void launchJournalScreen(StateManager stateManager) {
        ScreenController controller = new JournalController(stateManager);
        launchScreen(controller, rootPane);
    }

    /**
     * Launches the recipes screen
     *
     * @param stateManager the state manager for the application
     */
    public void launchRecipesScreen(StateManager stateManager) {
        ScreenController controller = new RecipesController(stateManager);
        launchScreen(controller, rootPane);
    }

    /**
     * Launches the table screen
     */
    public void launchTableScreen(StateManager stateManager) {
        ScreenController controller = new TableViewController(stateManager);
        launchScreen(controller, rootPane);
    }

    /**
     * Launches the table screen
     */
    public void launchSettingsScreen(StateManager stateManager) {
        ScreenController controller = new SettingsController(stateManager);
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
