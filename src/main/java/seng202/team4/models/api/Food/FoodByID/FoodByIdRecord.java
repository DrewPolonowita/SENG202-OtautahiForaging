package seng202.team4.models.api.Food.FoodByID;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 *
 * @param foodId
 * @param foodName
 * @param servings
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FoodByIdRecord(
        @JsonProperty("food_id") int foodId,
        @JsonProperty("food_name") String foodName,
        @JsonProperty("servings") FoodServings servings
) {
}
