package seng202.team4.models.api.Food.FoodByID;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @param servingId
 * @param servingDescription
 * @param calories Energy content in kcal
 * @param carbohydrate Total carbohydrate content in grams
 * @param protein Protein content in grams
 * @param fat Total fat in grams
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Serving(
    @JsonProperty("serving_id") int servingId,
    @JsonProperty("serving_description") String servingDescription,
    @JsonProperty("calories") double calories,
    @JsonProperty("carbohydrate") double carbohydrate,
    @JsonProperty("protein") double protein,
    @JsonProperty("fat") double fat
) {
}
