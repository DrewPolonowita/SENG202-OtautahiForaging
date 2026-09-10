package seng202.team4.services.api;

import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdResponse;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchResponse;
import seng202.team4.models.api.Request;

public interface RecipeClientInterface {

    Request<RecipeByIdResponse> getRecipeById(int recipeId);


    Request<RecipeSearchResponse> searchRecipe(String searchExpression, boolean mustHaveImages, int maxResults);
}
