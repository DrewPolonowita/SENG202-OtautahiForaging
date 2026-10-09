package seng202.team4.gui.utilities;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import seng202.team4.models.FilterCategory;

/**
 * Class to provide utilities for the food filters
 */
public class FoodFilterUtilities {
    private static final String SIDEBAR_BUTTON = "sideBar-button";
    private static final String SIDEBAR_BUTTON_SELECTED = "filter-buttons-selected";
    private static final String ICON = "icon";
    private static final String ICON_SELECTED = "icon-selected";

    /**
     * Empty constructor to hide the implicit public one
     */
    private FoodFilterUtilities() {}

    /**
     * For each filter category in the enum FilterCategory, a filter button is created
     * @param filterCategory the specific filter for each food
     */
    public static Button createFilterButton(FilterCategory filterCategory) {

        Button filterButton = new Button("  " + formatFilterName(filterCategory));
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
     * Toggles styling of the food filter buttons on the Map screen
     * @param button for the filters
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

    /**
     * Returns true or false based off of the buttons styling
     * @param button for the filters
     */
    public static boolean isSelected(Button button) {
        return button.getStyleClass().contains(SIDEBAR_BUTTON_SELECTED);
    }

    /**
     * Formats the enum FilterCategory values to a string format
     * which will be shown to the user when filter button is clicked
     * @param filterCategory category to be managed
     */
    public static String formatFilterName(FilterCategory filterCategory) {
        String name = filterCategory.name().toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
