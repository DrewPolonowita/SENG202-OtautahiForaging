package seng202.team4.unittests.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.models.api.Food.FoodByID.FoodByIdRecord;
import seng202.team4.models.api.Food.FoodByID.FoodByIdResponse;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdResponse;
import seng202.team4.models.api.Request;
import seng202.team4.services.api.ApiClientInterface;
import seng202.team4.services.api.FoodApiClient;
import seng202.team4.services.api.FoodClientInterface;
import seng202.team4.services.api.OAuth1Signature;
import seng202.team4.services.api.RecipeApiClient;
import seng202.team4.services.api.RecipeClientInterface;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FatSecretJsonResponseTest {

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
    public void setUpTest() {
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
    public void ChickenFoodByIdBecomesCorrectRecords() throws Exception {
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
        assertEquals(5034, actualRequest1.getApiResponse().food().servings().serving().get(1).servingId());
        assertEquals(164, actualRequest1.getApiResponse().food().servings().serving().get(1).calories());
        assertEquals("Chicken Breast", actualRequest1.getApiResponse().food().foodName());
    }

    @Test
    public void BakedLemonSnapperRecipeByIdBecomesCorrectRecords() throws Exception {
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
    public void CheeriosFoodByIdBecomesCorrectRecords() throws Exception {
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

    public void CheeseburgerSearchFoodBecomesCorrectRecords() throws Exception {
        FoodByIdResponse foodByIdResponse = objectMapper.readValue(
                cheeseburgerJson,
                FoodByIdResponse.class
        );
        Request<FoodByIdResponse> fakeRequest = new Request<>(foodByIdResponse, 200);
        when(apiClient.getOauth(
                eq("https://platform.fatsecret.com/rest/foods/search/v1?search_expression=Cheeseburger"),
                any(TypeReference.class),
                eq("OAuth test")
        )).thenReturn(fakeRequest);
    }

    public void MushroomsSearchFoodBecomesCorrectRecords() throws Exception {
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



    public void BakedLemonSnapperSearchRecipeBecomesCorrectRecords() throws Exception {
        FoodByIdResponse foodByIdResponse = objectMapper.readValue(
                searchBakedLemonSnapperJson,
                FoodByIdResponse.class
        );
        Request<FoodByIdResponse> fakeRequest = new Request<>(foodByIdResponse, 200);
        when(apiClient.getOauth(
                eq("https://platform.fatsecret.com/rest/food/v5?food_id=50953&format=json"),
                any(TypeReference.class),
                eq("OAuth test")
        )).thenReturn(fakeRequest);
    }


}
