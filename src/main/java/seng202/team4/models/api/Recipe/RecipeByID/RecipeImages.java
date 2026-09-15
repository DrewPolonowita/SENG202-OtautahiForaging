package seng202.team4.models.api.Recipe.RecipeByID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Stores information about recipe images
 * @param imageUrl the url for the image of a recipe from the API
 */
public record RecipeImages (
        @JsonProperty("recipe_image") String imageUrl
) {
}




