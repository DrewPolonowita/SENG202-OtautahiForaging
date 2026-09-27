package seng202.team4.unittests.services;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import seng202.team4.helpers.TestFoodData;
import seng202.team4.models.Food;
import seng202.team4.models.api.Food.FoodByID.FoodByIdRecord;
import seng202.team4.models.api.Food.FoodByID.FoodByIdResponse;
import seng202.team4.models.api.Food.FoodByID.FoodServings;
import seng202.team4.models.api.Food.FoodByID.Serving;
import seng202.team4.models.api.Food.SearchFood.FoodSearchDetails;
import seng202.team4.models.api.Food.SearchFood.FoodSearchRecord;
import seng202.team4.models.api.Food.SearchFood.FoodSearchResponse;
import seng202.team4.models.api.Request;
import seng202.team4.repository.FoodRepository;
import seng202.team4.services.FoodMapper;
import seng202.team4.services.api.FoodClientInterface;
import seng202.team4.services.dataCentre.foodData.FoodService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * mostly tests getFoods like the recipe service
 *
 * getFoods decides between database and api so it asks
 * Do we have the food id for the food name?
 * Do we have recent details for it?
 */
public class FoodServiceTest {

    private FoodRepository testFoodRepository;
    private FoodClientInterface testFoodClient;
    private FoodMapper foodMapper;
    private FoodService testFoodService;

    @BeforeEach
    public void setUpTest() {
        testFoodRepository = mock(FoodRepository.class);
        testFoodClient = mock(FoodClientInterface.class);
        foodMapper = mock(FoodMapper.class);
        when(foodMapper.getFoodMap())
                .thenReturn(Map.of("Apple", List.of("Apple")));
        when(foodMapper.getTypeMap())
                .thenReturn(Map.of("Fruit", List.of("Apple")));
        when(foodMapper.getFoodType("Apple"))
                .thenReturn("Fruit");

        testFoodService = new FoodService(testFoodRepository, testFoodClient, foodMapper);
    }

    @Test
    public void testFoodIdExistsAndDetailsUpToDate() {
        Food apple = TestFoodData.createAppleFood();

        when(testFoodRepository.getFoodIdByFoodName("Apple"))
                .thenReturn(apple.getId());
        when(testFoodRepository.foodDetailsUpToDateAndExist(apple.getId()))
                .thenReturn(true);
        when(testFoodRepository.getOne(apple.getId()))
                .thenReturn(apple);

        List<Food> foods = testFoodService.getFoods();

        assertTrue(foods.contains(apple));
        //database used
        verify(testFoodRepository).getFoodIdByFoodName("Apple");
        verify(testFoodRepository).foodDetailsUpToDateAndExist(apple.getId());
        verify(testFoodRepository).getOne(apple.getId());


        // details up to date
        verify(testFoodRepository, never()).replaceFood(any(Food.class));
    }

    @Test
    public void testIdsExistsButDetailsNotUpToDate() {
        FoodByIdRecord foodRecord = TestFoodData.createFakeFoodByIdRecord();

        when(testFoodRepository.getFoodIdByFoodName("Apple"))
                .thenReturn(foodRecord.foodId());
        when(testFoodRepository.foodDetailsUpToDateAndExist(foodRecord.foodId()))
                .thenReturn(false);
        when(testFoodClient.getFoodById(foodRecord.foodId()))
                .thenReturn(new Request<>(createFakeFoodByIdResponse(), 200));

        List<Food> foods = testFoodService.getFoods();

        boolean containsCorrectFood = false;

        for (Food food : foods) {
            if (food.getId() == foodRecord.foodId()) {
                containsCorrectFood = true;
                break;
            }
        }
        assertTrue(containsCorrectFood);

        // api used just for details
        verify(testFoodClient, never()).searchFood(anyString(), anyInt());
        verify(testFoodClient, atLeastOnce()).getFoodById(foodRecord.foodId());

        verify(testFoodRepository).replaceFood(any(Food.class));
    }

    @Test
    public void testIdsDontExistAndApiReturnsIds() {
        FoodByIdRecord foodRecord = TestFoodData.createFakeFoodByIdRecord();

        when(testFoodRepository.getFoodIdByFoodName("Apple"))
                .thenReturn(null);
        when(testFoodRepository.foodDetailsUpToDateAndExist(foodRecord.foodId()))
                .thenReturn(false);
        when(testFoodClient.searchFood("Apple", 1))
                .thenReturn(new Request<>(createFakeFoodSearchResponse(), 200));
        when(testFoodClient.getFoodById(foodRecord.foodId()))
                .thenReturn(new Request<>(createFakeFoodByIdResponse(), 200));

        List<Food> foods = testFoodService.getFoods();

        assertEquals(foodRecord.foodId(), foods.get(0).getId());

        //api used
        verify(testFoodClient).searchFood("Apple", 1);
        verify(testFoodClient).getFoodById(foodRecord.foodId());

        //database used to store
        verify(testFoodRepository).replaceFood(any(Food.class));

    }

    @Test
    public void testIdsDontExistButApiThrowsErrorsOnSearch() {
        FoodByIdRecord foodRecord = TestFoodData.createFakeFoodByIdRecord();

        when(testFoodRepository.getFoodIdByFoodName("Apple"))
                .thenReturn(null);
        when(testFoodRepository.foodDetailsUpToDateAndExist(foodRecord.foodId()))
                .thenReturn(false);
        when(testFoodClient.searchFood("Apple", 1))
                .thenReturn(new Request<>(null, 500));

        List<Food> foods = testFoodService.getFoods();

        assertTrue(foods.isEmpty());

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


    @Test
    public void testCreateFoodObjectById() {
        FoodByIdRecord foodRecord = TestFoodData.createFakeFoodByIdRecord();

        Food food = testFoodService.createFoodObjectById(foodRecord);

        assertEquals(123, food.getId());
        assertEquals("Apple", food.getName());
        assertEquals("Fruit", food.getType());
        assertEquals(100, food.getCalories());
        assertEquals(20, food.getCarbohydrates());
        assertEquals(5, food.getProtein());
        assertEquals(2, food.getFat());
        assertEquals("100 g", food.getServingDescription());
        assertEquals(1234, food.getServingId());
    }

    private FoodByIdResponse createFakeFoodByIdResponse() {
        return new FoodByIdResponse(createFakeFoodByIdRecord());
    }

    private FoodByIdRecord createFakeFoodByIdRecord() {
        Serving serving = new Serving(
                1234,
                "100 g",
                100,
                20,
                5,
                2
        );

        return new FoodByIdRecord(
                123,
                "Apple",
                new FoodServings(List.of(serving))
        );
    }




}
