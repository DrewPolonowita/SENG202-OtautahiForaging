package seng202.team4.models.api.Recipe.RecipeByID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Stores information about a recipes method from an API request
 * @param ingredient stores a list of ingredient
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeIngredients(
        @JsonProperty("ingredient") List<RecipeIngredient> ingredient
) {
}