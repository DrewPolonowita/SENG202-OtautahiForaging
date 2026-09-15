package seng202.team4.models.api.Recipe.RecipeByID;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Stores information about a recipes method from an API request
 * @param directionNumber the step number within a recipe method, beginning from 1
 * @param directionDescription the instruction associated with the direction
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeDirection(
        @JsonProperty("direction_number") int directionNumber,
        @JsonProperty("direction_description") String directionDescription
) {
}
