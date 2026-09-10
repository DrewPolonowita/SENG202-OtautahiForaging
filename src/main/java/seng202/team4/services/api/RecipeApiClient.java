package seng202.team4.services.api;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdResponse;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchResponse;
import seng202.team4.models.api.Request;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RecipeApiClient implements RecipeClientInterface {
    static final String BASE_URL = "https://platform.fatsecret.com/rest";
    private static final Logger log = LogManager.getLogger(FoodApiClient.class);
    static final String RECIPE_BY_ID = BASE_URL + "/recipe/v2";
    static final String RECIPE_BY_SEARCH = BASE_URL + "/recipes/search/v3";


    private final ApiClientInterface apiClient;
    private final OAuth1Signature oauth1Signature;

    public RecipeApiClient(ApiClientInterface apiClient) {
        this.apiClient = apiClient;
        Dotenv dotenv = Dotenv.configure().load();
        String consumerKey = dotenv.get("CONSUMER_KEY");
        String secretKey = dotenv.get("CONSUMER_SECRET");
        this.oauth1Signature = new OAuth1Signature(consumerKey, secretKey);
    }

    /**
     * testing
     * @param apiClient
     * @param oAuth1Signature
     */
    public RecipeApiClient(ApiClientInterface apiClient, OAuth1Signature oAuth1Signature) {
        this.apiClient = apiClient;
        this.oauth1Signature = oAuth1Signature;
    }


    private String encodeRecipeSearchParams(String searchExpression, boolean mustHaveImages, int maxResults) {
        return "?search_expression=" + URLEncoder
                .encode(searchExpression == null ? "" : searchExpression, StandardCharsets.UTF_8)
                .replace("+", "%20")
                + "&must_have_images=" + mustHaveImages
                + "&max_results=" + maxResults
                + "&format=json";
    }

    private String encodeRecipeByIdParams(int recipeId) {
        return "?recipe_id=" + recipeId + "&format=json";
    }

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
