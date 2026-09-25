package seng202.team4.unittests.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.helpers.TestRecipeData;
import seng202.team4.models.Recipe;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeIngredient;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.RecipeDAO;
import seng202.team4.repository.RecipeRepository;
import seng202.team4.services.api.RecipeClientInterface;
import seng202.team4.services.dataCentre.recipeData.RecipeService;
import seng202.team4.unittests.TestSuite;
import seng202.team4.unittests.services.RecipeServiceTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;


public class RecipeDAOTest extends TestSuite {

    RecipeRepository recipeDAO;
    DatabaseManager databaseManager;
    RecipeService testRecipeService;
    RecipeClientInterface testRecipeClient;



    @BeforeEach
    void setUpTest() {
        databaseManager = new DatabaseManager(this.getTestDb());
        recipeDAO = new RecipeDAO(databaseManager);
        testRecipeClient = mock(RecipeClientInterface.class);
        testRecipeService = new RecipeService(recipeDAO, testRecipeClient);

    }

    @Test
    void getOneReturnsNullWhenRecipeDoesntExist() {
        Recipe testRecipe = recipeDAO.getOne( 1000);

        assertNull(testRecipe);
    }


    @Test
    void addWhenRecipeIdExistsAlreadyThrowsDuplicateEntryException() throws DuplicateEntryException {
        Recipe recipe = TestRecipeData.createRecipeModels().get(0);

        recipeDAO.add(recipe);

        assertThrows(DuplicateEntryException.class, () -> recipeDAO.add(recipe));
    }

    @Test
    void replaceRecipeUpdatesRecipe() throws DuplicateEntryException {
        Recipe original = TestRecipeData.createRecipeModels().get(0);
        RecipeIngredient ingredient = TestRecipeData.createFakeRecipeIngredient().get(0);

        recipeDAO.add(original);
        recipeDAO.addIngredients(ingredient);
        recipeDAO.addIngredientsByRecipe(ingredient, original);

        Recipe updatedRecipe = new Recipe(
                original.getId(),
                "new name",
                "new method",
                original.getIngredients(),
                900,
                800,
                700,
                600,
                "new description",
                null,
                "new image url",
                500,
                400
        );

        recipeDAO.replaceRecipe(updatedRecipe);
        Recipe result = recipeDAO.getOne(original.getId());

        assertNotNull(result);
        assertEquals(original.getId(), result.getId());
        assertEquals("new name", result.getName());
        assertEquals("new method", result.getMethod());
        assertEquals(900, result.getCarbohydrates());
        assertEquals(800, result.getCalories());
        assertEquals(700, result.getProtein());
        assertEquals(600, result.getFat());
        assertEquals("new description", result.getDescription());
        assertEquals("new image url", result.getImage());
        assertEquals(500, result.getCookingTime());
        assertEquals(400, result.getPreparationTime());

    }

    @Test
    void replaceRecipeAddsRecipeWhenRecipeDoesNotExist() {
        Recipe newRecipe = new Recipe(
                41239841,
                "new name",
                "new method",
                List.of("Test ingredient"),
                900,
                800,
                700,
                600,
                "new description",
                null,
                "new image url",
                500,
                400
        );

        recipeDAO.replaceRecipe(newRecipe);
        RecipeIngredient ingredient = TestRecipeData.createFakeRecipeIngredient().get(0);
        recipeDAO.addIngredients(ingredient);
        recipeDAO.addIngredientsByRecipe(ingredient, newRecipe);

        Recipe result = recipeDAO.getOne(newRecipe.getId());

        assertNotNull(result);
        assertEquals(newRecipe.getId(), result.getId());
        assertEquals("new name", result.getName());
        assertEquals("new method", result.getMethod());
        assertEquals(900, result.getCarbohydrates());
        assertEquals(800, result.getCalories());
        assertEquals(700, result.getProtein());
        assertEquals(600, result.getFat());
        assertEquals("new description", result.getDescription());
        assertEquals("new image url", result.getImage());
        assertEquals(500, result.getCookingTime());
        assertEquals(400, result.getPreparationTime());

    }

