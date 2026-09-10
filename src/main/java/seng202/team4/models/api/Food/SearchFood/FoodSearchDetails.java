package seng202.team4.models.api.Food.SearchFood;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FoodSearchDetails(
        @JsonProperty("food") List<FoodSearchRecord> food,
        @JsonProperty("total_results") int totalResults
) {
}
