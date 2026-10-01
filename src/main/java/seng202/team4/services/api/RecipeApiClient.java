package seng202.team4.services.api;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.cdimascio.dotenv.Dotenv;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdResponse;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchResponse;
import seng202.team4.models.api.Request;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
        Dotenv dotenv = Dotenv.configure().load();
        String consumerKey = dotenv.get("CONSUMER_KEY");
        String secretKey = dotenv.get("CONSUMER_SECRET");
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
     * Url building for recipe search
     * @param searchExpression search expression to get recipes from FatSecret
     * @param mustHaveImages should the response be guaranteed to provide images
     * @param maxResults max search results
     * @return url search parameter string
     */
    private String encodeRecipeSearchParams(String searchExpression, boolean mustHaveImages, int maxResults) {
        return "?search_expression=" + URLEncoder
                .encode(searchExpression == null ? "" : searchExpression, StandardCharsets.UTF_8)
                .replace("+", "%20")
                + "&must_have_images=" + mustHaveImages
                + "&max_results=" + maxResults
                + "&format=json";
    }

    /**
     * Url building for getting recipe by id
     * @param recipeId recipe id for fatsecret to return
     * @return url search parameter string
     */
    private String encodeRecipeByIdParams(int recipeId) {
        return "?recipe_id=" + recipeId + "&format=json";
    }

    /**
     * Method for use in the application to find specific recipe
     * @param recipeId recipe being searched for
     * @return request object including the Json and error codes
     */
    @Override
    public Request<RecipeByIdResponse> getRecipeById(int recipeId) {
        Map<String, String> params = new HashMap<>();
        params.put("recipe_id", String.valueOf(recipeId));
        params.put("format", "json");
        String authenticatedString = oauth1Signature.createSignature("GET", RECIPE_BY_ID, params);
        String url = RECIPE_BY_ID + encodeRecipeByIdParams(recipeId);
        return apiClient.getOauth(url, new TypeReference<RecipeByIdResponse>() {
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
    public Request<RecipeSearchResponse> searchRecipe(String searchExpression, boolean mustHaveImages, int maxResults) {
        Map<String, String> params = new HashMap<>();
        params.put("search_expression", searchExpression);
        params.put("must_have_images", String.valueOf(mustHaveImages));
        params.put("max_results", String.valueOf(maxResults));
        params.put("format", "json");
        String authenticatedString = oauth1Signature.createSignature("GET", RECIPE_BY_SEARCH, params);
        String url = RECIPE_BY_SEARCH + encodeRecipeSearchParams(searchExpression, mustHaveImages, maxResults);
        return apiClient.getOauth(url, new TypeReference<RecipeSearchResponse>() {
        }, authenticatedString);

    }

}
