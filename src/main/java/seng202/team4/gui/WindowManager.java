package seng202.team4.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.gui.screens.*;
import seng202.team4.models.TreeModel;
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

        // Scaling minimum sizes
        stage.setMinWidth(600);
        stage.setMinHeight(360);

        // Creates the BorderPane for the navBar and the mainScreen
        rootPane = new BorderPane();
        navPane = new BorderPane();

        // Places the navBar and the mainScreen into the primaryStage
        gridPane.add(navPane, 0, 0);
        gridPane.add(rootPane, 1, 0);

        themeService = new ThemeService(stage.getScene(), gridPane);
        // Setup Row and Column constraints to stretch content correctly
        ColumnConstraints navigationColumn = new ColumnConstraints();
        navigationColumn.setHgrow(Priority.NEVER);
        ColumnConstraints contentColumn = new ColumnConstraints();
        contentColumn.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(navigationColumn, contentColumn);


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
     * Loads the FXML file for given screen controller.
     * @param controller the pre-built controller to inject FXML file
     * @return root node of loaded FXML
     * @throws IOException if FXML file cant be loaded.
     */
    private Parent loadScreen(ScreenController controller) throws IOException {
        FXMLLoader setupLoader = new FXMLLoader(getClass().getResource(controller.getFxmlFile()));
        setupLoader.setControllerFactory(param -> controller);
        return setupLoader.load();
    }

    /**
     * Launches screen using given screen controller.
     * @param controller the screen being launched
     * @param pane the {@link BorderPane} being displayed into
     */
    private void launchScreen(ScreenController controller, BorderPane pane) {
        try {
            Parent setupParent = loadScreen(controller);
            pane.setCenter(setupParent);
            stage.setTitle(controller.getTitle());
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    /**
     * Launches the add note menu. Blocking input from other applications while open.
     * @param tree the select tree to be converted to note
     * @param stateManager the provided state manager for the application
     */
    public void openAddNoteMenu(TreeModel tree, StateManager stateManager) {
        ScreenController controller = new AddNoteController(stateManager, tree);
        try {
            Parent menuRoot = loadScreen(controller);
            Stage menuStage = new Stage();
            menuStage.initModality(Modality.APPLICATION_MODAL); // blocks input to background screen
            menuStage.initStyle(StageStyle.TRANSPARENT);
            menuStage.setScene(new Scene(menuRoot));
            menuStage.showAndWait();
        } catch (IOException e) {
            LOG.error(e);
        }

    }
}
