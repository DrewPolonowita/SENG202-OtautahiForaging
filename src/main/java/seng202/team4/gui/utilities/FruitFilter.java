package seng202.team4.gui.utilities;
import javafx.geometry.Pos;

import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seng202.team4.models.Fruit;

import seng202.team4.gui.screens.MapController;
public class FruitFilter {

    /**
     * Creates a Fruit filter button for each fruit in enum FRUIT values.
     * @param filterBox
     * @param mapController
     */
    public static void createFruitButtons(VBox filterBox, MapController mapController) {
        filterBox.getChildren().clear();
        for (Fruit fruit : Fruit.values()) {
            Button filterButton = new Button("  " + formatFruitName(fruit));
            filterButton.setAlignment(Pos.TOP_LEFT);
            filterButton.setMaxWidth(Double.MAX_VALUE);
            filterButton.setPrefHeight(35);       // values based off old dimensions

            Region icon = new Region();
            icon.getStyleClass().add("icon");
            icon.getStyleClass().add("icon-" + fruit.name().toLowerCase());

            filterButton.setGraphic(icon);
            filterButton.getStyleClass().add("sideBar-button");
            filterButton.setOnAction(event ->
                    mapController.filterButtonClicked(filterButton, fruit)
            );
            filterBox.getChildren().add(filterButton);
        }
    }

    /**
     * Toggles styling of the filter buttons
     */
    public static void toggleButtonStyle(Button button) {
        if (button.getStyleClass().contains("sideBar-button-selected")) {
            button.getStyleClass().remove("sideBar-button-selected");
            button.getGraphic().getStyleClass().remove("icon-selected");
        } else {
            button.getStyleClass().add("sideBar-button-selected");
            button.getGraphic().getStyleClass().add("icon-selected");
        }
    }

    /**
     * Mapping enum FRUIT values to string format
     */
    private static String formatFruitName(Fruit fruit) {
        String name = fruit.name().toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

}