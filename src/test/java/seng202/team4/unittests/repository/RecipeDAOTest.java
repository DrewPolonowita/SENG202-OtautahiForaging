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
        Recipe recipe = TestRecipeData.createRecipeModels().getFirst();

        recipeDAO.add(recipe);

        assertThrows(DuplicateEntryException.class, () -> recipeDAO.add(recipe));
    }

    @Test
    void replaceRecipeUpdatesRecipe() throws DuplicateEntryException {
        Recipe original = TestRecipeData.createRecipeModels().getFirst();
        addRecipeWithIngredient(original);
        recipeDAO.replaceRecipe(TestRecipeData.createUpdatedRecipe(original));
        Recipe result = recipeDAO.getOne(original.getId());

        assertNotNull(result);
        assertEquals(original.getId(), result.getId());
        assertEquals("new name", result.getName());
        assertEquals("new method", result.getMethod());

    }

    @Test
    void replaceRecipeAddsRecipeWhenRecipeDoesNotExist() {
        Recipe newRecipe = TestRecipeData.createNewRecipe();

        recipeDAO.replaceRecipe(newRecipe);
        RecipeIngredient ingredient = TestRecipeData.createFakeRecipeIngredient().getFirst();
        recipeDAO.addIngredients(ingredient);
        recipeDAO.addIngredientsByRecipe(ingredient, newRecipe);

        Recipe result = recipeDAO.getOne(newRecipe.getId());

        assertNotNull(result);
        assertEquals(newRecipe.getId(), result.getId());
        assertEquals("new name", result.getName());
        assertEquals("new method", result.getMethod());


    }

    @Test
    void deleteRemovesRecipeAndLinkedData() throws Exception {
        Recipe recipe = TestRecipeData.createRecipeModels().get(0);
        addRecipeWithIngredient(recipe);

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
        addRecipeWithIngredient(recipe);
        recipeDAO.addRecipeUseFood("Apple", recipe.getId());

        assertTrue(recipeDAO.getRecipeIdsByFoodName("Apple").contains(recipe.getId()));

        recipeDAO.delete(recipe.getId());

        assertFalse(recipeDAO.getRecipeIdsByFoodName("Apple").contains(recipe.getId()));
    }

    @Test
    void getOneReturnsRecipeWithAllIngredients() throws DuplicateEntryException {
        Recipe testRecipe = TestRecipeData.createRecipeModels().getFirst();
        List<RecipeIngredient> ingredients = TestRecipeData.createFakeRecipeIngredient();

        recipeDAO.add(testRecipe);

        for (RecipeIngredient ingredient : ingredients) {
            recipeDAO.addIngredients(ingredient);
            recipeDAO.addIngredientsByRecipe(ingredient, testRecipe);
        }

        Recipe result = recipeDAO.getOne(testRecipe.getId());

        assertNotNull(result);
        assertEquals(testRecipe.getId(), result.getId());
        assertTrue(result.getIngredients().contains("100 g TestIngredient"));
        assertTrue(result.getIngredients().contains("200 cups TestIngredient2"));
        assertEquals(2, result.getIngredients().size());
    }

    @Test
    void replaceRecipeUpdatesBasedOnTime() throws Exception {
        Recipe recipe = TestRecipeData.createRecipeModels().get(0);
        addRecipeWithIngredient(recipe);

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

        recipeDAO.replaceRecipe(recipe);

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
        Recipe recipe1 = TestRecipeData.createRecipeModels().getFirst();
        Recipe recipe2 = TestRecipeData.createRecipeModels().get(1);

        recipeDAO.add(recipe1);
        recipeDAO.add(recipe2);

        recipeDAO.addRecipeUseFood("Apple", recipe1.getId());
        recipeDAO.addRecipeUseFood("Apple", recipe2.getId());

        List<Integer> ids = recipeDAO.getRecipeIdsByFoodName("Apple");

        assertTrue(ids.contains(recipe1.getId()));
        assertTrue(ids.contains(recipe2.getId()));
    }

    private void addRecipeWithIngredient(Recipe recipe) throws DuplicateEntryException {
        RecipeIngredient ingredient = TestRecipeData.createFakeRecipeIngredient().get(0);
        recipeDAO.add(recipe);
        recipeDAO.addIngredients(ingredient);
        recipeDAO.addIngredientsByRecipe(ingredient, recipe);
    }
}
