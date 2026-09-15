package seng202.team4.gui.screens;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import seng202.team4.gui.ScreenController;
import seng202.team4.models.Note;
import seng202.team4.services.StateManager;

import java.time.format.DateTimeFormatter;

public class ExpandNoteController extends ScreenController {

    private final Note note;

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


    public ExpandNoteController(StateManager stateManager, Note note) {
        super(stateManager);
        this.note = note;
    }

    @Override
    public String getFxmlFile() {
        return "/fxml/expand_note.fxml";
    }

    @Override
    public String getTitle() {
        return "Note details";
    }

    @FXML
    private void initialize() {
        nameLabel.setText(note.getName());
        dateLabel.setText(note.getTimeCreated().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        timeLabel.setText(note.getTimeCreated().format(DateTimeFormatter.ofPattern("h:mm a")));
        descriptionLabel.setText(note.getDescription());
//        photoView.setImage(new Image(note.getImagePath()));
    }

    @FXML
    private void handleClose() {
        ((Stage) nameLabel.getScene().getWindow()).close();
    }
}
