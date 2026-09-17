package seng202.team4.models.api.Recipe.RecipeByID;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Stores information about recipes from an API request
 *
 * @param recipeId the unique id number of a recipe
 * @param recipeName the name of the recipe
 * @param recipeDescription a description of the recipe
 * @param servingSizes Acts as holding the servings on fatsecret API
 * @param directions the recipes method as a list of recipeDirection objects
 * @param ingredients the ingredients in the recipe as a list of recipeIngredient objects
 * @param cookingTime the cooking time for the recipe in minutes
 * @param preparationTime the preparation time for the recipe in minutes
 * @param images the string url for a recipe
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeByIdRecord(
        @JsonProperty("recipe_id") int recipeId,
        @JsonProperty("recipe_name") String recipeName,
        @JsonProperty("recipe_description") String recipeDescription,
        @JsonProperty("serving_sizes") RecipeServingSizes servingSizes,
        @JsonProperty("directions") RecipeDirections directions,
        @JsonProperty("ingredients") RecipeIngredients ingredients,
        @JsonProperty("preparation_time_min") int preparationTime,
        @JsonProperty("cooking_time_min") int cookingTime,
        @JsonProperty("recipe_images") RecipeImages images
) {
}
