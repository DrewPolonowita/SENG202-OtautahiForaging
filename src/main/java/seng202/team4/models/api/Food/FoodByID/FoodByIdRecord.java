package seng202.team4.models.api.Food.FoodByID;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 *
 * @param foodId the unique id for a food item from the API
 * @param foodName the name of the food item from the API
 * @param servings amount of servings returned from the API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FoodByIdRecord(
        @JsonProperty("food_id") int foodId,
        @JsonProperty("food_name") String foodName,
        @JsonProperty("servings") FoodServings servings
) {
}
