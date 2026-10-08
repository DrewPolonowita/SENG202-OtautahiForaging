package seng202.team4.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import seng202.team4.gui.FilterManager;
import seng202.team4.models.FilterCategory;

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
    public void toggleFilter(String fruit) {
        FilterCategory category = FilterCategory.valueOf(fruit.toUpperCase());

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

    @Then("the {string} filter is selected")
    public void filterIsSelected(String fruit) {
        FilterCategory filterCategory = FilterCategory.valueOf(fruit.toUpperCase());

        assertTrue(filterManager.isSelected(filterCategory));
    }

    @Then("the other tree filters remain deselected")
    public void otherFiltersStayDeselected() {
        for (FilterCategory category : FilterCategory.values()) {
            if (category != FilterCategory.APPLE) {
                assertFalse(filterManager.isSelected(category));
            }
        }
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
