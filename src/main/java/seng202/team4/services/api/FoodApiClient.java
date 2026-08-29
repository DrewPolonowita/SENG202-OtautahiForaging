package seng202.team4.services.api;

import org.apache.commons.lang3.NotImplementedException;
import seng202.team4.models.api.Food.FoodByID.FoodByIdResponse;
import seng202.team4.models.api.Food.SearchFood.FoodSearchResponse;
import seng202.team4.models.api.Request;

public class FoodApiClient implements FoodClientInterface {

    static final String BASE_URL = "https://platform.fatsecret.com/rest/foods";
    static final String RECIPE_BY_ID = BASE_URL + "/recipe/v2";
    static final String RECIPE_BY_SEARCH = BASE_URL + "/recipes/search/v3";
    static final String FOOD_BY_ID = BASE_URL + "/food/v5";
    static final String FOOD_BY_SEARCH = BASE_URL + "/foods/search/v1";

    private final ApiClientInterface apiClient;

    public FoodApiClient(ApiClientInterface apiClient){
        this.apiClient = apiClient;
    }

    private String encodeFoodSearchParams(String searchExpression, int maxResults) {
        throw new NotImplementedException();
    }

    @Override
    public Request<FoodSearchResponse> searchFood(String searchExpression, int maxResults) {

        String url = FOOD_BY_ID;
        throw new NotImplementedException();

    }


    @Override
    public Request<FoodByIdResponse> getFoodById(int foodId) {
        throw new NotImplementedException();

    }




}
