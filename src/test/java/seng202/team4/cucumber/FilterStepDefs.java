package seng202.team4.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import seng202.team4.gui.FilterManager;
import seng202.team4.models.FilterCategory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilterStepDefs {

    private FilterManager filterManager;

    @Before
    public void setUp() {
        filterManager = new FilterManager();
    }

    @Given("all the tree filters are selected")
    public void allTreeFiltersSelected() {
        filterManager.selectAllFilters();
    }

    @Given("none of the tree filters are selected")
    public void noTreeFiltersSelected() {
        filterManager.deselectAllFilters();
    }

    @When("the user toggles the {string} filter")
    public void toggleFilter(String food) {
        FilterCategory category = FilterCategory.valueOf(food.toUpperCase());

        filterManager.toggleFilters(category);
    }

    @When("the user selects all the filters")
    public void selectAllFilters() {
        filterManager.selectAllFilters();
    }
    @When("the user clears all the filters")
    public void clearAllFilters() {
        filterManager.deselectAllFilters();
    }

    @Then("the {string} filter is deselected")
    public void filterIsDeselected(String food) {
        FilterCategory filterCategory = FilterCategory.valueOf(food.toUpperCase());

        assertFalse(filterManager.isSelected(filterCategory));
    }

    @Then("the other tree filters remain selected")
    public void otherFiltersStaySelected() {
        assertEquals(FilterCategory.values().length - 1, filterManager.getPossibleFilters().size());
    }

    @Then("every tree filter should be selected")
    public void everyFilterSelected() {
        for (FilterCategory filterCategory : FilterCategory.values()) {
            assertTrue(filterManager.isSelected(filterCategory));
        }
    }

    @Then("all the tree filters are deselected")
    public void everyFilterDeselected() {
        for (FilterCategory category : FilterCategory.values()) {
            assertFalse(filterManager.isSelected(category));
        }
    }

}
