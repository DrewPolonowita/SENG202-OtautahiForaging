package seng202.team4.unittests.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.helpers.TestFoodData;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.FoodDAO;
import seng202.team4.repository.FoodRepository;
import seng202.team4.models.Food;
import seng202.team4.services.FoodFactory;
import seng202.team4.unittests.TestSuite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seng202.team4.helpers.TestFoodData.assertFoodEquals;
import static seng202.team4.helpers.TestFoodData.createFoodServing;

class FoodDAOTest extends TestSuite {

    FoodRepository foodDAO;
    DatabaseManager databaseManager;


    @BeforeEach
    void setUpTest() {
        databaseManager = new DatabaseManager(this.getTestDb());
        foodDAO = new FoodDAO(databaseManager);
    }


    @Test
    void getOneReturnsNullWhenFoodDoesntExist() {
        Food result = foodDAO.getOne(999999);

        assertNull(result);
    }

    @Test
    void addWhenFoodIdExistsAlreadyThrowsDuplicateEntryException() throws DuplicateEntryException {
        Food food = TestFoodData.createFood(90000, "Apple", "Fruit");

        foodDAO.add(food);

        assertThrows(DuplicateEntryException.class, () -> foodDAO.add(food));
    }

    @Test
    void replaceFoodUpdatesFood() throws DuplicateEntryException {
        Food original = TestFoodData.createFood(90000, "Apple", "Fruit");

        foodDAO.add(original);

        Food updatedFood = FoodFactory.create(
                original.getId(),
                "New Apple",
                "New Fruit",
                List.of(
                        TestFoodData.createFoodServing(500,
                                        "1 apple",
                                        900,
                                        800,
                                        700,
                                        600)
                )
        );

        foodDAO.replaceFood(updatedFood);

        Food result = foodDAO.getOne(original.getId());

        assertFoodEquals(updatedFood, result);
    }

    @Test
    void replaceFoodAddsFoodWhenFoodDoesNotExist() {
        Food newFood = FoodFactory.create(90000,
                "Apple",
                "Fruit",
                List.of(
                        createFoodServing(1000,
                                "1 banana",
                                134143,
                                13414,
                                109,
                                341)
                ));

        foodDAO.replaceFood(newFood);

        Food result = foodDAO.getOne(newFood.getId());

        assertFoodEquals(newFood, result);
    }

    @Test
    void foodDetailsUpToDateAndExistReturnsTrueForRecentlyAddedFood() throws DuplicateEntryException {
        Food food = TestFoodData.createFood(90000, "Apple", "Fruit");

        foodDAO.add(food);

        assertTrue(foodDAO.foodDetailsUpToDateAndExist(food.getId()));
    }

    @Test
    void foodDetailsUpToDateAndExistReturnsFalseForMissingFood() {
        assertFalse(foodDAO.foodDetailsUpToDateAndExist(6767));
    }

    @Test
    void replaceFoodRefreshesRetrievalTime() throws Exception {
        Food food = TestFoodData.createFood(90000, "Apple", "Fruit");

        foodDAO.add(food);

        String sql = """
                UPDATE food
                SET retrieval_time = DATETIME('now', '-25 hours')
                WHERE food_id = ?
                """;

        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, food.getId());
            ps.executeUpdate();
        }

        assertFalse(foodDAO.foodDetailsUpToDateAndExist(food.getId()));

        foodDAO.replaceFood(food);

        assertTrue(foodDAO.foodDetailsUpToDateAndExist(food.getId()));
    }

    @Test
    void getFoodIdByFoodNameWorksWhenFoodExists() throws DuplicateEntryException {
        Food food = TestFoodData.createFood(90000, "Apple", "Fruit");

        foodDAO.add(food);

        Integer result = foodDAO.getFoodIdByFoodName("Apple");

        assertEquals(food.getId(), result);
    }

    @Test
    void getFoodIdByFoodNameGivesNullWhenDoesntExist() {
        Integer result = foodDAO.getFoodIdByFoodName("Apple");

        assertNull(result);

    }

    @Test
    void testFoodDetailsUpToDateAndExistReturnsFalseCorrectly() throws Exception {
        Food food = TestFoodData.createFood(90000, "Apple", "Fruit");

        foodDAO.add(food);

        String sql = """
            UPDATE food
            SET retrieval_time = DATETIME('now', '-25 hours')
            WHERE food_id = ?
            """;

        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, food.getId());
            ps.executeUpdate();
        }
        assertFalse(foodDAO.foodDetailsUpToDateAndExist(food.getId()));
    }



}
