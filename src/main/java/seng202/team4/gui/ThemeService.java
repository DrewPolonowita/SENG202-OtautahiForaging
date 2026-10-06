package seng202.team4.gui;

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
    private static final String STARTINGPRIMARY = "#67AF76";
    private static final String DARK_THEME_PATH = "/css/dark-theme.css";

    private final Scene scene;
    private final GridPane sceneRoot;
    private WebEngine webEngine;
    private Color primaryColor = Color.web(STARTINGPRIMARY);
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

    /**
     * sets the webEngine for use of filter theming
     */
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
     * Builds the inline style that defines the primary colour variables for a scene root.
     *
     * @param color the primary colour
     * @return a style string such as "-primary-color: #67af76; ..."
     */
    private static String primaryStyleFor(Color color) {
        String hex = CssUtilities.toHex(color);
        String highlightHex = CssUtilities.toHex(color.deriveColor(10, 1, 0.85, 1));
        String darkHex = CssUtilities.toHex(color.deriveColor(10, 1, 0.45, 1));
        return String.format("-primary-color: %s; -primary-color-highlight: %s; -primary-color-dark: %s;", hex, highlightHex, darkHex);
    }

    /**
     * Method used to change the primary scene colour
     */
    public void setPrimaryColor(Color color) {
        primaryColor = color;
        String hex = CssUtilities.toHex(color);
        String highlightHex = CssUtilities.toHex(color.deriveColor(10, 1, 0.85, 1));
        String darkHex = CssUtilities.toHex(color.deriveColor(10, 1, 0.45, 1));
        sceneRoot.setStyle(primaryStyleFor(color));
        CssUtilities.refreshStylesheets(scene);

        if (webEngine != null) {
            webEngine.executeScript(String.format(
                    "document.documentElement.style.setProperty('--primary-color', '%s');"
                            + "document.documentElement.style.setProperty('--primary-color-highlight', '%s');"
                            + "document.documentElement.style.setProperty('--primary-color-dark', '%s');",
                    hex,
                    highlightHex,
                    darkHex
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

    /**
     * Applies the current theme (primary colour and dark mode) to a separate Scene, such as a dialog.
     * Call this after the scene's base stylesheets have been added so dark-theme.css is loaded last.
     *
     * @param target the scene to theme; its root must already be set
     */
    public void applyTo(Scene target) {
        target.getRoot().setStyle(primaryStyleFor(primaryColor));
        String darkCss = getClass().getResource(DARK_THEME_PATH).toExternalForm();
        var stylesheets = target.getStylesheets();
        if (darkMode) {
            if (!stylesheets.contains(darkCss)) {
                stylesheets.add(darkCss);
            }
        } else {
            stylesheets.remove(darkCss);
        }
    }
}
