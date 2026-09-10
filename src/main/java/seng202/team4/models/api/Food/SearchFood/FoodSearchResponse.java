package seng202.team4.models.api.Food.SearchFood;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FoodSearchResponse(
        @JsonProperty("foods") FoodSearchDetails foods
    ) {
}
