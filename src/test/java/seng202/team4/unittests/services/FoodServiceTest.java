package seng202.team4.unittests.services;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import seng202.team4.helpers.TestFoodData;
import seng202.team4.models.Food;
import seng202.team4.models.FoodServing;
import seng202.team4.models.api.ApiResponseWrapper;
import seng202.team4.models.api.Food.FoodByID.FoodByIdRecord;
import seng202.team4.models.api.Food.FoodByID.FoodByIdResponse;

import seng202.team4.models.api.Food.SearchFood.FoodSearchDetails;
import seng202.team4.models.api.Food.SearchFood.FoodSearchRecord;
import seng202.team4.models.api.Food.SearchFood.FoodSearchResponse;
import seng202.team4.repository.FoodRepository;
import seng202.team4.services.FoodMapper;
import seng202.team4.services.api.FoodClientInterface;
import seng202.team4.services.dataCentre.foodData.FoodService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static seng202.team4.helpers.TestFoodData.assertFoodEquals;

/**
 * mostly tests getFoods like the recipe service
 *
 * getFoods decides between database and api so it asks
 * Do we have the food id for the food name?
 * Do we have recent details for it?
 */
class FoodServiceTest {

    private FoodRepository testFoodRepository;
    private FoodClientInterface testFoodClient;
    private FoodMapper foodMapper;
    private FoodService testFoodService;

    private List<FoodServing> servings;
    private FoodByIdRecord foodRecord;
    private Food expectedFood;

    @BeforeEach
    void setUpTest() {
        testFoodRepository = mock(FoodRepository.class);
        testFoodClient = mock(FoodClientInterface.class);
        foodMapper = mock(FoodMapper.class);
        when(foodMapper.getFoodMap())
                .thenReturn(Map.of("Apple", List.of("Apple")));
        when(foodMapper.getTypeMap())
                .thenReturn(Map.of("Fruit", List.of("Apple")));
        when(foodMapper.getFoodType("Apple"))
                .thenReturn("Fruit");

        servings = TestFoodData.createDefaultServings();

        foodRecord = TestFoodData.createFakeFoodByIdRecord(
                123,
                "Apple",
                servings
        );

        expectedFood = TestFoodData.createFood(
                123,
                "Apple",
                "Fruit",
                servings
        );

        testFoodService = new FoodService(testFoodRepository, testFoodClient, foodMapper);
    }

    @Test
    void testFoodIdExistsAndDetailsUpToDate() {

        when(testFoodRepository.getFoodIdByFoodName("Apple"))
                .thenReturn(expectedFood.getId());
        when(testFoodRepository.foodDetailsUpToDateAndExist(expectedFood.getId()))
                .thenReturn(true);
        when(testFoodRepository.getOne(expectedFood.getId()))
                .thenReturn(expectedFood);

        List<Food> foods = testFoodService.getFoods();

        assertTrue(foods.contains(expectedFood));
        //database used
        verify(testFoodRepository).getFoodIdByFoodName("Apple");
        verify(testFoodRepository).foodDetailsUpToDateAndExist(expectedFood.getId());
        verify(testFoodRepository).getOne(expectedFood.getId());


        // details up to date
        verify(testFoodRepository, never()).replaceFood(any(Food.class));
    }
    @Test
    void testGetFoodFoodIdExistsAndDetailsUpToDate() {

        when(testFoodRepository.getFoodIdByFoodName("Apple"))
                .thenReturn(expectedFood.getId());
        when(testFoodRepository.foodDetailsUpToDateAndExist(expectedFood.getId()))
                .thenReturn(true);
        when(testFoodRepository.getOne(expectedFood.getId()))
                .thenReturn(expectedFood);

        Food food = testFoodService.getFood("Apple");

        assertFoodEquals(expectedFood, food);

        //database used
        verify(testFoodRepository).getFoodIdByFoodName("Apple");
        verify(testFoodRepository).foodDetailsUpToDateAndExist(expectedFood.getId());
        verify(testFoodRepository).getOne(expectedFood.getId());


        // details up to date
        verify(testFoodRepository, never()).replaceFood(any(Food.class));
    }

    @Test
    void testIdsExistsButDetailsNotUpToDate() {
        when(testFoodRepository.getFoodIdByFoodName("Apple"))
                .thenReturn(foodRecord.foodId());
        when(testFoodRepository.foodDetailsUpToDateAndExist(foodRecord.foodId()))
                .thenReturn(false);
        when(testFoodClient.getFoodById(foodRecord.foodId()))
                .thenReturn(new ApiResponseWrapper<>(createFakeFoodByIdResponse(), 200));

        List<Food> foods = testFoodService.getFoods();


        assertFoodEquals(expectedFood, foods.getFirst());

        // api used just for details
        verify(testFoodClient, never()).searchFood(anyString(), anyInt());
        verify(testFoodClient, atLeastOnce()).getFoodById(foodRecord.foodId());

        verify(testFoodRepository).replaceFood(any(Food.class));
    }

