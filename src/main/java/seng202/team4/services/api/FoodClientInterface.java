package seng202.team4.services.api;

import seng202.team4.models.api.ApiResponseWrapper;
import seng202.team4.models.api.Food.FoodByID.FoodByIdResponse;
import seng202.team4.models.api.Food.SearchFood.FoodSearchResponse;

/**
 * Interface to call the FatSecret api for food information
 */
public interface FoodClientInterface {

    /**
     * Method to get the request object specifically for the search food function, with data, from the api
     * @param searchExpression expression to search the api with
     * @param maxResults maximum results from api
     * @return {@link ApiResponseWrapper<FoodSearchResponse>} wrapper with data from api
     */
    ApiResponseWrapper<FoodSearchResponse> searchFood(String searchExpression, int maxResults);

    /**
     * Method to get the request object specifically for the get food by id function with data from the api
     * @param foodId id of the specific food object being requested
     * @return {@link ApiResponseWrapper<FoodSearchResponse>} wrapper with data from api
     */
    ApiResponseWrapper<FoodByIdResponse> getFoodById(int foodId);
}
