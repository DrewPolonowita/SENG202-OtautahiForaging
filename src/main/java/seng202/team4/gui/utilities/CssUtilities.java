package seng202.team4.gui.utilities;

import javafx.scene.Scene;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Methods for helping with CSS functionality
 * @author Team 4
 */
public final class CssUtilities {
    private CssUtilities() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * Convers string to text
     */
    public static String toHex(Color color) {
        return String.format("#%02x%02x%02x",
                (int) Math.round(color.getRed() * 255),
                (int) Math.round(color.getGreen() * 255),
                (int) Math.round(color.getBlue() * 255));
    }

    /**
     * Used to refreash Scenes stylesheets
     */
    public static void refreshStylesheets(Scene scene) {
        var stylesheets = scene.getStylesheets();
        var copy = new ArrayList<>(stylesheets);
        stylesheets.clear();
        stylesheets.addAll(copy);
    }
}
