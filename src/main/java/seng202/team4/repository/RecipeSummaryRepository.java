package seng202.team4.repository;

import seng202.team4.models.RecipeSummary;

import java.util.List;

/**
 * Manages methods for accessing the database for recipe summaries
 */
public interface RecipeSummaryRepository extends DAOInterface<RecipeSummary, Integer> {

    /**
     * Method for accessing recipe summaries of a particular type of food
     * @param food food being search for
     * @return list of recipe summaries
     */
    List<RecipeSummary> getRecipeSummariesByFoodName(String food);

    /**
     * For adding the relationship between a recipe and food in the database
     * @param food food related to the recipe
     * @param recipeId recipe related to the food
     */
    void addRecipeUseFood(String food, int recipeId);
}
