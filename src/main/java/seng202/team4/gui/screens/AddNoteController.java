package seng202.team4.gui.screens;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.gui.ScreenController;
import seng202.team4.models.Note;
import seng202.team4.models.TreeModel;
import seng202.team4.services.StateManager;


/**
 * Controller for the Add Note popup
 */
public class AddNoteController extends ScreenController {

    private final TreeModel treeModel;
    @FXML
    private TextField commonNameField;
    @FXML
    private TextArea fieldNotesArea;

    /**
     * Constructor
     * @param stateManager stateManager
     * @param tree provided tree
     */
    public AddNoteController(StateManager stateManager, TreeModel tree) {
        super(stateManager);
        this.treeModel = tree;
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
        commonNameField.setText(treeModel.getCommonName());
    }

    /**
     * Builds a Note with trees information and adds note with note service
     */
    @FXML
    private void handleRecord() {
        Note note = new Note(0, treeModel.getCommonName() + " #" + treeModel.getId(), null, treeModel.getId());
        try {
            getStateManager().getNoteService().add(note);
        } catch (DuplicateEntryException e) {
            LOG.warn("Failed to add note for tree {}: duplicate entry", treeModel.getId(), e);
        }
        ((Stage) commonNameField.getScene().getWindow()).close();
    }

    public void handleClose() {
        ((Stage) commonNameField.getScene().getWindow()).close();
    }
}
