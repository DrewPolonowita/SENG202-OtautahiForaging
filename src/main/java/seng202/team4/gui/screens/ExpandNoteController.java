package seng202.team4.gui.screens;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.gui.ScreenController;
import seng202.team4.models.Food;
import seng202.team4.models.FoodServing;
import seng202.team4.models.Note;
import seng202.team4.models.TreeModel;
import seng202.team4.services.StateManager;

import java.io.File;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the expanded note window, displaying the details of
 * a selected journal note.
 * @author Team 4
 */
public class ExpandNoteController extends ScreenController {

    private static final String GRAMS_FORMAT = "%.1f g";

    private static final Logger LOG = LogManager.getLogger(ExpandNoteController.class);

    private final Note note;
    private final JournalController journalController;

    @FXML private ImageView photoView;
    @FXML private Label nameLabel;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label nutritionLabel;
    @FXML private Label caloriesLabel;
    @FXML private Label carbsLabel;
    @FXML private Label proteinLabel;
    @FXML private Label fatLabel;
    @FXML private VBox nutritionVBox;


    /**
     * Constructs an ExpandNoteController for the given note.
     * @param stateManager reference to the application's state manager
     * @param note the journal note to display
     */
    public ExpandNoteController(StateManager stateManager, Note note, JournalController journalController) {
        super(stateManager);
        this.note = note;
        this.journalController = journalController;
    }

    /**
     * Returns the FXML file used to create the expanded note window.
     * @return the expanded note FXML path file
     */
    @Override
    public String getFxmlFile() {
        return "/fxml/expand_note.fxml";
    }

    /**
     * Returns the title displayed for the expanded note window.
     * @return the title of the window.
     */
    @Override
    public String getTitle() {
        return "Note details";
    }

    /**
     * Initializes the expanded note window by displaying the note's
     * name, creation date, time, description, and image.
     */
    @FXML
    private void initialize() {
        nameLabel.setText(note.getName());
        dateLabel.setText(note.getTimeCreated().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        timeLabel.setText(note.getTimeCreated().format(DateTimeFormatter.ofPattern("h:mm a")));
        descriptionLabel.setText(note.getDescription());
        setNoteImage();
        loadNutrition();
    }

    /**
     * Sets the note image displayed in the expanded note window.
     * If the note has no image, the default application icon is displayed.
     */
    private void setNoteImage() {
        if (note.getImagePath() != null) {
            photoView.setFitWidth(780);
            photoView.setFitHeight(370);
            photoView.setPreserveRatio(true);
            photoView.setImage(new Image(new File(note.getImagePath()).toURI().toString()));
        } else {
            photoView.setFitWidth(360);
            photoView.setFitHeight(360);
            photoView.setPreserveRatio(true);
            photoView.setImage(new Image(getClass().getResource("/images/icon.png").toExternalForm()));
        }
    }

    /**
     * Deletes note and refreshes screen
     */
    @FXML
    private void handleDelete() {
        getStateManager().getNoteService().delete(note);
        handleClose();
        journalController.refreshNotes();
    }

    /**
     * Closes the expanded note window and opens the note editor.
     * runLater used to fully close the expanded note dialog
     * before opening the new modal dialog.
     */
    @FXML
    private void handleEdit() {
        handleClose();
        Platform.runLater(() -> {
            getStateManager().getWindowManager().openAddNoteMenu(null, getStateManager(), note, journalController);
            journalController.refreshNotes();
        });
    }

    /**
     * Closes the expanded note window.
     */
    @FXML
    private void handleClose() {
        ((Stage) nameLabel.getScene().getWindow()).close();
    }

    /**
     * Closes the note dialogue and opens the map at its linked tree.
     * Keeps the dialogue open if the tree can't be found.
     */
    @FXML
    private void handleViewMap() {
        TreeModel tree = getStateManager().getTreeRepository().getOne(note.getTreeId());
        if (tree == null) {
            LOG.error("The tree linked to this note could not be found.");
        } else {
            handleClose();
            getStateManager().getWindowManager().launchMapScreen(getStateManager(), tree);
        }
    }

    /**
     * Loads the notes food nutrition in a background task
     */
    private void loadNutrition() {
        if (note.getWeightGrams() == null || note.getWeightGrams() == 0) {
            nutritionVBox.setVisible(false);
            nutritionVBox.setManaged(false);
            return;
        }
        Task<Food> task = new Task<>() {
            @Override
            protected Food call() {
                TreeModel tree = getStateManager().getTreeRepository().getOne(note.getTreeId());
                if (tree == null || tree.getFood() == null) {
                    return null;
                }
                return getStateManager().getFoodService().getFood(tree.getFood());
            }
        };
        task.setOnSucceeded(event -> displayNutrition(task.getValue()));
        task.setOnFailed(event -> nutritionLabel.setText("Failed to get nutritional information"));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Scales the foods nutrition per 100 grams to the recorded weight and displays the totals.
     * @param food the loaded food, or null if unavailable
     */
    private void displayNutrition(Food food) {
        if (food == null || note.getWeightGrams() == null) {
            nutritionVBox.setVisible(false);
            nutritionVBox.setManaged(false);
            return;
        }
        FoodServing baseWeight = food.getServings().stream()
                .filter(serving -> "100 g".equalsIgnoreCase(serving.getDescription()))
                .findFirst()
                .orElse(null);

        if (baseWeight == null) {
            nutritionVBox.setVisible(false);
            nutritionVBox.setManaged(false);
            return;
        }
        double factor = note.getWeightGrams() / 100.0;
        double calories = baseWeight.getCalories() * factor;
        double carbs = baseWeight.getCarbohydrates() * factor;
        double protein = baseWeight.getProtein() * factor;
        double fat = baseWeight.getFat() * factor;

        nutritionLabel.setText("Nutrition per " + note.getWeightGrams() + " g");

        caloriesLabel.setText(String.format("%.1f kcal", calories));
        carbsLabel.setText(String.format(GRAMS_FORMAT, carbs));
        proteinLabel.setText(String.format(GRAMS_FORMAT, protein));
        fatLabel.setText(String.format(GRAMS_FORMAT, fat));
        ((Stage) nutritionLabel.getScene().getWindow()).sizeToScene();
    }
}
