package seng202.team4.gui.screens;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.gui.ScreenController;
import seng202.team4.models.Note;
import seng202.team4.services.StateManager;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for the journal_screen.fxml window which displays saved journal entries
 * @author Team 4
 */
public class JournalController extends ScreenController {

    @FXML
    private FlowPane notesFlowPane;
    private StateManager stateManager;

    private static final Logger LOG = LogManager.getLogger(JournalController.class);

    /**
     * Initialise the window
     *
     * @param stateManager Reference for window manager
     */
    public JournalController(StateManager stateManager) {

        super(stateManager);
    }

    @FXML
    private void initialize() {
        LOG.info("JournalController initialize() called");
        List<Note> notes = getStateManager().getNoteService().getAll();
        if (notes.isEmpty()) {
            notesFlowPane.getChildren().add(new Label("No notes yet! click a tree on the map to add one."));
        } else {
            for (Note note : notes) {
                notesFlowPane.getChildren().add(buildNoteCard(note));
            }
        }
    }

    /**
     * Builds the card for each node in a grid style
     * @param note note added by user
     * @return the built card to be iterated through
     */
    private VBox buildNoteCard(Note note) {
        VBox card = new VBox();
        card.getStyleClass().add("note-card");
        card.setPrefWidth(260);
        card.setCursor(Cursor.HAND);

        ImageView thumbnail = new ImageView();
        thumbnail.setFitWidth(260);
        thumbnail.setFitHeight(140);
        thumbnail.setPreserveRatio(false);

        Label name = new Label(note.getName());
        name.getStyleClass().add("note-card-title");

        Label timestamp = new Label(note.getTimeCreated().format(DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a")));
        timestamp.getStyleClass().add("note-card-date");

        card.getChildren().addAll(thumbnail, name, timestamp);

        card.setOnMouseClicked(event -> handleNoteClicked(note));
        return card;
    }

    private void handleNoteClicked(Note note) {
        getStateManager().getWindowManager().openExpandNote(note, getStateManager());
    }

    @Override
    public String getFxmlFile() {
        return "/fxml/journal_screen.fxml";
    }

    @Override
    public String getTitle() {
        return "Field Journal";
    }
}
