package seng202.team4.gui.screens;

import javafx.concurrent.Task;
import javafx.fxml.FXML;

import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import seng202.team4.gui.ScreenController;
import seng202.team4.gui.utilities.FatSecretAttributionUtilities;
import seng202.team4.gui.utilities.FruitFilterUtilities;
import seng202.team4.gui.utilities.RecipeFilterService;
import seng202.team4.gui.FilterManager;
import seng202.team4.models.FilterCategory;
import seng202.team4.models.Recipe;
import seng202.team4.models.RecipeSummary;
import seng202.team4.services.StateManager;
import seng202.team4.services.dataCentre.recipeData.RecipeDataManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controller for the recipe_screen.fxml window which controls the recipe screen
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
    private HBox filterActions;
    @FXML
    private Text recipeDetailsText;
    @FXML
    private VBox filterList;

    private final StateManager stateManager;
    private final List<String> startingSelectedFoods;

    private RecipeFilterService recipeFilterService;

    private final FilterManager filterManager;

    private static final Logger LOG = LogManager.getLogger(RecipesController.class);

    private final Set<Integer> displayedRecipeIds = new HashSet<>();
    private static final int CARD_WIDTH = 300;
    private static final int CARD_HEIGHT = 260;
    private static final int TEXT_HEIGHT = 60;
    private static final int IMAGE_HEIGHT = CARD_HEIGHT - TEXT_HEIGHT;

    @FXML private StackPane fullRecipeImageArea;
    @FXML private Label fullRecipeTitle;
    @FXML private Label fullRecipePreptime;
    @FXML private Label fullRecipeCooktime;
    @FXML private Label fullRecipeIngredientsText;
    @FXML private Label fullRecipeNutritionText;
    @FXML private Label fullRecipeMethodText;

    /**
     * Initialise the window
     *
     * @param stateManager Reference for window manager
     */
    public RecipesController(StateManager stateManager, FilterManager filterManager) {
        super(stateManager);
        this.stateManager = stateManager;
        this.filterManager = filterManager;
        this.startingSelectedFoods = List.of();
    }

    /**
     * Constructor for going to recipes from map
     * @param stateManager Reference for window manager
     * @param startingSelectedFoods List of foods selected, realistically will be just one
     */
    public RecipesController(StateManager stateManager, FilterManager filterManager, List<String> startingSelectedFoods) {
        super(stateManager);
        this.stateManager = stateManager;
        this.filterManager = filterManager;
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
        fullRecipeImageArea.setClip(new Rectangle(200, 200));
        setUpEvents();
        setStartingFilters();

        refreshRecipesForSelectedFilters();
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
        filterActions.setVisible(filterList.isVisible());
        filterActions.setManaged(filterList.isVisible());
    }

    /**
     * Sets up a recipe card to display the recipes from the FatSecret API
     * Searches for recipes with ingredients that are toggled on and returns
     * relevant information on the cards.
     */
    private void createRecipeCard(RecipeSummary recipeSummary) {
        GridPane card = new GridPane();
        card.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        card.getStyleClass().add("recipeCardBase");

        StackPane imageArea = createImageArea();
        ImageView imageView = getImageView(recipeSummary.getRecipeImageUrl());

        Rectangle clip = new Rectangle(CARD_WIDTH, IMAGE_HEIGHT);
        imageArea.setClip(clip);
        imageArea.getChildren().add(imageView);

        Label title = createRecipeTitle(recipeSummary);
        setRecipeCardRows(card);

        card.add(imageArea, 0, 0);
        card.add(title, 0, 1);
        card.setOnMouseClicked(event -> showRecipe(recipeSummary));
        recipeTilePane.getChildren().add(card);
    }

    /**
     * First row is the image, second is the title
     * @param card recipe card being set up
     */
    private void setRecipeCardRows(GridPane card) {
        RowConstraints textRow = new RowConstraints();
        textRow.setPrefHeight(TEXT_HEIGHT);
        textRow.setValignment(VPos.CENTER);
        card.getRowConstraints().addAll(new RowConstraints(IMAGE_HEIGHT), textRow);
        // pin the column to the card width so long titles wrap instead of running off the card
        card.getColumnConstraints().add(new ColumnConstraints(CARD_WIDTH));
    }

    /**
     * Creates the label for the recipe card title
     * @param recipeSummary recipe title is for
     * @return label object with title
     */
    private @NotNull Label createRecipeTitle(RecipeSummary recipeSummary) {
        Label title = new Label(recipeSummary.getRecipeName());
        title.getStyleClass().add("recipeCardTitle");
        title.setWrapText(true);
        title.setMaxWidth(CARD_WIDTH);
        title.setMaxHeight(TEXT_HEIGHT);
        return title;
    }

    /**
     * Create the image view for the recipe's image
     * @param recipeUrl recipe the image is for
     * @return image view ready for recipe image
     */
    private @NotNull ImageView getImageView(String recipeUrl) {
        ImageView imageView = new ImageView();

        if (recipeUrl != null) {
            loadImageIntoView(recipeUrl, imageView);
        }
        return imageView;
    }

    /**
     * Loads the recipe image into the image view
     * Remote images are loaded in the background
     * @param imagePath the remote URL or local path of image
     * @param imageView image view that displays the loaded image
     */
    private void loadImageIntoView(String imagePath, ImageView imageView) {
        if (imagePath.startsWith("https://")) {
            Image image = new Image(imagePath, true);
            image.progressProperty().addListener((obs, oldProgress, progress)
                    -> setRecipeImageScaled(progress, image, imageView));
        } else {
            var resource = getClass().getResource(imagePath);
            if (resource != null) {
                Image image = new Image(resource.toExternalForm());
                if (!image.isError()) {
                    scaleRecipeImage(image, imageView);
                }
            }
        }
    }

    /**
     * Sets the recipe image once its loaded and scales it into card
     * @param progress loading progress of image
     * @param image image to display
     * @param imageView image view that will have scaled imge
     */
    private static void setRecipeImageScaled(Number progress, Image image, ImageView imageView) {
        if (progress.doubleValue() >= 1.0 && !image.isError()) {
            scaleRecipeImage(image, imageView);
        }
    }

    /**
     * Sets the image of the recipe and scales it to fill the card while preserving its aspect ratio.
     * @param image the recipes image
     * @param imageView the housing of the image
     */
    private static void scaleRecipeImage(Image image, ImageView imageView) {
        imageView.setImage(image);
        double scale = Math.max(CARD_WIDTH / image.getWidth(), IMAGE_HEIGHT / image.getHeight());
        imageView.setFitWidth(image.getWidth() * scale);
        imageView.setFitHeight(image.getHeight() * scale);
    }

    /**
     * Creates the container used for recipe card image
     * @return stack pane sized to image area
     */
    private StackPane createImageArea() {
        StackPane imageArea = new StackPane();
        imageArea.setPrefSize(CARD_WIDTH, IMAGE_HEIGHT);
        imageArea.setMinSize(CARD_WIDTH, IMAGE_HEIGHT);
        imageArea.setMaxSize(CARD_WIDTH, IMAGE_HEIGHT);
        return imageArea;
    }

    /**
     * Full screens the recipe the user clicked
     * @param recipeSummary recipe to show full view
     */
    private void showRecipe(RecipeSummary recipeSummary) {
        progressIndicator.setVisible(true);
        Task<Recipe> task = new Task<>() {
            @Override
            protected Recipe call() {
                return recipeService.getRecipeById(recipeSummary.getRecipeId());
            }
        };
        task.setOnSucceeded(event -> {
            progressIndicator.setVisible(false);
            Recipe recipe = task.getValue();
            if (recipe == null) {
                recipeFailureAlert();
                return;
            }
            populateRecipeDetails(recipe);
            setRecipeDetailsVisible(true);
        });

        task.setOnFailed(event -> {
            progressIndicator.setVisible(false);
            LOG.error("Failed to load recipe", task.getException());
            recipeFailureAlert();
        });

        new Thread(task).start();
    }

    /**
     * Shows an alert when a recipe cannot be loaded.
     */
    private void recipeFailureAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(recipeListPane.getScene().getWindow());
        alert.setTitle("Recipe unavailable");
        alert.setHeaderText(null);
        alert.setContentText("Couldn't load this recipe. Please try again!");
        alert.showAndWait();
    }


    /**
     * Closes fullscreen recipe back to recipes screen
     */
    @FXML
    private void closeRecipe() {
        setRecipeDetailsVisible(false);
    }

    /**
     * Switches between the recipe details and the recipe list
     * @param visible true to show recipe details, false to show the list
     */
    private void setRecipeDetailsVisible(boolean visible) {
        recipeHighlightPane.setVisible(visible);
        recipeHighlightPane.setManaged(visible);
        recipeListPane.setVisible(!visible);
        recipeListPane.setManaged(!visible);
    }

    private void populateRecipeDetails(Recipe recipe) {
        fullRecipeTitle.setText(recipe.getName());
        fullRecipePreptime.setText("Prep time: " + recipe.getPreparationTime() + " min");
        fullRecipeCooktime.setText("Cook time: " + recipe.getCookingTime() + " min");
        fullRecipeIngredientsText.setText(buildIngredientsText(recipe));
        fullRecipeNutritionText.setText(buildNutritionText(recipe));
        fullRecipeMethodText.setText(recipe.getMethod() != null ? recipe.getMethod() : "");
        fullRecipeImageArea.getChildren().setAll(getImageView(recipe.getImage()));
    }

    private String buildNutritionText(Recipe recipe) {
        StringBuilder text = new StringBuilder();
        text.append("Calories: ").append(recipe.getCalories()).append("\n");
        text.append("Carbohydrates: ").append(recipe.getCarbohydrates()).append("g\n");
        text.append("Protein: ").append(recipe.getProtein()).append("g\n");
        text.append("Fat: ").append(recipe.getFat()).append("g");
        return text.toString();
    }

    private String buildIngredientsText(Recipe recipe) {
        StringBuilder text = new StringBuilder();
        if (recipe.getIngredients() != null) {
            for (String ingredient : recipe.getIngredients()) {
                text.append("- ").append(ingredient).append("\n");
            }
        }
        return text.toString().stripTrailing();
    }

    /**
     *For each fruit name in the enum FRUIT, a filter button is creaated
     */
    private void createFruitButtons() {
        filterList.getChildren().clear();

        for (FilterCategory filterCategory : FilterCategory.values()) {
            Button filterButton = FruitFilterUtilities.createFruitButton(filterCategory);
            if (filterManager.isSelected(filterCategory)) {
                FruitFilterUtilities.toggleButtonStyle(filterButton);
            }
            filterButton.setOnAction(event -> filterButtonClicked(filterButton, filterCategory));
            filterList.getChildren().add(filterButton);
        }
    }


    /**
     * Toggles the fruit button being clicked, and updates the
     * map to show only the relevant fruit trees
     * @param buttons filter buttons
     */
    public void filterButtonClicked(Button buttons, FilterCategory filterCategory) {
        FruitFilterUtilities.toggleButtonStyle(buttons);
        filterManager.toggleFilters(filterCategory);
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
            recipeTilePane.getChildren().add(buildEmptyState());
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


        for (FilterCategory filterCategory : filterManager.getPossibleFilters()) {
            selectedFoods.add(FruitFilterUtilities.formatFruitName(filterCategory));
        }


        return selectedFoods;
    }

    /**
     * Create the recipe cards to display
     * @param recipeSummaryList
     */
    private void displayRecipes(List<RecipeSummary> recipeSummaryList) {
        recipeTilePane.getChildren().clear();
        displayedRecipeIds.clear();

        for (RecipeSummary recipeSummary : recipeSummaryList) {
            if (recipeSummary == null || displayedRecipeIds.contains(recipeSummary.getRecipeId())) {
                continue;
            }

            displayedRecipeIds.add(recipeSummary.getRecipeId());
            createRecipeCard(recipeSummary);
        }
    }

    /**
     * Builds the empty state display shown when the Recipe screen displays no recipes.
     * The display contains an icon, title, and explanatory message.
     * @return a VBox containing the empty state display
     */
    private VBox buildEmptyState() {
        VBox emptyState = new VBox(8);
        emptyState.getStyleClass().add("empty-state");
        emptyState.setPrefWidth(300);
        emptyState.setAlignment(Pos.CENTER);

        ImageView icon = new ImageView(new Image(getClass().getResource("/images/iconBW.png").toExternalForm()));
        icon.getStyleClass().add("empty-state-icon");

        Label title = new Label("No filters selected yet");
        title.getStyleClass().add("empty-state-title");

        Label subtitle = new Label("Toggle filters to view recipes");
        subtitle.getStyleClass().add("empty-state-subtitle");
        subtitle.setWrapText(true);
        subtitle.setTextAlignment(TextAlignment.CENTER);

        emptyState.getChildren().addAll(icon, title, subtitle);
        return emptyState;
    }

    /**
     * Updates all filter buttons to match the given selection state.
     * @param selected true to select the buttons, false to deselect them.
     */
    private void updateFilterStyles(boolean selected) {
        for (int i = 0; i < filterList.getChildren().size(); i++) {
            Button button = (Button) filterList.getChildren().get(i);
            if (FruitFilterUtilities.isSelected(button) != selected) {
                FruitFilterUtilities.toggleButtonStyle(button);
            }
        }
    }

    /**
     * For all buttons in FilterCategory, loop through to find buttons selected
     * and deselect all.
     * Update the recipes accordingly to the filters.
     */
    @FXML
    private void deselectAllFilters() {
        filterManager.deselectAllFilters();
        updateFilterStyles(true);
        refreshRecipesForSelectedFilters();
    }

    /**
     * For all buttons in FilterCategory, loop through to find buttons unselected
     * and select all.
     * Update the recipes accordingly to the filters.
     */
    @FXML
    private void selectAllFilters() {
        filterManager.selectAllFilters();
        updateFilterStyles(false);
        refreshRecipesForSelectedFilters();
    }

    /**
     * Opens the help pop up
     */
    @FXML
    private void helpButtonClicked() {
        getStateManager().getWindowManager().openRecipeHelpPopup(getStateManager());
    }

    /**
     * Set the filters to start with
     */
    private void setStartingFilters() {
        if (startingSelectedFoods.isEmpty()) {
            return;
        }

        filterManager.deselectAllFilters();
        for (FilterCategory filterCategory : FilterCategory.values()) {
            String foodName = FruitFilterUtilities.formatFruitName(filterCategory);

            if (startingSelectedFoods.contains(foodName)) {
                filterManager.toggleFilters(filterCategory);
            }
        }

        createFruitButtons();
    }
}
