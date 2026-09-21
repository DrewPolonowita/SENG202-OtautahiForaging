package seng202.team4.services.dataCentre.recipeData;

import seng202.team4.models.Recipe;

import java.util.List;

/**
 * Interface for interacting with recipe data, abstracting away the api vs database details
 */
public interface RecipeDataManager {
    /**
     * Gets recipes of a particular type of food from the api or database
     * @param food Must be a key from {@link seng202.team4.services.FoodMapper} e.g "Apple" or "Walnut"
     * @return List of recipes
     */
    List<Recipe> getRecipesForFood(String food);
}
