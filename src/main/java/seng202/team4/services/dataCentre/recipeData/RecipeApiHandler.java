package seng202.team4.services.dataCentre.recipeData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.RecipeSummary;
import seng202.team4.models.api.ApiResponseWrapper;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdRecord;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdResponse;

import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchRecord;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchResponse;
import seng202.team4.services.RecipeFactory;
import seng202.team4.services.api.RecipeClientInterface;
import seng202.team4.services.dataCentre.foodData.FoodService;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to interact with the api package
 * @author team 4
 */
public class RecipeApiHandler {
    private static final Logger LOG = LogManager.getLogger(RecipeApiHandler.class);
    private static final int HTTP_OK = 200;
    private final RecipeClientInterface recipeApiClient;

    /**
     * Constructor for the RecipeApiHandler
     * @param recipeApiClient access to api methods
     */
    public RecipeApiHandler(RecipeClientInterface recipeApiClient) {
        this.recipeApiClient = recipeApiClient;
    }

    /**
     * Get recipe ids from the FatSecret api.
     * @param searchExpression input one of the food from the food map in {@link FoodService}
     * @return list of ids to use in calling the FatSecret api for specific recipes
     */
    public List<RecipeSummary> getRecipeSummariesByFoodFromApi(String searchExpression) {
        ApiResponseWrapper<RecipeSearchResponse> response = recipeApiClient.searchRecipe(searchExpression, true, 20);

        if (!validateApiResponse(response, searchExpression)) {
            return List.of();
        }

        List<RecipeSummary> recipeSummaries = new ArrayList<>();
        for (RecipeSearchRecord recipe :
                response.getApiResponse().recipes().recipe()) {
            recipeSummaries.add(RecipeFactory.createRecipeSummaryFromSearch(recipe));
        }

        return recipeSummaries;

    }

    /**
     * Using a specific id of a recipe, get non-storable (more than 24hrs) information about the recipe
     * @param id permanently stored id
     * @return recipe object
     */
    public RecipeByIdRecord getRecipeById(int id) {
        ApiResponseWrapper<RecipeByIdResponse> response = recipeApiClient.getRecipeById(id);

        if (!validateApiResponse(response, id)) {
            return null;
        }

        return response.getApiResponse().recipe();
    }

    /**
     * Validating get recipe ids by food from api
     * @return true if there are no errors, false if an error is found
     */
    private boolean validateApiResponse(ApiResponseWrapper<RecipeSearchResponse> response, String searchExpression) {
        int responseCode = response.getResponseCode();
        if (responseCode != HTTP_OK) {
            LOG.error("Error: getRecipesByIngredientFromApi doesn't return response, code: {}", responseCode);
            return false;
        }

        RecipeSearchResponse apiResponse = response.getApiResponse();
        if (apiResponse == null) {
            LOG.error("Unexpected recipe search response for '{}': null", searchExpression);
            return false;
        }

        if (apiResponse.error() != null) {
            LOG.error("FatSecret recipe search error for '{}': code={}, message={}",
                    searchExpression, apiResponse.error().code(), apiResponse.error().message()
            );
            return false;
        }

        if (apiResponse.recipes() == null || apiResponse.recipes().recipe() == null) {
            LOG.error("Unexpected recipe search response for '{}': {}", searchExpression, apiResponse);
            return false;
        }
        return true;
    }

    /**
     * Validate recipe by id response
     * @return true if there are no errors, false if an error is found
     */
    private boolean validateApiResponse(ApiResponseWrapper<RecipeByIdResponse> response, int id) {
        int responseCode = response.getResponseCode();
        if (responseCode != HTTP_OK) {
            LOG.error("Error: getRecipeById doesn't return response, code: {}", responseCode);
            return false;
        }

        RecipeByIdResponse apiResponse = response.getApiResponse();
        if (apiResponse == null) {
            LOG.error("Unexpected recipe by id response for id {}: null", id);
            return false;
        }

        if (apiResponse.recipe() == null) {
            LOG.error("Unexpected recipe by id response for id {}: {}", id, apiResponse);
            return false;
        }

        return true;
    }
}


