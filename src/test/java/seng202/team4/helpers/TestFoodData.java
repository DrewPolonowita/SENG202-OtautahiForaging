package seng202.team4.helpers;

import seng202.team4.models.Food;
import seng202.team4.models.FoodServing;
import seng202.team4.models.api.Food.FoodByID.FoodByIdRecord;
import seng202.team4.models.api.Food.FoodByID.FoodServings;
import seng202.team4.models.api.Food.FoodByID.Serving;
import seng202.team4.services.FoodFactory;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestFoodData {



    public static Food createFood(int id, String name, String type) {
        return createFood(id, name, type, createDefaultServings());
    }

    public static Food createFood(
            int id,
            String name,
            String type,
            List<FoodServing> servings
    ) {
        return FoodFactory.create(id, name, type, servings);
    }

    public static List<FoodServing> createDefaultServings() {
        return List.of(
                createFoodServing(1234, "100 g", 100, 20, 5, 2),
                createFoodServing(5678, "1 medium", 150, 30, 1, 0.5)
        );
    }

    public static FoodByIdRecord createFakeFoodByIdRecord(int id, String name, List<FoodServing> servings) {
        return new FoodByIdRecord(
                id,
                name,
                new FoodServings(createApiServings(servings))
        );
    }

    private static List<Serving> createApiServings(List<FoodServing> servings) {
        return servings.stream()
                .map(serving -> new Serving(
                        serving.getServingId(),
                        serving.getDescription(),
                        serving.getCalories(),
                        serving.getCarbohydrates(),
                        serving.getProtein(),
                        serving.getFat()
                ))
                .toList();
    }

    public static FoodServing createFoodServing(
            int servingId,
            String description,
            double calories,
            double carbohydrates,
            double protein,
            double fat
    ) {
        return FoodFactory.createServing(
                servingId,
                description,
                calories,
                carbohydrates,
                protein,
                fat
        );
    }

    public static FoodByIdRecord createFakeFoodByIdRecord() {
        Serving serving = new Serving(
                1234,
                "100 g",
                100,
                20,
                5,
                2
        );

        FoodServings servings = new FoodServings(List.of(serving));

        return new FoodByIdRecord(
                123,
                "Apple",
                servings
        );
    }

    public static void assertFoodEquals(Food expected, Food actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getType(), actual.getType());

        assertEquals(expected.getServings().size(), actual.getServings().size());

        for (int i = 0; i < expected.getServings().size(); i++) {
            assertFoodServingEquals(expected.getServings().get(i), actual.getServings().get(i));
        }
    }

    public static void assertFoodServingEquals(FoodServing expected, FoodServing actual) {
        assertNotNull(actual);
        assertEquals(expected.getServingId(), actual.getServingId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getCalories(), actual.getCalories());
        assertEquals(expected.getCarbohydrates(), actual.getCarbohydrates());
        assertEquals(expected.getProtein(), actual.getProtein());
        assertEquals(expected.getFat(), actual.getFat());
    }


}