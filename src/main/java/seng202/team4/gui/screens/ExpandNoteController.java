package seng202.team4.gui.screens;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.gui.FilterManager;
import seng202.team4.gui.ScreenController;
import seng202.team4.gui.utilities.FruitFilterUtilities;
import seng202.team4.models.FilterCategory;
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

    private static final Logger LOG = LogManager.getLogger(ExpandNoteController.class);

    private final Note note;
    private final JournalController journalController;

    @FXML
    private ImageView photoView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Button deleteLabel;


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
            addTypeOfTreeToFilters(tree);
            handleClose();
            getStateManager().getWindowManager().launchMapScreen(getStateManager(), tree);
        }
    }

    /**
     * Method to add the type of tree to filters, so when viewing on map it's there
     * @param tree tree to add
     */
    private void addTypeOfTreeToFilters(TreeModel tree) {
        FilterManager filterManager = getStateManager().getFilterManager();

        for (FilterCategory filterCategory : FilterCategory.values()) {
            if (FruitFilterUtilities.formatFruitName(filterCategory).equalsIgnoreCase(tree.getFood())) {
                filterManager.selectFilter(filterCategory);
                break;
            }
        }
    }
}
