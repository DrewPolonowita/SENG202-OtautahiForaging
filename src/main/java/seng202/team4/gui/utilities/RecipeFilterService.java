package seng202.team4.gui.utilities;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.RecipeSummary;
import seng202.team4.services.dataCentre.recipeData.RecipeDataManager;

import java.util.ArrayList;
import java.util.List;

/**
 * https://stackoverflow.com/questions/51312677/how-to-cancel-a-task-if-new-instance-of-task-is-started
 * Service for showing recipes, which helps us cancel the tasks if the filters are spammed.
 * This prevents multiple threads trying to access and change the same data at once
 */
public class RecipeFilterService extends Service<List<RecipeSummary>> {
    private static final Logger LOG = LogManager.getLogger(RecipeFilterService.class);
    private final RecipeDataManager recipeService;
    private List<String> selectedFoods = List.of();

    /**
     * Constructor
     */
    public RecipeFilterService(RecipeDataManager recipeService) {
        this.recipeService = recipeService;
    }

    /**
     * Call when changing filters, restarts the task
     * @param selectedFoods filter state
     */
    public void restartFor(List<String> selectedFoods) {
        this.selectedFoods = List.copyOf(selectedFoods);
        restart();
    }


    /**
     * Method to create the task for showing recipes. Instead of doing it one by one, we use all the selected
     * foods and do them all at once.
     * @return Recipes to display
     */
    @Override
    protected Task<List<RecipeSummary>> createTask() {
        return new Task<>() {
            // Task state should be immutable/encapsulated
            private final List<String> selectedFoodsCopy = selectedFoods;

            @Override
            protected List<RecipeSummary> call() {
                List<RecipeSummary> recipeSummaries = new ArrayList<>();
                for (String food : selectedFoodsCopy) {
                    if (isCancelled()) {
                        return List.of();
                    }
                    recipeSummaries.addAll(recipeService.getRecipeSummariesByFood(food));
                }

                return recipeSummaries;
            }
            @Override
            protected void succeeded() {
                LOG.info("Task finished");
                super.succeeded();
            }

            @Override
            protected void cancelled() {
                LOG.info("Task cancelled");
                super.cancelled();
            }
        };
    }

}
