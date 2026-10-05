package seng202.team4.gui.screens;

import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import seng202.team4.gui.ScreenController;
import seng202.team4.gui.utilities.FatSecretAttributionUtilities;
import seng202.team4.gui.utilities.FruitFilterUtilities;
import seng202.team4.gui.utilities.RecipeFilterService;
import seng202.team4.models.FilterCategory;
import seng202.team4.models.Recipe;
import seng202.team4.services.StateManager;
import seng202.team4.services.dataCentre.recipeData.RecipeDataManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controller for the map_screen.fxml window which controls the map
 * @author Team 4
 */
public class RecipesController extends ScreenController {
    @FXML
    private TilePane recipeTilePane;
    private RecipeDataManager recipeService;
    @FXML
    private StackPane attributionPane;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private BorderPane recipeListPane;

    @FXML
    private BorderPane recipeHighlightPane;

    @FXML
    private Text recipeDetailsText;
    @FXML
    private VBox filterList;
    private final StateManager stateManager;
    private final List<String> startingSelectedFoods;

    private RecipeFilterService recipeFilterService;

    private static final Logger LOG = LogManager.getLogger(RecipesController.class);

    private final Set<Integer> displayedRecipeIds = new HashSet<>();
    private static final int CARD_WIDTH = 300;
    private static final int CARD_HEIGHT = 260;
    private static final int TEXT_HEIGHT = 60;
    private static final int IMAGE_HEIGHT = CARD_HEIGHT - TEXT_HEIGHT;

    /**
     * Initialise the window
     *
     * @param stateManager Reference for window manager
     */
    public RecipesController(StateManager stateManager) {
        super(stateManager);
        this.stateManager = stateManager;
        this.startingSelectedFoods = List.of();
    }

    /**
     * Constructor for going to recipes from map
     * @param stateManager Reference for window manager
     * @param startingSelectedFoods List of foods selected, realistically will be just one
     */
    public RecipesController(StateManager stateManager, List<String> startingSelectedFoods) {
        super(stateManager);
        this.stateManager = stateManager;
        this.startingSelectedFoods = startingSelectedFoods;
    }

    public String getFxmlFile() {
        return "/fxml/recipe_screen.fxml";
    }

    public String getTitle() {
        return "Map";
    }

    /**
     * Initialises the fxml gui
     *
     */
    @FXML
    public void initialize() {
        recipeService = getStateManager().getRecipeService();
        ImageView attribution = FatSecretAttributionUtilities.createStandardAttributionImage(stateManager.getWindowManager().getThemeService().isDarkMode());
        attributionPane.getChildren().add(attribution);
        createFruitButtons();
        this.recipeFilterService = new RecipeFilterService(recipeService);

        setUpEvents();
        setStartingFilters();

    }

    /**
     * Sets up events for progress indicator and tasks
     */
    private void setUpEvents() {
        recipeFilterService.setOnSucceeded(event -> {
            progressIndicator.setVisible(false);
            displayRecipes(recipeFilterService.getValue());
        });

        recipeFilterService.setOnRunning(event -> progressIndicator.setVisible(true));

        recipeFilterService.setOnFailed(event -> {
            progressIndicator.setVisible(false);
            LOG.error("Failed to load recipes", recipeFilterService.getException());
        });

        recipeFilterService.setOnCancelled(event -> progressIndicator.setVisible(false));
    }

    /**
     * Toggles the visibility of the fruit filters.
     */
    @FXML
    private void onFilterClicked() {
        filterList.setVisible(!filterList.isVisible());
    }

    /**
     * Sets up a recipe card to display the recipes from the FatSecret API
     * Searches for recipes with ingredients that are toggled on and returns
     * relevant information on the cards.
     */
    private void createRecipeCard(Recipe recipe) {
        GridPane card = new GridPane();
        card.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        card.getStyleClass().add("recipeCardBase");

        StackPane imageArea = createImageArea();
        ImageView imageView = getImageView(recipe);

        Rectangle clip = new Rectangle(CARD_WIDTH, IMAGE_HEIGHT);
        imageArea.setClip(clip);
        imageArea.getChildren().add(imageView);

        Label title = createRecipeTitle(recipe);
        setRecipeCardRows(card);

        card.add(imageArea, 0, 0);
        card.add(title, 0, 1);
        card.setOnMouseClicked(event -> showRecipe(recipe));
        recipeTilePane.getChildren().add(card);
    }

    private void setRecipeCardRows(GridPane card) {
        RowConstraints textRow = new RowConstraints();
        textRow.setPrefHeight(TEXT_HEIGHT);
        textRow.setValignment(VPos.CENTER);
        card.getRowConstraints().addAll(new RowConstraints(IMAGE_HEIGHT), textRow);
    }

    private @NotNull Label createRecipeTitle(Recipe recipe) {
        Label title = new Label(recipe.getName());
        title.getStyleClass().add("recipeCardTitle");
        title.setWrapText(true);
        return title;
    }

    private @NotNull ImageView getImageView(Recipe recipe) {
        ImageView imageView = new ImageView();
        String imagePath = recipe.getImage();
        if (imagePath != null) {
            loadImageIntoView(imagePath, imageView);
        }
        return imageView;
    }

