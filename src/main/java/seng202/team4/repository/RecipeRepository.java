package seng202.team4.repository;

import seng202.team4.models.Recipe;

import java.util.List;


/**
 * Interface for the recipe specific data access object
 */
public interface RecipeRepository extends DAOInterface<Recipe, Integer> {

    /**
     * Retrieve all recipe ids with a specified ingredient
     * @param food the name of the ingredient specified
     * @return all ids for recipes with the food specified as an ingredient
     */
    List<Integer> getRecipeIdByFoodName(String food);
}
