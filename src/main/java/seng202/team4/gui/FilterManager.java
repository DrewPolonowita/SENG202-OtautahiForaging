package seng202.team4.gui;

import seng202.team4.models.FilterCategory;
import java.util.HashSet;
import java.util.Set;
import java.util.EnumSet;


/**
 * Manager handles a shared filter state across the application screens
 * this includes the MapController, the TableViewController
 * and the RecipesController
 * @author Team 4
 */

public class FilterManager {
    private final Set<FilterCategory> possibleFilters =
            EnumSet.allOf(FilterCategory.class);

    /**
     * Gets the EnumSet of currently selected filters
     * @return the selected filters
     */
    public Set<FilterCategory> getPossibleFilters() {
        return new HashSet<>(possibleFilters);
    }

    /**
     * Selects all filters from filterCategory
     */
    public void selectAllFilters() {
        possibleFilters.addAll(EnumSet.allOf(FilterCategory.class));
    }

    /**
     * Deselects all filters from filterCategory.
     */
    public void deselectAllFilters() {
        possibleFilters.clear();
    }

    /**
     * Toggles the selection state of a filter to change it accordingly
     * @param filterCategory the filter selected
     */
    public void toggleFilters(FilterCategory filterCategory) {
        if (!possibleFilters.remove(filterCategory)) {
            possibleFilters.add(filterCategory);
        }
    }

    /**
     * Returns selected filters from the FilterCategory
     * @param filterCategory the filter selected
     *
     */
    public boolean isSelected(FilterCategory filterCategory) {
        return possibleFilters.contains(filterCategory);
    }

    /**
     * Adds one specified filter to selection
     * @param filterCategory the filter selected
     */
    public void selectFilter(FilterCategory filterCategory) {
        possibleFilters.add(filterCategory);
    }

}


