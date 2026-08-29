package seng202.team4.models.api.Food.SearchFood;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FoodSearchRecord(
        @JsonProperty("food_id") int foodId,
        @JsonProperty("food_name") String foodName,
        @JsonProperty("food_description") String foodDescription
) {

}
