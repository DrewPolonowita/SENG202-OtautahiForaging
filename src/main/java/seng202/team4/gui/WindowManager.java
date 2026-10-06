package seng202.team4.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.gui.screens.AddNoteController;
import seng202.team4.gui.screens.ExpandNoteController;
import seng202.team4.gui.screens.JournalController;
import seng202.team4.gui.screens.MapController;
import seng202.team4.gui.screens.NavController;
import seng202.team4.gui.screens.RecipesController;
import seng202.team4.gui.screens.SettingsController;
import seng202.team4.gui.screens.TableViewController;
import seng202.team4.models.Note;
import seng202.team4.models.TreeModel;
import seng202.team4.services.StateManager;

import java.io.IOException;
import java.util.List;

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

    private NavController navController;

    /**
     * Launches the application and creates a base border pane for the screens to be built with
     * This method should eventually own its own FXML screen instead of constructing the element
     *
     * @param primaryStage The JavaFX stage
     */
    public WindowManager(Stage primaryStage) {
        this.stage = primaryStage;

        // Creates a GridPane and sets it as the primaryStage
        gridPane = new GridPane();
        // Creates the BorderPane for the navBar and the mainScreen
        rootPane = new BorderPane();
        navPane = new BorderPane();

        configureStage();
        configureLayout();
        configureStyles();

        themeService = new ThemeService(stage.getScene(), gridPane);
    }

    private void configureStage()
    {
        stage.setResizable(true);
        stage.setScene(new Scene(gridPane));
        // Scaling minimum sizes
        stage.setMinWidth(600);
        stage.setMinHeight(360);
        stage.setTitle("Ōtautahi Foraging");
        stage.setMaximized(true);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));

    }

    private void configureStyles() {
        Scene scene = stage.getScene();
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/icons.css").toExternalForm());
    }

    private void configureLayout() {
        // Places the navBar and the mainScreen into the primaryStage
        gridPane.add(navPane, 0, 0);
        gridPane.add(rootPane, 1, 0);
        // Setup Row and Column constraints to stretch content correctly
        ColumnConstraints navigationColumn = new ColumnConstraints();
        navigationColumn.setHgrow(Priority.NEVER);

        ColumnConstraints contentColumn = new ColumnConstraints();
        contentColumn.setHgrow(Priority.ALWAYS);

        gridPane.getColumnConstraints().addAll(navigationColumn, contentColumn);
    }

    public void show() {
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
        navController = new NavController(stateManager);
        launchScreen(navController, navPane);
    }

    /**
     * Launches the applications map at its default location.
     * @param stateManager the state manager for the application
     */
    public void launchMapScreen(StateManager stateManager) {
        launchMapScreen(stateManager, null);
    }

    /**
     * Launches the map centred on the target tree, or at the default location if null.
     *
     * @param stateManager the state manager for the application
     * @param targetTree the tree from the note.
     */
    public void launchMapScreen(StateManager stateManager, TreeModel targetTree) {
        ScreenController controller = new MapController(stateManager, themeService, targetTree);
        launchScreen(controller, rootPane);
        if (navController != null) {
            navController.selectMapButton();
        }
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
    public void launchRecipesScreen(StateManager stateManager, FilterManager filterManager) {
        ScreenController controller = new RecipesController(stateManager, filterManager);
        launchScreen(controller, rootPane);
    }

    /**
     * Launches the recipe screen with initial foods loaded
     *
     * @param stateManager the state manager for the application
     * @param selectedFoods foods to be loaded on launch
     */
    public void launchRecipesScreen(StateManager stateManager, FilterManager filterManager, List<String> selectedFoods) {
        ScreenController controller = new RecipesController(stateManager, filterManager, selectedFoods);
        launchScreen(controller, rootPane);

        navController.setRecipeButton();
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
            //stage.setTitle(controller.getTitle());
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    /**
     * Helper function for applying style to any new scene
     * @param root the root node of the scene to be styled
     * @return the newly created, styled scene
     */
    private Scene buildStyledScene(Parent root) {
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/icons.css").toExternalForm());
        themeService.applyTo(scene);
        return scene;
    }

    /**
     * Creates a new centered stage for different menus to reuse.
     * @param root the root node of the dialog's content.
     */
    private void showCenteredDialog(Parent root) {
        Stage dialogStage = new Stage();
        dialogStage.initOwner(stage);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.setScene(buildStyledScene(root));
        dialogStage.setOnShown(event -> {
            double centerX = stage.getX() + stage.getWidth() / 2;
            double centerY = stage.getY() + stage.getHeight() / 2;
            dialogStage.setX(centerX - dialogStage.getWidth() / 2);
            dialogStage.setY(centerY - dialogStage.getHeight() / 2);
        });
        dialogStage.showAndWait();
    }

    /**
     * Launches the add note menu centered on application. Blocking input from other applications while open.
     * @param tree the select tree to be converted to note
     * @param stateManager the provided state manager for the application
     */
    public void openAddNoteMenu(TreeModel tree, StateManager stateManager, Note existingNote, JournalController journalController) {
        try {
            showCenteredDialog(loadScreen(new AddNoteController(stateManager, tree, existingNote, journalController)));
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    /**
     * Launches the expanded version of the note user selects.
     * @param note the note of the tree user clicks
     * @param stateManager the provided state manager for the application
     */
    public void openExpandNote(Note note, StateManager stateManager, JournalController journalController) {
        try {
            showCenteredDialog(loadScreen(new ExpandNoteController(stateManager, note, journalController)));
        } catch (IOException e) {
            LOG.error(e);
        }
    }
}
