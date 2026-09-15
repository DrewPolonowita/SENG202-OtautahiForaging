package seng202.team4.models.api.Food.FoodByID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FoodByIdResponse(
        @JsonProperty("food") FoodByIdRecord food
) {
}