    private void loadImageIntoView(String imagePath, ImageView imageView) {
        if (imagePath.startsWith("https://")) {
            Image image = new Image(imagePath, true);
            image.progressProperty().addListener((obs, oldProgress, progress) -> {
                setRecipeImageScaled(progress, image, imageView);
            });
        } else {
            var resource = getClass().getResource(imagePath);
            if (resource != null) {
                Image image = new Image(resource.toExternalForm());
                if (!image.isError()) {
                    imageView.setImage(image);
                    double scale = Math.max(CARD_WIDTH / image.getWidth(), IMAGE_HEIGHT / image.getHeight());
                    imageView.setFitWidth(image.getWidth() * scale);
                    imageView.setFitHeight(image.getHeight() * scale);
                }
            }
        }
    }

    private static void setRecipeImageScaled(Number progress, Image image, ImageView imageView) {
        if (progress.doubleValue() >= 1.0 && !image.isError()) {
            imageView.setImage(image);
            double scale = Math.max(CARD_WIDTH / image.getWidth(), IMAGE_HEIGHT / image.getHeight());
            imageView.setFitWidth(image.getWidth() * scale);
            imageView.setFitHeight(image.getHeight() * scale);
        }
    }

    private StackPane createImageArea() {
        StackPane imageArea = new StackPane();
        imageArea.setPrefSize(CARD_WIDTH, IMAGE_HEIGHT);
        imageArea.setMinSize(CARD_WIDTH, IMAGE_HEIGHT);
        imageArea.setMaxSize(CARD_WIDTH, IMAGE_HEIGHT);
        return imageArea;
    }

    /**
     * Full screens the recipe the user clicked
     * @param recipe recipe to show full view
     */
    private void showRecipe(Recipe recipe) {
        recipeDetailsText.setText(displayText(recipe));
        recipeListPane.setVisible(false);
        recipeListPane.setManaged(false);
        recipeHighlightPane.setVisible(true);
        recipeHighlightPane.setManaged(true);
    }

    /**
     * Closes fullscreen recipe back to recipes screen
     */
    @FXML
    private void closeRecipe() {
        recipeHighlightPane.setVisible(false);
        recipeHighlightPane.setManaged(false);
        recipeListPane.setVisible(true);
        recipeListPane.setManaged(true);
    }

    /**
     * Displays the text for the recipes
     * @param recipe recipe that is being displayed
     */
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

    /**
     *For each fruit name in the enum FRUIT, a filter button is creaated
     */
    private void createFruitButtons() {
        filterList.getChildren().clear();

        for (FilterCategory filterCategory : FilterCategory.values()) {
            Button filterButton = FruitFilterUtilities.createFruitButton(filterCategory);
            filterButton.setOnAction(event -> filterButtonClicked(filterButton));
            filterList.getChildren().add(filterButton);
        }
    }


    /**
     * Toggles the fruit button being clicked, and updates the
     * map to show only the relevant fruit trees
     * @param buttons filter buttons
     */
    public void filterButtonClicked(Button buttons) {
        FruitFilterUtilities.toggleButtonStyle(buttons);

        refreshRecipesForSelectedFilters();
    }

    /**
     * Refresh recipe pane base on the filters, without having duplicate tiles
     */
    private void refreshRecipesForSelectedFilters() {
        List<String> selectedFoods = getSelectedFoods();

        recipeTilePane.getChildren().clear();
        displayedRecipeIds.clear();

        if (selectedFoods.isEmpty()) {
            recipeFilterService.cancel();
            progressIndicator.setVisible(false);
            return;
        }
        recipeFilterService.restartFor(selectedFoods);
    }

    /**
     * Find which foods on the filter have been selected
     * @return list of foods selected
     */
    private List<String> getSelectedFoods() {
        List<String> selectedFoods = new ArrayList<>();

        for (Node node : filterList.getChildren()) {
            Button button = (Button) node;

            if (FruitFilterUtilities.isSelected(button)) {
                selectedFoods.add(button.getText().trim());
            }
        }

        return selectedFoods;
    }

    /**
     * Create the recipe cards to display
     * @param recipes
     */
    private void displayRecipes(List<Recipe> recipes) {
        recipeTilePane.getChildren().clear();
        displayedRecipeIds.clear();

        for (Recipe recipe : recipes) {
            if (recipe == null || displayedRecipeIds.contains(recipe.getId())) {
                continue;
            }

            displayedRecipeIds.add(recipe.getId());
            createRecipeCard(recipe);
        }
    }

    private void setStartingFilters() {
        if (startingSelectedFoods.isEmpty()) {
            return;
        }

        for (Node node : filterList.getChildren()) {
            Button button = (Button) node;
            String foodName = button.getText().trim();

            if (startingSelectedFoods.contains(foodName)
                    && !FruitFilterUtilities.isSelected(button)) {
                FruitFilterUtilities.toggleButtonStyle(button);
            }
        }

        refreshRecipesForSelectedFilters();
    }


}
