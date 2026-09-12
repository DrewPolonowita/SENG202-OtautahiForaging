package seng202.team4.models.api.Recipe.SearchRecipe;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RecipeIngredients(
        @JsonProperty("ingredient") List<String> ingredients
) {
}
