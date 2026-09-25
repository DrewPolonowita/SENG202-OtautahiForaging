package seng202.team4.gui.screens;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import seng202.team4.gui.ScreenController;
import seng202.team4.models.Note;
import seng202.team4.services.StateManager;

import java.io.File;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the expanded note window, displaying the details of
 * a selected journal note.
 * @author Team 4
 */
public class ExpandNoteController extends ScreenController {

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
            photoView.setPreserveRatio(false);
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
            getStateManager().getWindowManager().openAddNoteMenu(null, getStateManager(), note);
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
}
