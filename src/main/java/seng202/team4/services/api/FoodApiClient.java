package seng202.team4.services.api;

import com.fasterxml.jackson.core.type.TypeReference;

import io.github.cdimascio.dotenv.Dotenv;
import seng202.team4.models.api.ApiResponseWrapper;
import seng202.team4.models.api.Food.FoodByID.FoodByIdResponse;
import seng202.team4.models.api.Food.SearchFood.FoodSearchResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
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
        if (consumerKey == null || secretKey == null) {
            throw new IllegalStateException("FatSecret API credentials are missing");
        }
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
     * Encode the parameters into url query string
     * @param params parameters to encode
     * @return encoded query string
     */
    private String encodeParams(Map<String, String> params) {
        StringBuilder result = new StringBuilder("?");

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (result.length() > 1) {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)
                    .replace("+", "%20"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8)
                    .replace("+", "%20"));
        }

        return result.toString();
    }

    /**
     * Method to get the request object specifically for the search food function, with data, from the api
     * @param searchExpression expression to search the api with
     * @param maxResults maximum results from api
     * @return Request object with the data
     */
    @Override
    public ApiResponseWrapper<FoodSearchResponse> searchFood(String searchExpression, int maxResults) {
        authenticateSearchFood(searchExpression, maxResults);

        Map<String, String> params = new LinkedHashMap<>();
        params.put("search_expression", searchExpression);
        params.put("max_results", String.valueOf(maxResults));
        params.put("format", "json");

        String authenticatedString = oauth1Signature.createSignature("GET", FOOD_BY_SEARCH, params);
        String url = FOOD_BY_SEARCH + encodeParams(params);


        return apiClient.get(url, new TypeReference<FoodSearchResponse>() {
        }, authenticatedString);
    }

    /**
     * Authenticate params for search food
     * @param searchExpression expression to not be empty
     * @param maxResults max results to be greater than 0
     */
    private static void authenticateSearchFood(String searchExpression, int maxResults) {
        if (searchExpression == null || searchExpression.isBlank()) {
            throw new IllegalArgumentException("Search Expression can't be empty");
        }

        if (maxResults <= 0) {
            throw new IllegalArgumentException("maxResults must be greater than 0");
        }
    }
    /**
     * Method to get the request object specifically for the get food by id function with data from the api
     * @param foodId id of the specific food object being requested
     * @return Request object with the data
     */
    @Override
    public ApiResponseWrapper<FoodByIdResponse> getFoodById(int foodId) {

        Map<String, String> params = new LinkedHashMap<>();
        params.put("food_id", String.valueOf(foodId));
        params.put("format", "json");
        String authenticatedString = oauth1Signature.createSignature("GET", FOOD_BY_ID, params);

        String url = FOOD_BY_ID + encodeParams(params);
        return apiClient.get(url, new TypeReference<FoodByIdResponse>() {
        }, authenticatedString);
    }




}

