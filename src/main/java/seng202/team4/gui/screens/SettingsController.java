package seng202.team4.gui.screens;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.gui.ScreenController;
import seng202.team4.services.StateManager;

/**
 * Controller for the map_screen.fxml window which controls the map
 * @author Team 4
 */
public class SettingsController extends ScreenController {

    @FXML
    private ToggleButton darkModeToggle;
    @FXML
    private StackPane switchTrack;
    @FXML
    private Circle switchCircle;

    private static final Logger LOG = LogManager.getLogger(SettingsController.class);

    /**
     * Initialise the window
     *
     * @param stateManager Reference for window manager
     */
    public SettingsController(StateManager stateManager) {

        super(stateManager);
    }

    public String getFxmlFile() {
        return "/fxml/settings_screen.fxml";
    }

    public String getTitle() {
        return "Map";
    }

    @FXML
    public void onDarkModeToggle() {
        boolean isDark = darkModeToggle.isSelected();
        TranslateTransition slide = new TranslateTransition(Duration.millis(150), switchCircle);
        if (isDark) {
            slide.setToX(24);
        } else {
            slide.setToX(2);
        }
        slide.play();
        getStateManager().getWindowManager().setDarkMode(isDark);
    }
}
