package seng202.team4.gui.screens;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.gui.ScreenController;
import seng202.team4.models.Note;
import seng202.team4.models.TreeModel;
import seng202.team4.services.StateManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


/**
 * Controller for the Add Note popup
 * @author Team 4
 */
public class AddNoteController extends ScreenController {

    private final TreeModel treeModel;
    @FXML
    private Label commonNameDisplay;
    @FXML
    private TextArea fieldNotesArea;
    @FXML
    private VBox uploadZone;
    @FXML
    private Button recordButton;

    private String selectedImagePath;
    private Note existingNote;

    /**
     * Constructor
     * @param stateManager stateManager
     * @param tree provided tree
     */
    public AddNoteController(StateManager stateManager, TreeModel tree, Note existingNote) {
        super(stateManager);
        this.treeModel = tree;
        this.existingNote = existingNote;
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
            commonNameDisplay.setText("Editing " + existingNote.getName());
            fieldNotesArea.setText(existingNote.getDescription());
            recordButton.setText("Edit Note");
            selectedImagePath = existingNote.getImagePath();
            updateUploadZonePreview();
        } else {
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
        if (existingNote != null) {
            Note updated = new Note(existingNote.getId(), existingNote.getName(), existingNote.getTimeCreated(), existingNote.getTreeId(), fieldNotesArea.getText(), selectedImagePath);
            getStateManager().getNoteService().update(updated);
        } else {
            Note note = new Note(0, treeModel.getCommonName(), null, treeModel.getId(), fieldNotesArea.getText(), selectedImagePath);
            try {
                getStateManager().getNoteService().add(note);
            } catch (DuplicateEntryException e) {
                LOG.warn("Failed to add note for tree {}: duplicate entry", treeModel.getId(), e);
            }
        }
        ((Stage) commonNameDisplay.getScene().getWindow()).close();
    }

    /**
     * Handles when a user clicks remove photo from add note/edit note
     */
    public void handleDeletePhoto() {
        selectedImagePath = null;
        updateUploadZonePreview();
    }

    /**
     * closes window
     */
    public void handleClose() {
        ((Stage) commonNameDisplay.getScene().getWindow()).close();
    }
}
