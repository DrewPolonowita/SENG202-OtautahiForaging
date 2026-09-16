package seng202.team4.unittests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.FoodDAO;
import seng202.team4.services.dataCentre.foodData.FoodService;

import java.nio.file.Path;


public class FoodServiceTest {
    private FoodService testFoodService;
    private DatabaseManager databaseManager;
    private FoodDAO foodDAO;

    @TempDir
    Path tempDirectory;

    @BeforeEach
    public void setUpTest() {
        String testUrl = "jdbc:sqlite:" + tempDirectory.resolve("test_database.db").toAbsolutePath();
        databaseManager = new DatabaseManager(testUrl);
        foodDAO = new FoodDAO(databaseManager);
        testFoodService = new FoodService(foodDAO);
    }
    /*
    public void testGetAllFood() {
        List<Food> allFood = testFoodService.getAllFood();
        List<Food> expectedFoods = List.of(
                new Food ("Apple", "Fruit"),
                new Food("Beech", "Nut"),
                new Food("Walnut", "Nut"),
                new Food ("Strawberry", "Fruit"),
                new Food ("Sweet Chestnut", "Nut"),
                new Food("Honey Locust", "Nut"),
                new Food ("Olive", "Fruit"),
                new Food ("Peach", "Food"),
                new Food ("Pear", "Fruit"),
                new Food ("Hazel", "Nut"),
                new Food ("Apricot", "Fruit"),
                new Food ("Sweet Bay", "Leaves"),
                new Food ("Mulberry", "Fruit"),
                new Food ("Loquat", "Fruit"),
                new Food ("Quince", "Fruit"),
                new Food ("Almond", "Nut"),
                new Food ("Cherry", "Fruit"),
                new Food ("Elderberry", "Fruit"),
                new Food ("Juniper", "Fruit")
        );
        assertEquals(expectedFoods, allFood);
    }*/
}