    @Test
    void deleteRemovesRecipeAndLinkedData() throws Exception {
        Recipe recipe = TestRecipeData.createRecipeModels().get(0);
        RecipeIngredient ingredient = TestRecipeData.createFakeRecipeIngredient().get(0);

        recipeDAO.add(recipe);
        recipeDAO.addIngredients(ingredient);
        recipeDAO.addIngredientsByRecipe(ingredient, recipe);

        recipeDAO.delete(recipe.getId());
        assertNull(recipeDAO.getOne(recipe.getId()));
        String sql = "SELECT COUNT(*) FROM ingredients_in_recipe WHERE recipe_id = ?";

        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recipe.getId());

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(0, rs.getInt(1));
            }
        }
    }

    @Test
    void deleteRemovesRecipeUseFoodLink() throws DuplicateEntryException {
        Recipe recipe = TestRecipeData.createRecipeModels().get(0);
        RecipeIngredient ingredient = TestRecipeData.createFakeRecipeIngredient().get(0);

        recipeDAO.add(recipe);
        recipeDAO.addIngredients(ingredient);
        recipeDAO.addIngredientsByRecipe(ingredient, recipe);
        recipeDAO.addRecipeUseFood("Apple", recipe.getId());

        assertTrue(recipeDAO.getRecipeIdsByFoodName("Apple").contains(recipe.getId()));

        recipeDAO.delete(recipe.getId());

        assertFalse(recipeDAO.getRecipeIdsByFoodName("Apple").contains(recipe.getId()));
    }

    @Test
    void getOneReturnsRecipeWithAllIngredients() throws DuplicateEntryException {
        Recipe testRecipe = TestRecipeData.createRecipeModels().get(0);

        RecipeIngredient ingredient1 = new RecipeIngredient(900, "Apple", 700, 500, "", "1 Apple");
        RecipeIngredient ingredient2 = new RecipeIngredient(800, "Butter", 600, 400, "g", "100 g butter");

        recipeDAO.add(testRecipe);

        recipeDAO.addIngredients(ingredient1);
        recipeDAO.addIngredientsByRecipe(ingredient1, testRecipe);

        recipeDAO.addIngredients(ingredient2);
        recipeDAO.addIngredientsByRecipe(ingredient2, testRecipe);

        Recipe result = recipeDAO.getOne(testRecipe.getId());

        assertNotNull(result);
        assertEquals(testRecipe.getId(), result.getId());
        assertTrue(result.getIngredients().contains("1 Apple"));
        assertTrue(result.getIngredients().contains("100 g butter"));
        assertEquals(2, result.getIngredients().size());

    }

    @Test
    void replaceRecipeUpdatesBasedOnTime() throws Exception {
        Recipe recipe = TestRecipeData.createRecipeModels().get(0);
        RecipeIngredient ingredient = TestRecipeData.createFakeRecipeIngredient().get(0);

        recipeDAO.add(recipe);
        recipeDAO.addIngredients(ingredient);
        recipeDAO.addIngredientsByRecipe(ingredient, recipe);

        String sql = """
            UPDATE recipes
            SET retrieval_time = DATETIME('now', '-25 hours')
            WHERE recipe_id = ?
            """;

        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recipe.getId());
            ps.executeUpdate();
        }

        assertFalse(recipeDAO.recipeDetailsUpToDateAndExist(recipe.getId()));

        Recipe updatedRecipe = new Recipe(
                recipe.getId(),
                recipe.getName(),
                recipe.getMethod(),
                recipe.getIngredients(),
                recipe.getCarbohydrates(),
                recipe.getCalories(),
                recipe.getProtein(),
                recipe.getFat(),
                recipe.getDescription(),
                null,
                recipe.getImage(),
                recipe.getCookingTime(),
                recipe.getPreparationTime()
        );

        recipeDAO.replaceRecipe(updatedRecipe);

        assertTrue(recipeDAO.recipeDetailsUpToDateAndExist(recipe.getId()));
    }

    private List<Recipe> fillDatabase() {
        List<Recipe> testRecipes = TestRecipeData.createRecipeModels();
        List<RecipeIngredient> testIngredients = TestRecipeData.createFakeRecipeIngredient();
        testRecipeService.populateIngredientTable(testIngredients);

        for (Recipe recipe : testRecipes) {
            try {
                recipeDAO.add(recipe);
            } catch (DuplicateEntryException e) {

            }
            testRecipeService.populateIngredientInRecipeTable(testIngredients, recipe);
        }
        return testRecipes;
    }

    @Test
    public void testGetAllRecipesWithPopulatedDatabase() {
        List<Recipe> populatedList = fillDatabase();


        List<Recipe> allRecipes = testRecipeService.getAllRecipes();


        assertEquals(3, allRecipes.size());


        for (Recipe expectedRecipe : populatedList) {
            assertTrue(allRecipes.contains(expectedRecipe));
        }
    }

    @Test
    void getRecipeIdsByFoodNameGivesRightIds() throws DuplicateEntryException {
        Recipe recipe1 = TestRecipeData.createRecipeModels().get(0);
        Recipe recipe2 = TestRecipeData.createRecipeModels().get(1);

        recipeDAO.add(recipe1);
        recipeDAO.add(recipe2);

        recipeDAO.addRecipeUseFood("Apple", recipe1.getId());
        recipeDAO.addRecipeUseFood("Apple", recipe2.getId());

        List<Integer> ids = recipeDAO.getRecipeIdsByFoodName("Apple");

        assertTrue(ids.contains(recipe1.getId()));
        assertTrue(ids.contains(recipe2.getId()));
    }
}
