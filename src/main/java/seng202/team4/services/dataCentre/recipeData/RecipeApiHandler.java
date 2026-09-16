package seng202.team4.services.dataCentre.recipeData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdRecord;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdResponse;

import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchRecord;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchResponse;
import seng202.team4.models.api.Request;
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
    private final RecipeClientInterface recipeApiClient;

    /**
     * Constructor for the RecipeApiHandler
     */
    public RecipeApiHandler(RecipeClientInterface recipeApiClient) {
        this.recipeApiClient = recipeApiClient;
    }

    /**
     * Get recipe ids from the FatSecret api.
     * @param searchExpression input one of the food from the food map in {@link FoodService}
     * @return list of ids to use in calling the FatSecret api for specific recipes
     */
    public List<Integer> getRecipeIdsByFoodFromApi(String searchExpression) {
        Request<RecipeSearchResponse> response = recipeApiClient.searchRecipe(searchExpression, true, 20);
        int responseCode = response.getResponseCode();
        if (responseCode != 200) {
            LOG.error("Error: getRecipesByIngredientFromApi doesn't return response, code: " + responseCode);
            return List.of();
        }
        List<Integer> recipeIds = new ArrayList<>();
        for (RecipeSearchRecord recipe : response.getApiResponse().recipes().recipe()) {
            recipeIds.add(recipe.id());
        }

        return recipeIds;


    }

    /**
     * Using a specific id of a recipe, get non-storable (more than 24hrs) information about the recipe
     * @param id permanently stored id
     * @return recipe object
     */
    public RecipeByIdRecord getRecipeById(int id) {
        Request<RecipeByIdResponse> response = recipeApiClient.getRecipeById(id);
        int responseCode = response.getResponseCode();
        if (responseCode != 200) {
            LOG.error("Error: getRecipeById doesn't return response, code: " + responseCode);
            return null;
        }

        return response.getApiResponse().recipe();



    }
}
