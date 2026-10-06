package seng202.team4.services.api;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import seng202.team4.models.api.ApiResponseWrapper;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdResponse;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Interface implementation to call the FatSecret api for the purpose of getting recipe information
 */
public class RecipeApiClient implements RecipeClientInterface {
    static final String BASE_URL = "https://platform.fatsecret.com/rest";
    static final String RECIPE_BY_ID = BASE_URL + "/recipe/v2";
    static final String RECIPE_BY_SEARCH = BASE_URL + "/recipes/search/v3";


    private final ApiClientInterface apiClient;
    private final OAuth1Signature oauth1Signature;

    /**
     * Constructor
     * @param apiClient apiClient for handling the connection
     */
    public RecipeApiClient(ApiClientInterface apiClient) {
        this.apiClient = apiClient;
        Dotenv dotenv;
        String consumerKey;
        String secretKey;
        try {
            dotenv = Dotenv.configure().load();
            consumerKey = dotenv.get("CONSUMER_KEY");
            secretKey = dotenv.get("CONSUMER_SECRET");
            if (consumerKey == null || secretKey == null) {
                throw new IllegalStateException("FatSecret API credentials are missing");
            }
        } catch (DotenvException e) {
            throw new IllegalStateException("Failed to load FatSecret API credentials", e);
        }
        this.oauth1Signature = new OAuth1Signature(consumerKey, secretKey);

    }

    /**
     * Strictly for testing
     * @param apiClient mock apiClient for faking the connection
     * @param oAuth1Signature fake authentication
     */
    public RecipeApiClient(ApiClientInterface apiClient, OAuth1Signature oAuth1Signature) {
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
     * Method for use in the application to find specific recipe
     * @param recipeId recipe being searched for
     * @return request object including the Json and error codes
     */
    @Override
    public ApiResponseWrapper<RecipeByIdResponse> getRecipeById(int recipeId) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("recipe_id", String.valueOf(recipeId));
        params.put("format", "json");
        String authenticatedString = oauth1Signature.createSignature("GET", RECIPE_BY_ID, params);
        String url = RECIPE_BY_ID + encodeParams(params);
        return apiClient.get(url, new TypeReference<RecipeByIdResponse>() {
        }, authenticatedString);
    }

    /**
     * Method for use in the application to search for recipes
     * @param searchExpression expression for fatsecret to use to match for recipes
     * @param mustHaveImages should the results be guaranteed to include images
     * @param maxResults maximum number of results
     * @return request object including the Json and error codes
     */
    @Override
    public ApiResponseWrapper<RecipeSearchResponse> searchRecipe(String searchExpression, boolean mustHaveImages, int maxResults) {
        validateSearchRecipe(searchExpression, maxResults);


        Map<String, String> params = new LinkedHashMap<>();
        params.put("search_expression", searchExpression);
        params.put("must_have_images", String.valueOf(mustHaveImages));
        params.put("max_results", String.valueOf(maxResults));
        params.put("format", "json");

        String authenticatedString = oauth1Signature.createSignature("GET", RECIPE_BY_SEARCH, params);
        String url = RECIPE_BY_SEARCH + encodeParams(params);
        return apiClient.get(url, new TypeReference<RecipeSearchResponse>() {
        }, authenticatedString);

    }

    /**
     * Validate parameters when searching recipes
     * @param searchExpression recipe search expression
     * @param maxResults max num results
     * @throws IllegalArgumentException if search expression is empty or results not greater than 0
     */
    private static void validateSearchRecipe(String searchExpression, int maxResults) throws IllegalArgumentException {
        if (searchExpression == null || searchExpression.isBlank()) {
            throw new IllegalArgumentException("Search expression cannot be empty");
        }

        if (maxResults <= 0) {
            throw new IllegalArgumentException("maxResults must be greater than 0");
        }
    }

}
