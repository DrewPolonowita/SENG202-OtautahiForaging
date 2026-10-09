package seng202.team4.gui.screens;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import seng202.team4.gui.ScreenController;
import seng202.team4.gui.ThemeService;
import seng202.team4.gui.utilities.CssUtilities;
import seng202.team4.services.StateManager;


/**
 * Controller for the settings_screen.fxml window which controls the settings screen
 * @author Team 4
 */
public class SettingsController extends ScreenController {

    static final String DEFAULT_ACCENT = "#67AF76";

    @FXML private ToggleButton darkModeToggle;
    @FXML private StackPane switchTrack;
    @FXML private Circle switchCircle;
    @FXML private ColorPicker colorPicker;
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
        ThemeService theme = getStateManager().getWindowManager().getThemeService();
        colorPicker.setValue(theme.getPrimaryColor());
        boolean isDark = theme.isDarkMode();
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
        saveTheme();
    }

    /**
     * Javafx color picker for changing primary colour
     */
    @FXML
    public void changeThemeColour() {
        getStateManager().getWindowManager().getThemeService().setPrimaryColor(colorPicker.getValue());
        saveTheme();
    }

    /**
     * Resets the main theme off a button
     */
    @FXML
    public void resetThemes() {
        if (getStateManager().getWindowManager().getThemeService().isDarkMode()) {
            darkModeToggle.setSelected(false);
            onDarkModeToggle();
        }
        getStateManager().getWindowManager().getThemeService().setDefaultColor();
        colorPicker.setValue(Color.web(DEFAULT_ACCENT));
        saveTheme();
    }

    /**
     * Saves the current theme (primary colour and dark mode) to the database
     */
    private void saveTheme() {
        ThemeService theme = getStateManager().getWindowManager().getThemeService();
        getStateManager().getThemeRepository().save(CssUtilities.toHex(theme.getPrimaryColor()), theme.isDarkMode());
    }
}
