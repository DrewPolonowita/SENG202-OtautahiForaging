package seng202.team4.models.api.Recipe.RecipeByID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import seng202.team4.models.api.Recipe.RecipeNutrition;

/**
 * Stores information about servings from an API request
 *
 * @param serving the serving size
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeServingSizes(
        @JsonProperty("serving") RecipeNutrition serving
) {

}
