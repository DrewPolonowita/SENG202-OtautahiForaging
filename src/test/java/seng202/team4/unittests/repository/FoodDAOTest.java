package seng202.team4.unittests.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.FoodDAO;
import seng202.team4.repository.FoodRepository;
import seng202.team4.models.Food;
import seng202.team4.unittests.TestSuite;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FoodDAOTest extends TestSuite {

    FoodRepository foodDAO;
    DatabaseManager databaseManager;


    @BeforeEach
    void setUpTest() {
        databaseManager = new DatabaseManager(this.getTestDb());
        foodDAO = new FoodDAO(databaseManager);
    }

    private Food createFood(int id, String name, String type) {
        return new Food(id, name, type, 100, 20, 5, 2, "100 g", 1234);
    }

    @Test
    void getOneReturnsNullWhenFoodDoesntExist() {
        Food result = foodDAO.getOne(999999);

        assertNull(result);
    }

    @Test
    void addWhenFoodIdExistsAlreadyThrowsDuplicateEntryException() throws DuplicateEntryException {
        Food food = createFood(90000, "Apple", "Fruit");

        foodDAO.add(food);

        assertThrows(DuplicateEntryException.class, () -> foodDAO.add(food));
    }

    @Test
    void replaceFoodUpdatesFood() throws DuplicateEntryException {
        Food original = createFood(90000, "Apple", "Fruit");

        foodDAO.add(original);

        Food updatedFood = new Food(
                original.getId(),
                "New Apple",
                "New Fruit",
                900,
                800,
                700,
                600,
                "1 apple",
                500
        );

        foodDAO.replaceFood(updatedFood);

        Food result = foodDAO.getOne(original.getId());

        assertFoodEquals(updatedFood, result);
    }

    @Test
    void replaceFoodAddsFoodWhenFoodDoesNotExist() {
        Food newFood = new Food(
                90000,
                "Apple",
                "Fruit",
                1000,
                142,
                41424,
                142134,
                "1 banana",
                41341
        );

        foodDAO.replaceFood(newFood);

        Food result = foodDAO.getOne(newFood.getId());

        assertFoodEquals(newFood, result);
    }

    @Test
    void foodDetailsUpToDateAndExistReturnsTrueForRecentlyAddedFood() throws DuplicateEntryException {
        Food food = createFood(90000, "Apple", "Fruit");

        foodDAO.add(food);

        assertTrue(foodDAO.foodDetailsUpToDateAndExist(food.getId()));
    }

    @Test
    void foodDetailsUpToDateAndExistReturnsFalseForMissingFood() {
        assertFalse(foodDAO.foodDetailsUpToDateAndExist(6767));
    }

    @Test
    void replaceFoodRefreshesRetrievalTime() throws Exception {
        Food food = createFood(90000, "Apple", "Fruit");

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
    public void getFoodIdByFoodNameWorksWhenFoodExists() throws DuplicateEntryException {
        Food food = createFood(90000, "Apple", "Fruit");

        foodDAO.add(food);

        Integer result = foodDAO.getFoodIdByFoodName("Apple");

        assertEquals(food.getId(), result);
    }

    @Test
    public void getFoodByIdGivesNullWhenDoesntExist() throws DuplicateEntryException {
        Integer result = foodDAO.getFoodIdByFoodName("Apple");

        assertNull(result);

    }

    private void assertFoodEquals(Food expected, Food actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getCalories(), actual.getCalories());
        assertEquals(expected.getCarbohydrates(), actual.getCarbohydrates());
        assertEquals(expected.getProtein(), actual.getProtein());
        assertEquals(expected.getFat(), actual.getFat());
        assertEquals(expected.getServingDescription(), actual.getServingDescription());
        assertEquals(expected.getServingId(), actual.getServingId());
    }

}
