package seng202.team4.unittests.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.models.api.Food.FoodByID.FoodByIdRecord;
import seng202.team4.models.api.Food.FoodByID.FoodByIdResponse;
import seng202.team4.models.api.Request;
import seng202.team4.services.api.ApiClientInterface;
import seng202.team4.services.api.FoodApiClient;
import seng202.team4.services.api.FoodClientInterface;
import seng202.team4.services.api.OAuth1Signature;

import java.io.InputStream;
import java.lang.reflect.Type;

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

    OAuth1Signature oauth1Signature;
    ApiClientInterface apiClient;
    FoodClientInterface foodApiClient;

    @BeforeEach
    public void setUpTest() {
        apiClient = mock(ApiClientInterface.class);
        oauth1Signature = mock(OAuth1Signature.class);

        foodApiClient = new FoodApiClient(apiClient, oauth1Signature);
        chickenJson = getClass()
                .getResourceAsStream("/fatsecretresponses/food-by-id-chicken.json");
        cheeriosJson = getClass()
                .getResourceAsStream("/fatsecretresponses/food-by-id-cheerios.json");
    }

    @Test
    public void exampleOneChickenFoodByIdBecomesCorrectRecords() throws Exception {
        FoodByIdResponse foodByIdResponse1 = objectMapper.readValue(
                chickenJson,
                FoodByIdResponse.class
        );

        Request<FoodByIdResponse> fakeRequest1 = new Request<>(foodByIdResponse1, 200);

        when(oauth1Signature.createSignature(anyString(), anyString(), anyMap()))
                .thenReturn("OAuth test");

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

    public void exampleTwoCheeriosFoodByIdBecomesCorrectRecords() throws Exception {
        FoodByIdResponse foodByIdResponse2 = objectMapper.readValue(
                cheeriosJson,
                FoodByIdResponse.class
        );
        Request<FoodByIdResponse> fakeRequest2 = new Request<>(foodByIdResponse2, 200);
        when(apiClient.getOauth(
                eq("https://platform.fatsecret.com/rest/food/v5?food_id=50953&format=json"),
                any(TypeReference.class),
                eq("OAuth test")
        )).thenReturn(fakeRequest2);
        Request<FoodByIdResponse> actualRequest2 = foodApiClient.getFoodById(50953);

        assertEquals(200, actualRequest2);

    }
}
