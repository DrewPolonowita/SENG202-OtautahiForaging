package seng202.team4.models.api.Recipe.RecipeByID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Stores information about a recipes method from an API request
 * @param direction stores directions as a list
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeDirections(
        @JsonProperty("direction") List<RecipeDirection> direction
) {
}
