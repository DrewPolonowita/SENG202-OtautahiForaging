package seng202.team4.models.api.Recipe.RecipeByID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Stores information about recipe images
 * @param imageUrl the url for the image of a recipe from the API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeImages(
        @JsonProperty("recipe_image") List<String> imageUrl
) {
}




