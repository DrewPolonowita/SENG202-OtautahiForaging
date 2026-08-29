package seng202.team4.models.api.Recipe;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeRecord (
        @JsonProperty Long id,
        @JsonProperty String recipeName,
        @JsonProperty String recipeDescription,
        @JsonProperty Nutrition nutritionalInformation,
        @JsonProperty List<String> ingredient
        ){
}
