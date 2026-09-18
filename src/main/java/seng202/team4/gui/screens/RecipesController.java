package seng202.team4.gui.screens;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
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
import seng202.team4.gui.ScreenController;
import seng202.team4.gui.utilities.FatSecretAttributionUtilities;
import seng202.team4.models.Fruit;
import seng202.team4.models.Recipe;
import seng202.team4.services.StateManager;
import seng202.team4.services.dataCentre.recipeData.RecipeDataManager;

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

    private static final Logger LOG = LogManager.getLogger(RecipesController.class);

    private final Set<Integer> displayedRecipeIds = new HashSet<>();
    /**
     * Initialise the window
     *
     * @param stateManager Reference for window manager
     */
    public RecipesController(StateManager stateManager) {
        super(stateManager);
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
        ImageView attribution = FatSecretAttributionUtilities.createStandardAttributionImage();
        attributionPane.getChildren().add(attribution);
        createFruitButtons();

    }

    /**
     * Toggles the visibility of the fruit filters.
     */
    @FXML
    private void onFilterClicked() {
        filterList.setVisible(!filterList.isVisible());
    }

    /**
     * https://stackoverflow.com/questions/9165251/execute-task-in-background-in-javafx
     * @param food type of search food
     */
    private void recipeSearch(String food) {

        Task<List<Recipe>> task = new Task<>() {
            @Override
            protected List<Recipe> call() {
                return recipeService.getRecipesForFood(food);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                progressIndicator.visibleProperty().unbind();
                progressIndicator.setVisible(false);

                List<Recipe> recipes = getValue();
                for (Recipe recipe : recipes) {
                    if (recipe == null || displayedRecipeIds.contains(recipe.getId())) {
                        continue;
                    }
                    displayedRecipeIds.add(recipe.getId());
                    createRecipeCard(recipe);
                }
            }

            @Override
            protected void failed() {
                super.failed();
                progressIndicator.visibleProperty().unbind();
                progressIndicator.setVisible(false);
                LOG.error("Failed to load recipes for {}", food);
            }

        };

        progressIndicator.visibleProperty().bind(task.runningProperty());


        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }


    /**
     * Sets up a recipe card to display the recipes from the FatSecret API
     * Searches for recipes with ingredients that are toggled on and returns
     * relevant information on the cards.
     */
    private void createRecipeCard(Recipe recipe) {
        final int CARD_WIDTH = 300;
        final int CARD_HEIGHT = 260;
        final int TEXT_HEIGHT = 60;
        final int IMAGE_HEIGHT = CARD_HEIGHT - TEXT_HEIGHT;

        GridPane card = new GridPane();
        card.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        card.getStyleClass().add("recipeCardBase");

        StackPane imageArea = new StackPane();
        imageArea.setPrefSize(CARD_WIDTH, IMAGE_HEIGHT);
        imageArea.setMinSize(CARD_WIDTH, IMAGE_HEIGHT);
        imageArea.setMaxSize(CARD_WIDTH, IMAGE_HEIGHT);

        ImageView imageView = new ImageView();
        String imagePath = recipe.getImage();
        if (imagePath != null) {
            if (imagePath.startsWith("https://")) {
                Image image = new Image(imagePath, true);
                image.progressProperty().addListener((obs, oldProgress, progress) -> {
                    if (progress.doubleValue() >= 1.0 && !image.isError()) {
                        imageView.setImage(image);
                        double scale = Math.max(CARD_WIDTH / image.getWidth(), IMAGE_HEIGHT / image.getHeight());
                        imageView.setFitWidth(image.getWidth() * scale);
                        imageView.setFitHeight(image.getHeight() * scale);
                    }
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
        Rectangle clip = new Rectangle(CARD_WIDTH, IMAGE_HEIGHT);
        imageArea.setClip(clip);

        imageArea.getChildren().add(imageView);
        Label title = new Label(recipe.getName());
        title.getStyleClass().add("recipeCardTitle");
        title.setWrapText(true);
        RowConstraints textRow = new RowConstraints();
        textRow.setPrefHeight(TEXT_HEIGHT);
        textRow.setValignment(VPos.CENTER);
        card.getRowConstraints().addAll(new RowConstraints(IMAGE_HEIGHT), textRow);
        card.add(imageArea, 0, 0);
        card.add(title, 0, 1);
        card.setOnMouseClicked(event -> showRecipe(recipe));
        recipeTilePane.getChildren().add(card);
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
        for (Fruit fruit : Fruit.values()) {
            Button filterButton = new Button("  " + formatFruitName(fruit));
            filterButton.setAlignment(Pos.TOP_LEFT);
            filterButton.setMaxWidth(Double.MAX_VALUE);
            filterButton.setPrefHeight(35);
            Region icon = new Region();
            icon.getStyleClass().add("icon");
            icon.getStyleClass().add("icon-" + fruit.name().toLowerCase());
            filterButton.setGraphic(icon);
            filterButton.getStyleClass().add("sideBar-button");
            filterButton.setOnAction(event -> filterButtonClicked(filterButton, fruit));
            filterList.getChildren().add(filterButton);
        }
    }

    /**
     * Toggles styling of the filter buttons
     */
    public static void toggleButtonStyle(Button button) {
        if (button.getStyleClass().contains("sideBar-button-selected")) {
            button.getStyleClass().remove("sideBar-button-selected");
            button.getGraphic().getStyleClass().remove("icon-selected");
        } else {
            button.getStyleClass().add("sideBar-button-selected");
            button.getGraphic().getStyleClass().add("icon-selected");
        }
    }

    /**
     * Toggles the fruit button being clicked, and updates the
     * map to show only the relevant fruit trees
     * @param buttons
     * @param fruit
     */
    public void filterButtonClicked(Button buttons, Fruit fruit) {
        toggleButtonStyle(buttons);

        refreshRecipesForSelectedFilters();
    }

    /**
     *
     */
    private void refreshRecipesForSelectedFilters() {
        recipeTilePane.getChildren().clear();
        displayedRecipeIds.clear();

        for (Node node : filterList.getChildren()) {
            Button button = (Button) node;

            if (button.getStyleClass().contains("sideBar-button-selected")) {
                recipeSearch(button.getText().trim());
            }
        }
    }

    /**
     * Mapping enum FRUIT values to string format
     */
    private static String formatFruitName(Fruit fruit) {
        String name = fruit.name().toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * Toggles on and off all the fruits to be included in the map by default
     **/
    public void toggleAllFilterButtons() {
        for (Node node : filterList.getChildren()) {
            Button button = (Button) node;
            toggleButtonStyle(button);
        }
    }
}
