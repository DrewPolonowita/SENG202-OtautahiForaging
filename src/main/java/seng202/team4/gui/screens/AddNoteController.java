package seng202.team4.gui.screens;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.gui.ScreenController;
import seng202.team4.gui.WindowManager;
import seng202.team4.models.Note;
import seng202.team4.models.TreeModel;
import seng202.team4.services.StateManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;


/**
 * Controller for the Add Note popup
 * @author Team 4
 */
public class AddNoteController extends ScreenController {

    private static final Logger LOG = LogManager.getLogger(AddNoteController.class);
    private final TreeModel treeModel;
    @FXML
    private TextField commonNameDisplay;
    @FXML
    private TextArea fieldNotesArea;
    @FXML
    private VBox uploadZone;
    @FXML
    private Button recordButton;
    @FXML
    private Button backButton;

    private String selectedImagePath;
    private Note existingNote;

    private final JournalController journalController;

    /**
     * Constructor
     * @param stateManager stateManager
     * @param tree provided tree
     */
    public AddNoteController(StateManager stateManager, TreeModel tree, Note existingNote, JournalController journalController) {
        super(stateManager);
        this.treeModel = tree;
        this.existingNote = existingNote;
        this.journalController = journalController;
    }

    /**
     * Gets fxml add note fxml
     * @return fxml path
     */
    @Override
    protected String getFxmlFile() {
        return "/fxml/add_note.fxml";
    }

    /**
     * Gets title of note
     * @return title String
     */
    @Override
    protected String getTitle() {
        return "Add new Note";
    }

    /**
     * Sets common name field to the tree models name and initializes
     */
    @FXML
    private void initialize() {
        if (existingNote != null) {
            backButton.setVisible(true);
            commonNameDisplay.setText(existingNote.getName());
            fieldNotesArea.setText(existingNote.getDescription());
            recordButton.setText("Edit Note");
            selectedImagePath = existingNote.getImagePath();
            updateUploadZonePreview();
        } else {
            backButton.setVisible(false);
            commonNameDisplay.setText(treeModel.getCommonName());
        }
    }

    /**
     * Refreshes the upload zone's contents to show the selected image.
     * a preview of that image is shown in place of the default hint text.
     */
    private void updateUploadZonePreview() {
        uploadZone.getChildren().clear();
        if (selectedImagePath != null) {
            ImageView preview = new ImageView(new Image(new File(selectedImagePath).toURI().toString()));
            preview.setFitWidth(280);
            preview.setFitHeight(110);
            preview.setPreserveRatio(true);
            uploadZone.getChildren().add(preview);
        } else {
            Label hint = new Label("Upload a photo");
            hint.getStyleClass().add("upload-hint");
            uploadZone.getChildren().add(hint);
        }
    }

    /**
     * Creates a new prompt for user to select image
     */
    @FXML
    private void handleSelectedPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File selected = fileChooser.showOpenDialog(uploadZone.getScene().getWindow());
        if (selected != null) {
            try {
                Path imageDir = getStateManager().getDatabaseManager().getDatabaseDirectory().toPath().resolve("images");
                Files.createDirectories(imageDir);
                String extension = selected.getName().substring(selected.getName().lastIndexOf('.'));
                String newFileName = UUID.randomUUID() + extension;
                Path destination = imageDir.resolve(newFileName);
                Files.copy(selected.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                selectedImagePath = destination.toString();
                updateUploadZonePreview();
            } catch (IOException e) {
                LOG.error("Failed to copy over selected photo", e);
            }
        }
    }

    /**
     * Builds a Note with trees information and adds note with note service
     */
    @FXML
    private void handleRecord() {
        if (commonNameDisplay.getText().trim().isEmpty()) {
            emptyNameAlert();
            return;
        }
        if (existingNote != null) {
            Note updated = new Note(existingNote.getId(), commonNameDisplay.getText().trim(), existingNote.getTimeCreated(), existingNote.getTreeId(), fieldNotesArea.getText(), selectedImagePath);
            getStateManager().getNoteService().update(updated);
        } else {
            Note note = new Note(0, commonNameDisplay.getText().trim(), null, treeModel.getId(), fieldNotesArea.getText(), selectedImagePath);
            try {
                getStateManager().getNoteService().add(note);
                handleClose();
            } catch (DuplicateEntryException e) {
                LOG.error("Failed to add note for tree {}: duplicate entry", treeModel.getId(), e);
            }
        }
        ((Stage) commonNameDisplay.getScene().getWindow()).close();
    }

    /**
     * Throws an alert if the user inputs no note name
     */
    public void emptyNameAlert() {
        Alert emptyAlert = new Alert(Alert.AlertType.WARNING);
        emptyAlert.initOwner(commonNameDisplay.getScene().getWindow());
        emptyAlert.setTitle("Missing name");
        emptyAlert.setHeaderText(null);
        emptyAlert.setContentText("Please enter a note name.");
        emptyAlert.showAndWait();
    }

    /**
     * Returns to expanded note after confirming unsaved changes.
     */
    public void handleGoBack() {
        if (!checkDiscardChanges()) {
            return;
        }
        handleClose();
        Platform.runLater(() ->
                getStateManager().getWindowManager()
                        .openExpandNote(existingNote, getStateManager(), journalController));
    }

    /**
     * Handles when a user clicks remove photo from add note/edit note
     */
    public void handleDeletePhoto() {
        selectedImagePath = null;
        updateUploadZonePreview();
    }

    /**
     * Closes window
     */
    public void handleClose() {
        ((Stage) commonNameDisplay.getScene().getWindow()).close();
    }

    /**
     * Closes after confirming unsaved changes can be discarded
     */
    public void handleExit() {
        if (checkDiscardChanges()) {
            handleClose();
        }
    }

    /**
     * Checks for unsaved changes when adding/editing note
     * @return true if no changes, or user confirms discarding them
     * false if the user cancels or dismisses confirmation.
     */
    public boolean checkDiscardChanges() {
        String originalName = existingNote != null ? existingNote.getName() : treeModel.getCommonName();
        String originalDescription = existingNote != null ? existingNote.getDescription() : "";
        String originalPhoto = existingNote != null ? existingNote.getImagePath() : null;

        boolean checkNoteModified =
                !Objects.equals(originalName, commonNameDisplay.getText().trim())
                        || !Objects.equals(originalDescription, fieldNotesArea.getText())
                        || !Objects.equals(originalPhoto, selectedImagePath);
        if (!checkNoteModified) {
            return true;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Your unsaved changes will be lost.",
                ButtonType.CANCEL, ButtonType.OK);
        alert.initOwner(commonNameDisplay.getScene().getWindow());
        alert.setTitle("Discard Changes");
        alert.setHeaderText("Close without saving?");
        return alert.showAndWait().filter(button-> button == ButtonType.OK).isPresent();
    }
}
