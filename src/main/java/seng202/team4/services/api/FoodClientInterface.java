package seng202.team4.services.api;

import seng202.team4.models.api.Food.FoodByID.FoodByIdResponse;
import seng202.team4.models.api.Food.SearchFood.FoodSearchResponse;
import seng202.team4.models.api.Request;

public interface FoodClientInterface {

    Request<FoodSearchResponse> searchFood(String searchExpression, int maxResults);

    Request<FoodByIdResponse> getFoodById(int foodId);
}
