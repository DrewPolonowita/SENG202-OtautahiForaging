package seng202.team4.models.api.Recipe.RecipeByID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import seng202.team4.models.api.Recipe.RecipeNutrition;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeServingSizes(
        @JsonProperty("serving") RecipeNutrition serving
) {

}