    @Test
    void testGetFoodIdExistsButDetailsNotUpToDate() {
        when(testFoodRepository.getFoodIdByFoodName("Apple"))
                .thenReturn(foodRecord.foodId());
        when(testFoodRepository.foodDetailsUpToDateAndExist(foodRecord.foodId()))
                .thenReturn(false);
        when(testFoodClient.getFoodById(foodRecord.foodId()))
                .thenReturn(new ApiResponseWrapper<>(createFakeFoodByIdResponse(), 200));

        Food food = testFoodService.getFood("Apple");


        assertFoodEquals(expectedFood, food);

        // api used just for details
        verify(testFoodClient, never()).searchFood(anyString(), anyInt());
        verify(testFoodClient, atLeastOnce()).getFoodById(foodRecord.foodId());

        verify(testFoodRepository).replaceFood(any(Food.class));
    }

    @Test
    void testIdsDontExistAndApiReturnsIds() {

        when(testFoodRepository.getFoodIdByFoodName("Apple"))
                .thenReturn(null);
        when(testFoodRepository.foodDetailsUpToDateAndExist(foodRecord.foodId()))
                .thenReturn(false);
        when(testFoodClient.searchFood("Apple", 1))
                .thenReturn(new ApiResponseWrapper<>(createFakeFoodSearchResponse(), 200));
        when(testFoodClient.getFoodById(foodRecord.foodId()))
                .thenReturn(new ApiResponseWrapper<>(createFakeFoodByIdResponse(), 200));

        List<Food> foods = testFoodService.getFoods();

        assertFoodEquals(expectedFood, foods.getFirst());
        //api used
        verify(testFoodClient).searchFood("Apple", 1);
        verify(testFoodClient).getFoodById(foodRecord.foodId());

        //database used to store
        verify(testFoodRepository).replaceFood(any(Food.class));
    }

    @Test
    void testGetFoodIdDontExistAndApiReturnsIds() {

        when(testFoodRepository.getFoodIdByFoodName("Apple"))
                .thenReturn(null);
        when(testFoodRepository.foodDetailsUpToDateAndExist(foodRecord.foodId()))
                .thenReturn(false);
        when(testFoodClient.searchFood("Apple", 1))
                .thenReturn(new ApiResponseWrapper<>(createFakeFoodSearchResponse(), 200));
        when(testFoodClient.getFoodById(foodRecord.foodId()))
                .thenReturn(new ApiResponseWrapper<>(createFakeFoodByIdResponse(), 200));

        Food food = testFoodService.getFood("Apple");

        assertFoodEquals(expectedFood, food);
        //api used
        verify(testFoodClient).searchFood("Apple", 1);
        verify(testFoodClient).getFoodById(foodRecord.foodId());

        //database used to store
        verify(testFoodRepository).replaceFood(any(Food.class));
    }



    @Test
    void testIdsDontExistButApiThrowsErrorsOnSearch() {

        when(testFoodRepository.getFoodIdByFoodName("Apple"))
                .thenReturn(null);
        when(testFoodRepository.foodDetailsUpToDateAndExist(foodRecord.foodId()))
                .thenReturn(false);
        when(testFoodClient.searchFood("Apple", 1))
                .thenReturn(new ApiResponseWrapper<>(null, 500));

        List<Food> foods = testFoodService.getFoods();

        assertTrue(foods.isEmpty());

        //api used but doesnt call food by id
        verify(testFoodClient).searchFood("Apple", 1);
        verify(testFoodClient, never()).getFoodById(foodRecord.foodId());

        //Database isnt used to store anything
        verify(testFoodRepository, never()).replaceFood(any(Food.class));

    }
    @Test
    void testGetFoodIdDontExistButApiThrowsErrorsOnSearch() {

        when(testFoodRepository.getFoodIdByFoodName("Apple"))
                .thenReturn(null);
        when(testFoodRepository.foodDetailsUpToDateAndExist(foodRecord.foodId()))
                .thenReturn(false);
        when(testFoodClient.searchFood("Apple", 1))
                .thenReturn(new ApiResponseWrapper<>(null, 500));

        Food food = testFoodService.getFood("Apple");

        assertNull(food);

        //api used but doesnt call food by id
        verify(testFoodClient).searchFood("Apple", 1);
        verify(testFoodClient, never()).getFoodById(foodRecord.foodId());

        //Database isnt used to store anything
        verify(testFoodRepository, never()).replaceFood(any(Food.class));

    }

    private FoodSearchResponse createFakeFoodSearchResponse() {
        FoodSearchRecord foodSearchRecord = new FoodSearchRecord(
                123,
                "Apple",
                "Apple"
        );

        return new FoodSearchResponse(
                new FoodSearchDetails(
                        List.of(foodSearchRecord),
                        1
                )
        );
    }



    private FoodByIdResponse createFakeFoodByIdResponse() {
        return new FoodByIdResponse(foodRecord);
    }
}
