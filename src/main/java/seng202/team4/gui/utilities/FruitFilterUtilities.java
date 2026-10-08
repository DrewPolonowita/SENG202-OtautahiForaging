package seng202.team4.gui.utilities;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import seng202.team4.models.FilterCategory;

public class FruitFilterUtilities {
    private static final String SIDEBAR_BUTTON = "sideBar-button";
    private static final String SIDEBAR_BUTTON_SELECTED = "filter-buttons-selected";
    private static final String ICON = "icon";
    private static final String ICON_SELECTED = "icon-selected";

    private FruitFilterUtilities() {
    }

    /**
     * For each fruit name in the enum FRUIT, a filter button is created
     */
    public static Button createFruitButton(FilterCategory filterCategory) {

        Button filterButton = new Button("  " + formatFruitName(filterCategory));
        filterButton.setAlignment(Pos.TOP_LEFT);
        filterButton.setMaxWidth(Double.MAX_VALUE);
        filterButton.setPrefHeight(35);

        Region icon = new Region();
        icon.getStyleClass().add(ICON);
        icon.getStyleClass().add("icon-" + filterCategory.name().toLowerCase());

        filterButton.setGraphic(icon);
        filterButton.getStyleClass().add(SIDEBAR_BUTTON);


        return filterButton;
    }

    /**
     * Toggles styling of the fruit filter buttons on the Map screen
     */
    public static void toggleButtonStyle(Button button) {
        if (isSelected(button)) {
            button.getStyleClass().remove(SIDEBAR_BUTTON_SELECTED);
            button.getGraphic().getStyleClass().remove(ICON_SELECTED);
        } else {
            button.getStyleClass().add(SIDEBAR_BUTTON_SELECTED);
            button.getGraphic().getStyleClass().add(ICON_SELECTED);
        }
    }

    public static boolean isSelected(Button button) {
        return button.getStyleClass().contains(SIDEBAR_BUTTON_SELECTED);
    }

    /**
     * Formats the enum FRUIT values to a string format
     * which will be shown to the user when filter button is clicked
     * @param filterCategory category to be managed
     */
    public static String formatFruitName(FilterCategory filterCategory) {
        String name = filterCategory.name().toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
