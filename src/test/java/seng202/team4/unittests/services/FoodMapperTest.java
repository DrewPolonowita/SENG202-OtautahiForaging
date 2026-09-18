package seng202.team4.unittests.services;

import org.junit.jupiter.api.Test;
import seng202.team4.services.FoodMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FoodMapperTest {

    @Test
    public void testHallsCrabAppleExistsInFoodMap() {
        FoodMapper foodMapper = new FoodMapper();

        assertTrue(foodMapper.getFoodMap().get("Apple").contains("Hall's Crab Apple"));
    }

    @Test
    public void testGetFoodTypeReturnsNAForTreeCommonName() {
        FoodMapper foodMapper = new FoodMapper();

        assertEquals("N/A", foodMapper.getFoodType("Common Mulberry"));
    }
}
