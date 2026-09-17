package seng202.team4.gui.screens;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.gui.ScreenController;
import seng202.team4.gui.utilities.FatSecretAttributionUtilities;
import seng202.team4.models.Recipe;
import seng202.team4.services.StateManager;

public class RecipeHighlightController extends ScreenController {

    private static final Logger LOG = LogManager.getLogger(RecipeHighlightController.class);
    private final Recipe recipe;
    @FXML
    private Text recipeNameText;
    @FXML
    private StackPane attributionPane;

    public RecipeHighlightController(StateManager stateManager, Recipe recipe) {
        super(stateManager);
        this.recipe = recipe;
    }

    public String getFxmlFile() {
        return "/fxml/recipe_screen_highlight.fxml";
    }

    public String getTitle() {
        return "RecipeHighlight";
    }

    @FXML
    public void initialize() {
        recipeNameText.setText(displayText(recipe));
        ImageView attribution = FatSecretAttributionUtilities.createStandardAttributionImage();
        attributionPane.getChildren().add(attribution);
    }

    private String displayText(Recipe recipe) {
        StringBuilder text = new StringBuilder();
        text.append(recipe.getName()).append("\n\n");

        if (recipe.getDescription() != null) {
            text.append(recipe.getDescription()).append("\n\n");
        }

        text.append("Prep time: ").append(recipe.getPreparationTime()).append(" min\n");
        text.append("Cook time: ").append(recipe.getCookingTime()).append(" min\n\n");

        text.append("Calories: ").append(recipe.getCalories()).append("\n");
        text.append("Carbohydrates: ").append(recipe.getCarbohydrates()).append("g\n");
        text.append("Protein: ").append(recipe.getProtein()).append("g\n");
        text.append("Fat: ").append(recipe.getFat()).append("g\n\n");

        text.append("Ingredients:\n");
        if (recipe.getIngredients() != null) {
            for (String ingredient : recipe.getIngredients()) {
                text.append("- ").append(ingredient).append("\n");
            }
        }
        text.append("\n");

        text.append("Method:\n").append(recipe.getMethod());

        return text.toString();
    }

    @FXML
    public void closeClicked() {
        StateManager stateManager = getStateManager();
        stateManager.getWindowManager().launchRecipesScreen(stateManager);
    }
}
