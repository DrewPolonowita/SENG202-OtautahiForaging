package seng202.team4.gui.screens;

import javafx.fxml.FXML;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import seng202.team4.gui.ScreenController;
import seng202.team4.models.Recipe;
import seng202.team4.services.StateManager;
import seng202.team4.services.dataCentre.recipeData.RecipeDataManager;
import seng202.team4.services.dataCentre.recipeData.RecipeService;

import java.util.List;

/**
 * Controller for the map_screen.fxml window which controls the map
 * @author Team 4
 */
public class RecipesController extends ScreenController {

    @FXML
    private TilePane recipeTilePane;
    private RecipeDataManager recipeService;

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
}
