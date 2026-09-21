package seng202.team4.models.api.Recipe.SearchRecipe;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import seng202.team4.models.api.Recipe.RecipeNutrition;


/**
 * Represents an element of the returned list of recipes
 * @param id recipes id from the API
 * @param recipeName recipes name from the API
 * @param recipeDescription recipes description from the API
 * @param nutritionalInformation recipes nutritional information from the API
 * @param ingredients recipes ingredients from the API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeSearchRecord(
        @JsonProperty("recipe_id") int id,
        @JsonProperty("recipe_name") String recipeName,
        @JsonProperty("recipe_description") String recipeDescription,
        @JsonProperty("recipe_nutrition") RecipeNutrition nutritionalInformation,
        @JsonProperty("recipe_ingredients") RecipeIngredients ingredients
        ) {
}
