package seng202.team4.gui.screens;


import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seng202.team4.gui.ScreenController;
import seng202.team4.services.StateManager;

/**
 * Controller for the guidelines popup
 * @author Team 4
 */
public class GuidelinePopupController extends ScreenController {

    @FXML
    private VBox guidelinesPopup;

    /**
     * Initialises the window
     * @param stateManager Reference for state manager
     */
    public GuidelinePopupController(StateManager stateManager) {
        super(stateManager);
    }


    @FXML
    private void exitPopUp() {
        ((Stage) guidelinesPopup.getScene().getWindow()).close();
    }

    @Override
    public String getFxmlFile() { return "/fxml/guidelines_popup.fxml"; }

    @Override
    public String getTitle() { return "Guidelines"; }



}
