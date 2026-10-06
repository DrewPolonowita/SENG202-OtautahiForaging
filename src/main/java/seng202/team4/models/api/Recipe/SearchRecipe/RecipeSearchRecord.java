package seng202.team4.models.api.Recipe.SearchRecipe;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents an element of the returned list of recipes
 * @param id recipes id from the API
 * @param recipeName recipes name from the API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeSearchRecord(
        @JsonProperty("recipe_id") int id,
        @JsonProperty("recipe_name") String recipeName,
        @JsonProperty("recipe_image") String recipeImageUrl
        ) {
}
