package seng202.team4.models.api.Recipe.SearchRecipe;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import seng202.team4.models.api.FatSecretError;

/**
 * Top level record from json response
 * @param recipes
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeSearchResponse(
        @JsonProperty("recipes") RecipeSearchDetails recipes,
        @JsonProperty("error") FatSecretError error
        ) {
}
