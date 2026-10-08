package seng202.team4.models.api.Recipe.SearchRecipe;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents the list of recipes returned itself
 * @param recipe takes a recipe
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeSearchDetails(
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        @JsonProperty("total_results") int totalResult,
        @JsonProperty("recipe") List<RecipeSearchRecord> recipe
) {
}
