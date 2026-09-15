package seng202.team4.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.Recipe;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdRecord;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdResponse;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeDirection;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeIngredient;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchRecord;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchResponse;
import seng202.team4.models.api.Request;
import seng202.team4.services.api.RecipeClientInterface;

import java.util.ArrayList;
import java.util.Comparator;
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
    public Recipe getRecipeById(int id) {
        Request<RecipeByIdResponse> response = recipeApiClient.getRecipeById(id);
        int responseCode = response.getResponseCode();
        if (responseCode != 200) {
            LOG.error("Error: getRecipeById doesn't return response, code: " + responseCode);
            return null;
        }

        Recipe createdRecipe = createRecipeObjectById(response.getApiResponse().recipe());
        return createdRecipe;



    }

    /**
     * Creates a recipe using a RecipeByIdRecord
     * @param recipeRecord a RecipeByIdRecord
     * @return a Recipe model
     */
    private Recipe createRecipeObjectById(RecipeByIdRecord recipeRecord) {
        int recipeId = recipeRecord.recipeId();
        String recipeName = recipeRecord.recipeName();
        String method = buildMethodString(recipeRecord.directions());
        List<String> ingredients = getIngredientString(recipeRecord.ingredients());




        double carbohydrates = recipeRecord.servingSizes().serving().carbohydrate();
        double calories = recipeRecord.servingSizes().serving().calories();
        double protein = recipeRecord.servingSizes().serving().protein();
        double fat = recipeRecord.servingSizes().serving().fat();
        String description = recipeRecord.recipeDescription();
        String imageUrl = recipeRecord.imageUrl();
        int cookingTime = recipeRecord.cookingTime();
        int preparationTime = recipeRecord.preparationTime();


        return new Recipe(
                recipeId,
                recipeName,
                method,
                ingredients,
                carbohydrates,
                calories,
                protein,
                fat,
                description,
                null,
                imageUrl,
                cookingTime,
                preparationTime
        );
    }


    private List<String> getIngredientString(List<RecipeIngredient> ingredients) {
        List<String> ingredientStrings = new ArrayList<>();
        for (RecipeIngredient ingredient : ingredients) {
            ingredientStrings.add(ingredient.ingredientDescription());
        }
        return ingredientStrings;
    }


    /**
     * Method to build string of recipe method with steps each on a new line
     * @param directions a list of RecipeDirection models
     * @return a string containing the recipe's method
     */
    private String buildMethodString(List<RecipeDirection> directions) {
        directions.sort(Comparator.comparing(RecipeDirection::directionNumber));
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < directions.size(); i++) {
            stringBuilder.append(directions.get(i).directionNumber());
            stringBuilder.append(": ").append(directions.get(i).directionDescription()).append("\n");
        }
        return stringBuilder.toString();
    }

}
