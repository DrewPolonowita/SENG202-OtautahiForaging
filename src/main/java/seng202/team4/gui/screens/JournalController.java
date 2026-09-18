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
            notesFlowPane.getChildren().add(buildEmptyState());
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

    private VBox buildEmptyState() {
        VBox emptyState = new VBox(8);
        emptyState.getStyleClass().add("empty-state");
        emptyState.setPrefWidth(400);
        emptyState.setAlignment(Pos.CENTER);

        ImageView icon = new ImageView(new Image(getClass().getResource("/images/icon.png").toExternalForm()));
        icon.getStyleClass().add("empty-state-icon");

        Label title = new Label("No notes yet");
        title.getStyleClass().add("empty-state-title");

        Label subtitle = new Label("Click a tree on the map to add your first journal entry");
        subtitle.getStyleClass().add("empty-state-subtitle");
        subtitle.setWrapText(true);
        subtitle.setTextAlignment(TextAlignment.CENTER);

        emptyState.getChildren().addAll(icon, title, subtitle);
        return emptyState;
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
