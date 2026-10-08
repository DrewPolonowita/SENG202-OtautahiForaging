package seng202.team4.gui.screens;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seng202.team4.gui.ScreenController;
import seng202.team4.services.StateManager;

/**
 * Controller for the help popup
 * @author Team 4
 */
public class HelpPopupController extends ScreenController {

    private String page;

    @FXML
    private VBox helpPopup;
    @FXML
    private ScrollPane mapPageScrollPane;
    @FXML
    private Label pageTextBox;
    @FXML
    private ScrollPane journalPageScrollPane;
    @FXML
    private ScrollPane recipePageScrollPane;

    /**
    * Initialises the window
    * @param stateManager Reference for state manager
    */
    public HelpPopupController(StateManager stateManager, String page) {
        super(stateManager);
        this.page = page;
    }

    /**
    * Initialises the help popup
    */
    @FXML
    private void initialize() {
        resetHelpPopUp();
        setHelpPage();
    }

    /**
     * Hides all help Vboxes
     */
    @FXML
    private void resetHelpPopUp() {
        mapPageScrollPane.setVisible(false);
        mapPageScrollPane.setManaged(false);
        journalPageScrollPane.setVisible(false);
        journalPageScrollPane.setManaged(false);
        recipePageScrollPane.setVisible(false);
        recipePageScrollPane.setManaged(false);
    }

    /**
    * Selects the correct help popup based on the page the user is currently on
    */
    @FXML
    private void setHelpPage() {
        if (page.equals("Map")) {
            setPageMap();
        }
        if (page.equals("Journal")) {
            setPageJournal();
        }
        if (page.equals("Recipe")) {
            setPageRecipe();
        }
    }

    /**
     * Sets the pop up information to fit the map page
     */
    @FXML
    private void setPageMap() {
        pageTextBox.setText("The Map Page");
        mapPageScrollPane.setManaged(true);
        mapPageScrollPane.setVisible(true);
    }

    /**
     * Sets the pop up information to fit the journal page
     */
    @FXML
    private void setPageJournal() {
        pageTextBox.setText("The Journal Page");
        journalPageScrollPane.setManaged(true);
        journalPageScrollPane.setVisible(true);
    }

    /**
     * Sets the pop up information to fit the recipe page
     */
    @FXML
    private void setPageRecipe() {
        pageTextBox.setText("The Recipe Page");
        recipePageScrollPane.setVisible(true);
        recipePageScrollPane.setManaged(true);
    }

    /**
     * Exits the help pop up
     */
    @FXML
    private void exitPopUp() {
        ((Stage) helpPopup.getScene().getWindow()).close();
    }

    /**
     * Returns the FXML file used to create the help pop up
     * @return the guidelines pop up FXML path file
     */
    @Override
    public String getFxmlFile() {
        return "/fxml/help_popup.fxml";
    }

    /**
     * Returns the title displayed for the help pop up
     * @return the title of the window.
     */
    @Override
    public String getTitle() { return "Help"; }
}
