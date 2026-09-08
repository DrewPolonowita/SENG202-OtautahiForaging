package seng202.team4.gui.screens;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.gui.ScreenController;
import seng202.team4.gui.utilities.JavaFXUtils;
import seng202.team4.services.StateManager;

/**
 * Controller for the recipe_screen_highlight.fxml window which controls the navigation
 * @author Team 4
 */
public class RecipeHighlightController extends ScreenController {

    private static final Logger LOG = LogManager.getLogger(seng202.team4.gui.screens.RecipeHighlightController.class);
    private String recipeName;
    @FXML
    private Text recipeNameText;


    /**
     * Initialise the window
     *
     * @param stateManager Reference for window manager
     */
    public RecipeHighlightController(StateManager stateManager, String recipeName) {
        super(stateManager);
        this.recipeName = recipeName;
    }

    public String getFxmlFile() {
        return "/fxml/recipe_screen_highlight.fxml";
    }

    public String getTitle() {
        return "RecipeHighlight";
    }

    /**
     * Method to be called automatically after FXML files loaded
     */
    @FXML
    public void initialize() {
        recipeNameText.setText(recipeName);
    }

    @FXML
    public void closeClicked() {
        StateManager stateManager = getStateManager();
        stateManager.getWindowManager().launchRecipesScreen(stateManager);
    }
}
