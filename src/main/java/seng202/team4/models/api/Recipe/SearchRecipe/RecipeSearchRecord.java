package seng202.team4.models.api.Recipe.SearchRecipe;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import seng202.team4.models.api.Recipe.RecipeNutrition;


/**
 * Represents an element of the returned list of recipes
 * @param id
 * @param recipeName
 * @param recipeDescription
 * @param nutritionalInformation
 * @param ingredients
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeSearchRecord(
        @JsonProperty("recipe_id") int id,
        @JsonProperty("recipe_name") String recipeName,
        @JsonProperty("recipe_description") String recipeDescription,
        @JsonProperty("recipe_nutrition") RecipeNutrition nutritionalInformation,
        @JsonProperty("recipe_ingredients") RecipeIngredients ingredients
        ){
}
