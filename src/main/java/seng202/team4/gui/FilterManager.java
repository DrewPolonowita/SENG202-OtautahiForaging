package seng202.team4.gui;
import seng202.team4.models.FilterCategory;
import java.util.Set;
import java.util.EnumSet;


/**
 * Manger handles a shared filter state across the application screens
 * this includes the MapController, the TableViewController
 * and the RecipesController
 * @author Team 4
 */

public class FilterManager {
    private final Set<FilterCategory> selectedFilters =
            EnumSet.allOf(FilterCategory.class);

    /**
     * Gets the EnumSet of currently selected filters
     * @return the selected filters
     */
    public Set<FilterCategory> getSelectedFilters() {
        return selectedFilters;
    }

    /**
     * Selects all filters from filterCategory
     */
    public void selectAllFilters() {
        selectedFilters.addAll(EnumSet.allOf(FilterCategory.class));
    }

    /**
     * Deselects all filters from filterCategory.
     */
    public void deselectAllFilters() {
        selectedFilters.clear();
    }
    /**
     * Toggles the selection state of a filter to change it accordingly
     */
    public void toggleFilters(FilterCategory filterCategory) {
        if (!selectedFilters.remove(filterCategory)) {
            selectedFilters.add(filterCategory);
        }
    }

    /**
     * returns selected filters from the FilterCategory
     * @param filterCategory
     *
     */
    public boolean isSelected(FilterCategory filterCategory) {
        return selectedFilters.contains(filterCategory);
    }


}


