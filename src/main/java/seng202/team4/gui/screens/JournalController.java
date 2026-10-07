package seng202.team4.gui.screens;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.gui.ScreenController;
import seng202.team4.models.Note;
import seng202.team4.services.StateManager;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

/**
 * Controller for the journal_screen.fxml window which displays saved journal entries
 * @author Team 4
 */
public class JournalController extends ScreenController {

    private static final Logger LOG = LogManager.getLogger(JournalController.class);

    @FXML
    private FlowPane notesFlowPane;

    /**
     * Constructs a JournalController with the given state manager.
     * @param stateManager Reference for window manager
     */
    public JournalController(StateManager stateManager) {

        super(stateManager);
    }

    /**
     * Initializes the journal screen by retrieving all saved notes.
     * If no notes exist, an empty state message is displayed.
     * Otherwise, a card is created and displayed for each note.
     */
    @FXML
    private void initialize() {
        LOG.info("JournalController initialize() called");
        refreshNotes();
    }

    /**
     * Refresh the notes.
     * called on init and when a note is deleted.
     */
    public void refreshNotes() {
        notesFlowPane.getChildren().clear();
        List<Note> notes = getStateManager().getNoteService().getAll();
        if (notes.isEmpty()) {
            notesFlowPane.getChildren().add(buildEmptyState());
        } else {
            for (Note note : notes) {
                notesFlowPane.getChildren().add(buildNoteCard(note));
            }
        }
    }

    /**
     * Builds a visual card displaying the details of a journal note.
     * The card includes the note's image, name, and creation timestamp.
     * @param note the journal note to display
     * @return a Vbox containing the note card
     */
    private VBox buildNoteCard(Note note) {
        VBox card = new VBox();
        card.getStyleClass().add("note-card");
        card.setPrefWidth(260);
        card.setCursor(Cursor.HAND);

        ImageView thumbnail = new ImageView();
        StackPane photoContainer = new StackPane(thumbnail);
        photoContainer.getStyleClass().add("photo-container");
        photoContainer.setPrefSize(260, 140);
        photoContainer.setMaxSize(260, 140);

        if (note.getImagePath() != null) {
            thumbnail.setFitWidth(260);
            thumbnail.setFitHeight(140);
            thumbnail.setPreserveRatio(false);
            thumbnail.setImage(new Image(new File(note.getImagePath()).toURI().toString()));
        } else {
            thumbnail.setFitWidth(90);
            thumbnail.setFitHeight(90);
            thumbnail.setPreserveRatio(true);
            thumbnail.setImage(new Image(getClass().getResource("/images/icon.png").toExternalForm()));
        }

        Label name = new Label(note.getName());
        name.getStyleClass().add("note-card-title");

        Label timestamp = new Label(note.getTimeCreated().format(DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a")));
        timestamp.getStyleClass().add("note-card-date");

        card.getChildren().addAll(photoContainer, name, timestamp);
        card.setOnMouseClicked(event -> handleNoteClicked(note));
        return card;
    }

    /**
     * Builds the empty state display shown when the journal contains no notes.
     * The display contains an icon, title, and explanatory message.
     * @return a VBox containing the empty state display
     */
    private VBox buildEmptyState() {
        VBox emptyState = new VBox(8);
        emptyState.getStyleClass().add("empty-state");
        emptyState.setPrefWidth(400);
        emptyState.setAlignment(Pos.CENTER);

        ImageView icon = new ImageView(new Image(getClass().getResource("/images/icon.png").toExternalForm()));
        icon.getStyleClass().add("empty-state-icon");

        Label title = new Label("No notes yet");
        title.getStyleClass().add("empty-state-title");

        Label subtitle = new Label("Click here to navigate to the map");
        subtitle.getStyleClass().add("empty-state-subtitle");
        subtitle.setWrapText(true);
        subtitle.setTextAlignment(TextAlignment.CENTER);

        emptyState.getChildren().addAll(icon, title, subtitle);

        emptyState.setOnMouseClicked(event ->
            getStateManager().getWindowManager().launchMapScreen(getStateManager()));
        return emptyState;
    }

    /**
     * Opens the expanded view for the selected journal note.
     * @param note the journal note selected by the user
     */
    private void handleNoteClicked(Note note) {
        getStateManager().getWindowManager().openExpandNote(note, getStateManager(), this);
    }

    /**
     * Sorts journal menu's notes alphabetically
     */
    @FXML
    private void handleSortAlphabetical() {
        List<Note> notes = getStateManager().getNoteService().getAll();
        notes.sort(Comparator.comparing(Note::getName));
        notesFlowPane.getChildren().clear();
        for (Note note : notes) {
            notesFlowPane.getChildren().add(buildNoteCard(note));
        }
    }

    /**
     * Sorts journal menu's notes by newest first
     */
    @FXML
    private void handleSortNewest() {
        List<Note> notes = getStateManager().getNoteService().getAll();
        notes.sort(Comparator.comparing(Note::getTimeCreated).reversed());
        notesFlowPane.getChildren().clear();
        for (Note note: notes) {
            notesFlowPane.getChildren().add(buildNoteCard(note));
        }
    }

    /**
     * Sorts journal menu's notes by oldest first
     */
    @FXML
    private void handleSortOldest() {
        List<Note> notes = getStateManager().getNoteService().getAll();
        notes.sort(Comparator.comparing(Note::getTimeCreated));
        notesFlowPane.getChildren().clear();
        for (Note note: notes) {
            notesFlowPane.getChildren().add(buildNoteCard(note));
        }
    }

    /**
     * Returns the FXML file used to create the journal screen.
     * @return the journal screen FXML path file
     */
    @Override
    public String getFxmlFile() {
        return "/fxml/journal_screen.fxml";
    }

    /**
     * Returns the title displayed for the journal screen.
     * @return the title of the journal screen
     */
    @Override
    public String getTitle() {
        return "Field Journal";
    }
}
