package seng202.team4.models.api.Recipe.RecipeByID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Stores information about recipes from id
 *
 * @param recipe the unique recipe
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeByIdResponse(
        @JsonProperty("recipe") RecipeByIdRecord recipe
) {

}
