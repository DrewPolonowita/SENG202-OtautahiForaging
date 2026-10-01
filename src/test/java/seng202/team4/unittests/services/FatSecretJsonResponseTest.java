package seng202.team4.unittests.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.models.api.Food.FoodByID.FoodByIdResponse;
import seng202.team4.models.api.Food.SearchFood.FoodSearchResponse;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdResponse;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchResponse;
import seng202.team4.models.api.Request;
import seng202.team4.services.api.ApiClientInterface;
import seng202.team4.services.api.FoodApiClient;
import seng202.team4.services.api.FoodClientInterface;
import seng202.team4.services.api.OAuth1Signature;
import seng202.team4.services.api.RecipeApiClient;
import seng202.team4.services.api.RecipeClientInterface;

import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FatSecretJsonResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    InputStream chickenJson;
    InputStream cheeriosJson;
    InputStream cheeseburgerJson;
    InputStream mushroomsJson;
    InputStream idBakedLemonSnapperJson;
    InputStream searchBakedLemonSnapperJson;


    OAuth1Signature oauth1Signature;
    ApiClientInterface apiClient;
    FoodClientInterface foodApiClient;
    RecipeClientInterface recipeApiClient;

    @BeforeEach
    void setUpTest() {
        apiClient = mock(ApiClientInterface.class);
        oauth1Signature = mock(OAuth1Signature.class);

        foodApiClient = new FoodApiClient(apiClient, oauth1Signature);
        recipeApiClient = new RecipeApiClient(apiClient, oauth1Signature);
        chickenJson = getClass()
                .getResourceAsStream("/fatsecretresponses/food-by-id-chicken.json");
        cheeriosJson = getClass()
                .getResourceAsStream("/fatsecretresponses/food-by-id-cheerios.json");
        cheeseburgerJson = getClass()
                .getResourceAsStream("/fatsecretresponses/search-food-cheeseburger.json");
        mushroomsJson = getClass()
                .getResourceAsStream("/fatsecretresponses/search-food-mushrooms.json");
        idBakedLemonSnapperJson = getClass()
                .getResourceAsStream("/fatsecretresponses/recipe-by-id-baked-lemon-snapper.json");
        searchBakedLemonSnapperJson = getClass()
                .getResourceAsStream("/fatsecretresponses/search-recipe-baked-lemon-snapper.json");

        when(oauth1Signature.createSignature(anyString(), anyString(), anyMap()))
                .thenReturn("OAuth test");
    }

    @Test
    void ChickenFoodByIdBecomesCorrectRecords() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(chickenJson);

        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }

        FoodByIdResponse foodByIdResponse1 = objectMapper.readValue(
                stringBuilder.toString(),
                new TypeReference<FoodByIdResponse>() {}
        );

        Request<FoodByIdResponse> fakeRequest1 = new Request<>(foodByIdResponse1, 200);


        when(apiClient.getOauth(
                eq("https://platform.fatsecret.com/rest/food/v5?food_id=1641&format=json"),
                any(TypeReference.class),
                eq("OAuth test")
        )).thenReturn(fakeRequest1);

        Request<FoodByIdResponse> actualRequest1 = foodApiClient.getFoodById(1641);

        assertEquals(200, actualRequest1.getResponseCode());
        assertEquals(1641, actualRequest1.getApiResponse().food().foodId());
        assertEquals(5034, actualRequest1.getApiResponse().food().servings().servings().get(1).servingId());
        assertEquals(164, actualRequest1.getApiResponse().food().servings().servings().get(1).calories());
        assertEquals("Chicken Breast", actualRequest1.getApiResponse().food().foodName());
    }

    @Test
    void BakedLemonSnapperRecipeByIdBecomesCorrectRecords() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(idBakedLemonSnapperJson);

        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }

        RecipeByIdResponse recipeByIdResponse = objectMapper.readValue(
                stringBuilder.toString(),
                new TypeReference<RecipeByIdResponse>() {}
        );

        Request<RecipeByIdResponse> fakeRequest = new Request<>(recipeByIdResponse, 200);


        when(apiClient.getOauth(
                eq("https://platform.fatsecret.com/rest/recipe/v2?recipe_id=91&format=json"),
                any(TypeReference.class),
                eq("OAuth test")
        )).thenReturn(fakeRequest);

        Request<RecipeByIdResponse> actualRequest = recipeApiClient.getRecipeById(91);

        assertEquals(200, actualRequest.getResponseCode());
        assertEquals(91, actualRequest.getApiResponse().recipe().recipeId());
        assertEquals("Baked Lemon Snapper", actualRequest.getApiResponse().recipe().recipeName());
        assertEquals(177, actualRequest.getApiResponse().recipe().servingSizes().serving().calories());
    }

    @Test
    void CheeriosFoodByIdBecomesCorrectRecords() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(cheeriosJson);

        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }

        FoodByIdResponse foodByIdResponse = objectMapper.readValue(
                stringBuilder.toString(),
                new TypeReference<FoodByIdResponse>() {}
        );

        Request<FoodByIdResponse> fakeRequest = new Request<>(foodByIdResponse, 200);
        when(apiClient.getOauth(
                eq("https://platform.fatsecret.com/rest/food/v5?food_id=50953&format=json"),
                any(TypeReference.class),
                eq("OAuth test")
        )).thenReturn(fakeRequest);
        Request<FoodByIdResponse> actualRequest = foodApiClient.getFoodById(50953);

        assertEquals(200, actualRequest.getResponseCode());
        assertEquals(50953, actualRequest.getApiResponse().food().foodId());
        assertEquals("Whole Grain Cheerios", actualRequest.getApiResponse().food().foodName());
    }
    @Test
    void CheeseburgerSearchFoodBecomesCorrectRecords() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(cheeseburgerJson);

        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }
        FoodSearchResponse foodSearchResponse = objectMapper.readValue(
                stringBuilder.toString(),
                new TypeReference<FoodSearchResponse>() {
                }
        );
        Request<FoodSearchResponse> fakeRequest = new Request<>(foodSearchResponse, 200);
        when(apiClient.getOauth(
                eq("https://platform.fatsecret.com/rest/foods/search/v1?search_expression=Cheeseburger&max_results=1&format=json"),
                any(TypeReference.class),
                eq("OAuth test")
        )).thenReturn(fakeRequest);

        Request<FoodSearchResponse> actualResponse = foodApiClient.searchFood("Cheeseburger", 1);

        assertEquals(200, actualResponse.getResponseCode());
        assertEquals(41963, actualResponse.getApiResponse().foods().food().get(0).foodId());
        assertEquals(220, actualResponse.getApiResponse().foods().totalResults());
        assertEquals("Cheeseburger", actualResponse.getApiResponse().foods().food().get(0).foodName());
    }

    void MushroomsSearchFoodBecomesCorrectRecords() throws Exception {
        FoodByIdResponse foodByIdResponse = objectMapper.readValue(
                mushroomsJson,
                FoodByIdResponse.class
        );
        Request<FoodByIdResponse> fakeRequest = new Request<>(foodByIdResponse, 200);
        when(apiClient.getOauth(
                eq("https://platform.fatsecret.com/rest/food/v5?food_id=50953&format=json"),
                any(TypeReference.class),
                eq("OAuth test")
        )).thenReturn(fakeRequest);
    }


    @Test
    void BakedLemonSnapperSearchRecipeBecomesCorrectRecords() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(searchBakedLemonSnapperJson);

        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }
        RecipeSearchResponse recipeSearchResponse = objectMapper.readValue(
                stringBuilder.toString(),
                new TypeReference<RecipeSearchResponse>() {
                }
        );
        Request<RecipeSearchResponse> fakeRequest = new Request<>(recipeSearchResponse, 200);
        when(apiClient.getOauth(
                eq("https://platform.fatsecret.com/rest/recipes/search/v3?search_expression=Baked%20Lemon%20Snapper&must_have_images=false&max_results=1&format=json"),
                any(TypeReference.class),
                eq("OAuth test")
        )).thenReturn(fakeRequest);

        Request<RecipeSearchResponse> actualResponse = recipeApiClient.searchRecipe("Baked Lemon Snapper", false, 1);

        assertEquals(200, actualResponse.getResponseCode());
        assertEquals(91, actualResponse.getApiResponse().recipes().recipe().get(0).id());
        assertEquals("Baked Lemon Snapper", actualResponse.getApiResponse().recipes().recipe().get(0).recipeName());
        assertEquals(177, actualResponse.getApiResponse().recipes().recipe().get(0).nutritionalInformation().calories());

    }


}
