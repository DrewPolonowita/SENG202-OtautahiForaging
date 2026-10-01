package seng202.team4.services.api;

import com.fasterxml.jackson.core.type.TypeReference;

import io.github.cdimascio.dotenv.Dotenv;
import seng202.team4.models.api.Food.FoodByID.FoodByIdResponse;
import seng202.team4.models.api.Food.SearchFood.FoodSearchResponse;
import seng202.team4.models.api.Request;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to interact with the api for the purpose of getting food information from the FatSecret API
 */
public class FoodApiClient implements FoodClientInterface {
    static final String BASE_URL = "https://platform.fatsecret.com/rest";

    static final String FOOD_BY_ID = BASE_URL + "/food/v5";
    static final String FOOD_BY_SEARCH = BASE_URL + "/foods/search/v1";

    private final ApiClientInterface apiClient;
    private final OAuth1Signature oauth1Signature;

    /**
     * Constructor
     * @param apiClient interface for accessing api and handling https connection
     */
    public FoodApiClient(ApiClientInterface apiClient) {
        this.apiClient = apiClient;
        Dotenv dotenv = Dotenv.configure().load();
        String consumerKey = dotenv.get("CONSUMER_KEY");
        String secretKey = dotenv.get("CONSUMER_SECRET");
        this.oauth1Signature = new OAuth1Signature(consumerKey, secretKey);
    }

    /**
     * Strictly for testing
     * @param apiClient api client mock
     * @param oAuth1Signature fake oauth signature
     */
    public FoodApiClient(ApiClientInterface apiClient, OAuth1Signature oAuth1Signature) {
        this.apiClient = apiClient;
        this.oauth1Signature = oAuth1Signature;
    }

    /**
     * Building the parameters for food search URL
     * @param searchExpression expression for use in database
     * @param maxResults maximum results
     * @return url extension
     */
    private String encodeFoodSearchParams(String searchExpression, int maxResults) {
        return "?search_expression=" + URLEncoder
                .encode(searchExpression == null ? "" : searchExpression, StandardCharsets.UTF_8)
                .replace("+", "%20")
                + "&max_results=" + maxResults
                + "&format=json";
    }

    /**
     * Building the parameters for gettign food by id URL
     * @param foodId id to search for
     * @return url extension
     */
    private String encodeGetFoodByIdParams(int foodId) {
        return "?food_id=" + foodId + "&format=json";
    }

    /**
     * Method to get the request object specifically for the search food function, with data, from the api
     * @param searchExpression expression to search the api with
     * @param maxResults maximum results from api
     * @return Request object with the data
     */
    @Override
    public Request<FoodSearchResponse> searchFood(String searchExpression, int maxResults) {
        Map<String, String> params = new HashMap<>();
        params.put("search_expression", searchExpression);
        params.put("max_results", String.valueOf(maxResults));
        params.put("format", "json");
        String authenticatedString = oauth1Signature.createSignature("GET", FOOD_BY_SEARCH, params);
        String url = FOOD_BY_SEARCH + encodeFoodSearchParams(searchExpression, maxResults);
        return apiClient.getOauth(url, new TypeReference<FoodSearchResponse>() {
        }, authenticatedString);
    }

    /**
     * Method to get the request object specifically for the get food by id function with data from the api
     * @param foodId id of the specific food object being requested
     * @return Request object with the data
     */
    @Override
    public Request<FoodByIdResponse> getFoodById(int foodId) {

        Map<String, String> params = new HashMap<>();
        params.put("food_id", String.valueOf(foodId));
        params.put("format", "json");
        String authenticatedString = oauth1Signature.createSignature("GET", FOOD_BY_ID, params);
        String url = FOOD_BY_ID + encodeGetFoodByIdParams(foodId);
        return apiClient.getOauth(url, new TypeReference<FoodByIdResponse>() {
        }, authenticatedString);
    }




}

