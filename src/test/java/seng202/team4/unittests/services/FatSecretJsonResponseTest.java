package seng202.team4.unittests.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.models.api.ApiResponseWrapper;
import seng202.team4.models.api.Food.FoodByID.FoodByIdResponse;
import seng202.team4.models.api.Food.SearchFood.FoodSearchResponse;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdResponse;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchResponse;
import seng202.team4.services.api.ApiClientInterface;
import seng202.team4.services.api.FoodApiClient;
import seng202.team4.services.api.FoodClientInterface;
import seng202.team4.services.api.OAuth1Signature;
import seng202.team4.services.api.RecipeApiClient;
import seng202.team4.services.api.RecipeClientInterface;

import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

        ApiResponseWrapper<FoodByIdResponse> fakeApiResponseWrapper1 = new ApiResponseWrapper<>(foodByIdResponse1, 200);


        when(apiClient.get(
                eq("https://platform.fatsecret.com/rest/food/v5?food_id=1641&format=json"),
                any(TypeReference.class),
                eq("OAuth test")
        )).thenReturn(fakeApiResponseWrapper1);

        ApiResponseWrapper<FoodByIdResponse> actualApiResponseWrapper1 = foodApiClient.getFoodById(1641);

        assertEquals(200, actualApiResponseWrapper1.getResponseCode());
        assertEquals(1641, actualApiResponseWrapper1.getApiResponse().food().foodId());
        assertEquals(5034, actualApiResponseWrapper1.getApiResponse().food().servings().servings().get(1).servingId());
        assertEquals(164, actualApiResponseWrapper1.getApiResponse().food().servings().servings().get(1).calories());
        assertEquals("Chicken Breast", actualApiResponseWrapper1.getApiResponse().food().foodName());
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

        ApiResponseWrapper<RecipeByIdResponse> fakeApiResponseWrapper = new ApiResponseWrapper<>(recipeByIdResponse, 200);


        when(apiClient.get(
                eq("https://platform.fatsecret.com/rest/recipe/v2?recipe_id=91&format=json"),
                any(TypeReference.class),
                eq("OAuth test")
        )).thenReturn(fakeApiResponseWrapper);

        ApiResponseWrapper<RecipeByIdResponse> actualApiResponseWrapper = recipeApiClient.getRecipeById(91);

        assertEquals(200, actualApiResponseWrapper.getResponseCode());
        assertEquals(91, actualApiResponseWrapper.getApiResponse().recipe().recipeId());
        assertEquals("Baked Lemon Snapper", actualApiResponseWrapper.getApiResponse().recipe().recipeName());
        assertEquals(177, actualApiResponseWrapper.getApiResponse().recipe().servingSizes().serving().calories());
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

        ApiResponseWrapper<FoodByIdResponse> fakeApiResponseWrapper = new ApiResponseWrapper<>(foodByIdResponse, 200);
        when(apiClient.get(
                eq("https://platform.fatsecret.com/rest/food/v5?food_id=50953&format=json"),
                any(TypeReference.class),
                eq("OAuth test")
        )).thenReturn(fakeApiResponseWrapper);
        ApiResponseWrapper<FoodByIdResponse> actualApiResponseWrapper = foodApiClient.getFoodById(50953);

        assertEquals(200, actualApiResponseWrapper.getResponseCode());
        assertEquals(50953, actualApiResponseWrapper.getApiResponse().food().foodId());
        assertEquals("Whole Grain Cheerios", actualApiResponseWrapper.getApiResponse().food().foodName());
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
        ApiResponseWrapper<FoodSearchResponse> fakeApiResponseWrapper = new ApiResponseWrapper<>(foodSearchResponse, 200);
        when(apiClient.get(
                eq("https://platform.fatsecret.com/rest/foods/search/v1?search_expression=Cheeseburger&max_results=1&format=json"),
                any(TypeReference.class),
                eq("OAuth test")
        )).thenReturn(fakeApiResponseWrapper);

        ApiResponseWrapper<FoodSearchResponse> actualResponse = foodApiClient.searchFood("Cheeseburger", 1);

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
        ApiResponseWrapper<FoodByIdResponse> fakeApiResponseWrapper = new ApiResponseWrapper<>(foodByIdResponse, 200);
        when(apiClient.get(
                eq("https://platform.fatsecret.com/rest/food/v5?food_id=50953&format=json"),
                any(TypeReference.class),
                eq("OAuth test")
        )).thenReturn(fakeApiResponseWrapper);
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
        ApiResponseWrapper<RecipeSearchResponse>
                fakeApiResponseWrapper = new ApiResponseWrapper<>(recipeSearchResponse, 200);
        when(apiClient.get(
                eq("https://platform.fatsecret.com/rest/recipes/search/v3?search_expression=Baked%20Lemon%20Snapper&must_have_images=false&max_results=1&format=json"),
                any(TypeReference.class),
                eq("OAuth test")
        )).thenReturn(fakeApiResponseWrapper);

        ApiResponseWrapper<RecipeSearchResponse>
                actualResponse = recipeApiClient.searchRecipe("Baked Lemon Snapper", false, 1);

        assertEquals(200, actualResponse.getResponseCode());
        assertEquals(91, actualResponse.getApiResponse().recipes().recipe().get(0).id());
        assertEquals("Baked Lemon Snapper", actualResponse.getApiResponse().recipes().recipe().get(0).recipeName());
        assertNotNull(actualResponse.getApiResponse().recipes().recipe().getFirst().recipeImageUrl());
    }


}
