package seng202.team4.models.api.Recipe;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @param calories Energy content in kcal
 * @param carbohydrate Total carbohydrate content in grams
 * @param protein Protein content in grams
 * @param fat Total fat in grams
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Nutrition(
        @JsonProperty float calories,
        @JsonProperty float carbohydrate,
        @JsonProperty float protein,
        @JsonProperty float fat
) {
}
