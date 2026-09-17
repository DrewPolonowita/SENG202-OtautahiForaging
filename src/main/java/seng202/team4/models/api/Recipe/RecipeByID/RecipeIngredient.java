package seng202.team4.models.api.Recipe.RecipeByID;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 *
 * @param ingredientId ingredient ID from API
 * @param ingredientName name of the ingredient from API
 * @param servingId id for a particular portion or serving of a food item API
 * @param ingredientUnitNumber the number part of a measurement without the unit (e.g. 100 in 100g)
 * @param ingredientMeasurementDescription the unit part of a measurement without the number (e.g. g in 100g)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipeIngredient(
        @JsonProperty("food_id") int ingredientId,
        @JsonProperty("food_name") String ingredientName,
        @JsonProperty("serving_id") int servingId,
        @JsonProperty("number_of_units") double ingredientUnitNumber,
        @JsonProperty("measurement_description") String ingredientMeasurementDescription,
        @JsonProperty("ingredient_description") String ingredientDescription
) {
}
