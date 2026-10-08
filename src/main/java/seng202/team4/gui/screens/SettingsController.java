package seng202.team4.gui.screens;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import seng202.team4.gui.ScreenController;
import seng202.team4.services.StateManager;

/**
 * Controller for the settings_screen.fxml window which controls the settings screen
 * @author Team 4
 */
public class SettingsController extends ScreenController {

    @FXML
    private ToggleButton darkModeToggle;
    @FXML
    private StackPane switchTrack;
    @FXML
    private Circle switchCircle;
    @FXML
    private ColorPicker colorPicker;
    @FXML private Button tableButton;

    /**
     * Initialise the window
     *
     * @param stateManager Reference for window manager
     */
    public SettingsController(StateManager stateManager) {

        super(stateManager);
    }

    /**
     * Method to be called automatically after FXML files loaded
     */
    @FXML
    public void initialize() {
        boolean isDark = getStateManager().getWindowManager().getThemeService().isDarkMode();
        darkModeToggle.setSelected(isDark);
        switchCircle.setTranslateX(isDark ? 24 : 2);
    }

    public String getFxmlFile() {
        return "/fxml/settings_screen.fxml";
    }

    public String getTitle() {
        return "Map";
    }

    /**
     * On click plays animation and sets dark mode on/off
     */
    @FXML
    public void onDarkModeToggle() {
        boolean isDark = darkModeToggle.isSelected();
        TranslateTransition slide = new TranslateTransition(Duration.millis(150), switchCircle);
        slide.setToX(isDark ? 24 : 2);
        slide.play();
        getStateManager().getWindowManager().getThemeService().setDarkMode(isDark);
    }

    /**
     * Javafx color picker for changing primary colour
     */
    @FXML
    public void changeThemeColour() {
        getStateManager().getWindowManager().getThemeService().setPrimaryColor(colorPicker.getValue());
    }



}
