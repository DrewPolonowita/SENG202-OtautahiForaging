package seng202.team4.gui.screens;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import seng202.team4.gui.ScreenController;
import seng202.team4.gui.utilities.FatSecretAttributionUtilities;
import seng202.team4.models.Fruit;
import seng202.team4.models.Recipe;
import seng202.team4.services.StateManager;
import seng202.team4.services.dataCentre.recipeData.RecipeDataManager;


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
    private VBox filterList;

    private static final Logger LOG = LogManager.getLogger(RecipesController.class);

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
        toggleAllFilterButtons();

    }

    /**
     * Toggles the visibility of the fruit filters.
     */
    @FXML
    private void onFilterClicked() {
        filterList.setVisible(!filterList.isVisible());
    }

    /**
     * Button to test search for apple
     *
     */
    @FXML
    public void appleSearch() {
        recipeSearch("Apple");
    }

    private void recipeSearch(String food) {
        for (Recipe recipe : recipeService.getRecipesForFood(food)) {
            if (recipe == null) {
                continue;
            }
            System.out.println(recipe.getName());
            createRecipeCard(recipe);
        }
    }

    /**
     * Sets up a recipe card to display the recipes from the FatSecret API
     * Searches for recipes with ingredients that are toggled on and returns
     * relevent information on the cards.
     * @param recipe
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
            Image image;
            if (imagePath.startsWith("https://")) {
                image = new Image(imagePath, false);
            } else {
                var resource = getClass().getResource(imagePath);
                if (resource != null) {
                    image = new Image(resource.toExternalForm());
                } else {
                    image = null;
                }
            }
            if (image != null && !image.isError()) {
                imageView.setImage(image);
                double scale = Math.max(CARD_WIDTH / image.getWidth(), IMAGE_HEIGHT / image.getHeight());
                imageView.setFitWidth(image.getWidth() * scale);
                imageView.setFitHeight(image.getHeight() * scale);
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
        card.setOnMouseClicked(event -> {
            StateManager stateManager = getStateManager();
            stateManager.getWindowManager().launchRecipesScreenHighlight(stateManager, recipe);
        });
        recipeTilePane.getChildren().add(card);
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
