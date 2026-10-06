package seng202.team4.repository;


import seng202.team4.models.Recipe;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeIngredient;
import seng202.team4.models.RecipeSummary;

import java.util.List;
import java.util.Map;


/**
 * Interface for the recipe specific data access object
 */
public interface RecipeRepository extends DAOInterface<Recipe, Integer> {


    /**
     * Retrieve all recipe ids with a specified ingredient
     * @param food the name of the ingredient specified
     * @return all ids for recipes with the food specified as an ingredient
     */
    List<Integer> getRecipeIdsByFoodName(String food);

    /**
     * Adds recipeIngredient object information to the database
     *
     * @param toAdd the ingredient to add
     */
    void addIngredient(RecipeIngredient toAdd);

    /**
     * Adds recipeIngredient information alongside recipe ids in the ingredients in recipe table
     * @param toAdd the ingredient to add
     * @param recipe the recipe the ingredient is in
     * @return a map mapping recipe id to ingredient id and servings id
     */
    Map<Integer, List<Integer>> addIngredientsByRecipe(RecipeIngredient toAdd, Recipe recipe);

    /**
     * Method to check if the recipe details are up-to-date in the last 24hrs and exist
     * in order to fit the FatSecret rule of not storing data for longer than 24hrs
     * @param recipeId id of item to check
     * @return True if the data is good to use, false otherwise
     */
    boolean recipeDetailsUpToDateAndExist(int recipeId);

    /**
     * Add data to the recipe_uses_food table
     * @param food food to add
     * @param recipeId recipe id with the food in it
     */
    void addRecipeUseFood(String food, int recipeId);

    /**
     * Method to save a recipe in the database
     * @param recipe recipe to be saved
     */
    void saveRecipe(Recipe recipe);

    List<RecipeSummary> getRecipeSummariesByFoodName(String food);

    void addRecipeSummary(RecipeSummary recipeSummary);

}
