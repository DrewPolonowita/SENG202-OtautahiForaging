package seng202.team4.models.api.Recipe.SearchRecipe;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents the list of recipes returned itself
 * @param recipe
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeSearchDetails(
        @JsonProperty("total_results") int totalResult,
        @JsonProperty("recipe") List<RecipeSearchRecord> recipe
) {
}
