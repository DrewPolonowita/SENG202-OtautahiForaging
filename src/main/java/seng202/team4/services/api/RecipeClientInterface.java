package seng202.team4.services.api;

import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdResponse;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchResponse;
import seng202.team4.models.api.Request;

/**
 * Interface to call the FatSecret API for recipe information
 */
public interface RecipeClientInterface {

    /**
     * Method for use in the application to find specific recipe
     * @param recipeId recipe being searched for
     * @return request object including the Json and error codes
     */
    Request<RecipeByIdResponse> getRecipeById(int recipeId);


    /**
     * Method for use in the application to search for recipes
     * @param searchExpression expression for fatsecret to use to match for recipes
     * @param mustHaveImages should the results be guaranteed to include images
     * @param maxResults maximum number of results
     * @return request object including the Json and error codes
     */
    Request<RecipeSearchResponse> searchRecipe(String searchExpression, boolean mustHaveImages, int maxResults);
}
