package seng202.team4.models.api.Recipe.RecipeByID;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @param recipeId
 * @param recipeName
 * @param recipeDescription
 * @param servingSizes Acts as holding the servings on fatsecret API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeByIdRecord(
        @JsonProperty("recipe_id") int recipeId,
        @JsonProperty("recipe_name") String recipeName,
        @JsonProperty("recipe_description") String recipeDescription,
        @JsonProperty("serving_sizes") RecipeServingSizes servingSizes
){
}
