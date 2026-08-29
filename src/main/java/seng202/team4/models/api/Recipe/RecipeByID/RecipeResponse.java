package seng202.team4.models.api.Recipe.RecipeByID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeResponse(
        @JsonProperty("recipe") RecipeByIdRecord recipe
) {

}
