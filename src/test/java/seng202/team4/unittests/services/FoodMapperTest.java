package seng202.team4.unittests.services;

import org.junit.jupiter.api.Test;
import seng202.team4.services.FoodMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FoodMapperTest {

    @Test
    void testCommonAppleExistsInFoodMap() {
        FoodMapper foodMapper = new FoodMapper();

        assertTrue(foodMapper.getFoodMap().get("Apple").contains("Common Apple"));
    }

    @Test
    void testGetFoodTypeReturnsNAForTreeCommonName() {
        FoodMapper foodMapper = new FoodMapper();

        assertEquals("N/A", foodMapper.getFoodType("Common Mulberry"));
    }
}
