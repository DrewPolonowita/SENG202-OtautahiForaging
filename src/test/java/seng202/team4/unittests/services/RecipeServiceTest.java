package seng202.team4.unittests.services;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.helpers.TestRecipeData;
import seng202.team4.models.Recipe;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeIngredient;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.RecipeDAO;
import seng202.team4.services.FoodService;
import seng202.team4.services.RecipeService;


import java.nio.file.Path;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class RecipeServiceTest {


    private RecipeService testRecipeService;
    private DatabaseManager databaseManager;
    private RecipeDAO recipeDAO;
    private FoodService foodService;


    @TempDir
    Path tempDirectory;


    @BeforeEach
    public void setUpTest(){
        //todo: ADD API HANDLER FOR RECIPE
        String testUrl = "jdbc:sqlite:" + tempDirectory.resolve("test_database.db").toAbsolutePath();
        databaseManager = new DatabaseManager(testUrl);
        recipeDAO = new RecipeDAO(databaseManager);
        testRecipeService = new RecipeService(recipeDAO, null);
    }


    private List<Recipe> fillDatabase() {
        List<Recipe> testRecipes = TestRecipeData.createRecipeModels();
        List<RecipeIngredient> testIngredients = TestRecipeData.createFakeRecipeIngredient();
            testRecipeService.populateIngredientTable(testIngredients);

        for (Recipe recipe : testRecipes) {
            recipeDAO.add(recipe);
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


}

