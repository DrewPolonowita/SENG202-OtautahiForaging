package seng202.team4.services;

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import seng202.team4.gui.utilities.CssUtilities;
/**
 * The class which stores information related to the theme
 * @author Team 4
 */
public class ThemeService {
    private static final String DARK_THEME_PATH = "/css/dark-theme.css";

    private final Scene scene;
    private final GridPane sceneRoot;
    private WebEngine webEngine;
    private Color primaryColor = Color.web("#86CA82");
    private boolean darkMode;
    /**
     * Creates a ThemeService bound to the app's main Scene and its root GridPane.
     *
     * @param scene the Scene whose stylesheets control dark mode
     * @param sceneRoot the root node used to apply primary-color overrides
     */
    public ThemeService(Scene scene, GridPane sceneRoot) {
        this.scene = scene;
        this.sceneRoot = sceneRoot;
    }

    public void setWebEngine(WebEngine webEngine) {
        this.webEngine = webEngine;
        setPrimaryColor(primaryColor);
        setDarkMode(darkMode);
    }

    /**
     * Checks to see if darkmode theme is applied
     * @return true/false bool
     */
    public boolean isDarkMode() {
        return scene.getStylesheets().contains(getClass().getResource(DARK_THEME_PATH).toExternalForm());
    }

    /**
     * Method used to change the primary scene colour
     */
    public void setPrimaryColor(Color color) {
        primaryColor = color;
        String hex = CssUtilities.toHex(color);
        String highlightHex = CssUtilities.toHex(color.deriveColor(0, 1, 0.9, 1));
        sceneRoot.setStyle(String.format("-primary-color: %s; -primary-color-highlight: %s;", hex, highlightHex));
        CssUtilities.refreshStylesheets(scene);

        if (webEngine != null) {
            webEngine.executeScript(String.format(
                    "document.documentElement.style.setProperty('--primary-color', '%s');"
                            + "document.documentElement.style.setProperty('--primary-color-highlight', '%s');",
                    hex,
                    highlightHex
            ));
        }
    }


    /**
     * Method for toggling dark mode on/off
     * @param darkMode true to enable dark mode, false to disable it
     */
    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
        String darkCss = getClass().getResource(DARK_THEME_PATH).toExternalForm();
        var stylesheets = scene.getStylesheets();
        if (darkMode) {
            if (!stylesheets.contains(darkCss)) {
                stylesheets.add(darkCss);
            }
        } else {
            stylesheets.remove(darkCss);
        }
        if (webEngine != null) {
            webEngine.executeScript(String.format(
                    "document.documentElement.classList.%s('dark-theme');",
                    darkMode ? "add" : "remove"
            ));
        }
    }
}
