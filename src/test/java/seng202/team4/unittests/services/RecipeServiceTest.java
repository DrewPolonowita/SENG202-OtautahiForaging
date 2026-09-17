package seng202.team4.unittests.services;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.helpers.TestRecipeData;
import seng202.team4.models.Recipe;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdRecord;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdResponse;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeIngredient;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchDetails;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchResponse;
import seng202.team4.models.api.Request;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.RecipeDAO;
import seng202.team4.repository.RecipeRepository;
import seng202.team4.services.api.RecipeClientInterface;
import seng202.team4.services.dataCentre.recipeData.RecipeService;
import seng202.team4.unittests.TestSuite;


import java.io.InputStream;
import java.util.List;
import java.util.Scanner;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class RecipeServiceTest extends TestSuite {


    private RecipeService testRecipeService;
    private DatabaseManager databaseManager;
    private RecipeRepository recipeDAO;
    InputStream idBakedLemonSnapperJson;
    InputStream searchBakedLemonSnapperJson;



    @BeforeEach
    public void setUpTest() {
        //todo: ADD API HANDLER FOR RECIPE
        databaseManager = new DatabaseManager(getTestDb());
        recipeDAO = new RecipeDAO(databaseManager);
        testRecipeService = new RecipeService(recipeDAO, null);
        idBakedLemonSnapperJson = getClass()
                .getResourceAsStream("/fatsecretresponses/recipe-by-id-baked-lemon-snapper.json");
        searchBakedLemonSnapperJson = getClass()
                .getResourceAsStream("/fatsecretresponses/search-recipe-baked-lemon-snapper.json");
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    private <T> T readJson(String path, TypeReference<T> typeReference) throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(path);
        assertNotNull(inputStream);
        return objectMapper.readValue(inputStream, typeReference);
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
    public void testCreateRecipeObjectById() {
        RecipeByIdRecord testByIdRecord = TestRecipeData.createFakeRecipeByIdRecord();
        Recipe testRecipeRecord = testRecipeService.createRecipeObjectById(testByIdRecord);
        List<String> testIngredients = List.of("1 Apple");
        Recipe expectedRecord  = new Recipe(123, "Recipe Name", "1: Direction step 1\n2: Direction step 2", testIngredients, 345, 123,567, 789, "Description", null, "Image URL", 10, 10);
        assertEquals(expectedRecord, testRecipeRecord);
    }

    @Test
    public void testGetRecipeForFoodWithPopulatedDatabase() {
        List<Recipe> populatedList = fillDatabase();

        String food = "Apple";
        Recipe expectedRecipe1 = populatedList.get(0);
        Recipe expectedRecipe2 = populatedList.get(1);

        recipeDAO.addRecipeUseFood(food, expectedRecipe1.getId());
        recipeDAO.addRecipeUseFood(food, expectedRecipe2.getId());

        List<Recipe> recipes = testRecipeService.getRecipesForFood(food);

        assertEquals(2, recipes.size());

        List<Integer> recipeIds = recipes.stream()
                .map(Recipe::getId)
                .toList();

        assertTrue(recipeIds.contains(expectedRecipe1.getId()));
        assertTrue(recipeIds.contains(expectedRecipe2.getId()));
    }

    private String inputStreamToString(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(inputStream);

        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }

        return stringBuilder.toString();
    }

    @Test
    public void testGetRecipeForFoodWithoutPopulatedDatabase() throws Exception {
        RecipeClientInterface recipeClient = mock(RecipeClientInterface.class);
        ObjectMapper objectMapper = new ObjectMapper();

        RecipeSearchResponse searchResponse = objectMapper.readValue(
                inputStreamToString(searchBakedLemonSnapperJson),
                new TypeReference<RecipeSearchResponse>() {
                }
        );

        RecipeByIdResponse byIdResponse = objectMapper.readValue(
                inputStreamToString(idBakedLemonSnapperJson),
                new TypeReference<RecipeByIdResponse>() {
                }
        );


        when(recipeClient.searchRecipe(eq("Apple"), eq(true), eq(20)))
                .thenReturn(new Request<>(searchResponse, 200));

        when(recipeClient.getRecipeById(eq(91)))
                .thenReturn(new Request<>(byIdResponse, 200));
        // called the api and got a response

        RecipeService recipeService = new RecipeService(recipeDAO, recipeClient);

        List<Recipe> recipes = recipeService.getRecipesForFood("Apple");

        assertEquals(1, recipes.size());
        assertEquals(91, recipes.getFirst().getId());

        verify(recipeClient).searchRecipe("Apple", true, 20);
        verify(recipeClient).getRecipeById(91);

        assertEquals(91, recipeDAO.getRecipeIdsByFoodName("Apple").get(0));
    }

    @Test
    public void testCaseWhereApiReturnsNothingAndNothingInDatabase() {
        RecipeClientInterface recipeClient = mock(RecipeClientInterface.class);

        RecipeSearchResponse emptySearchResponse = new RecipeSearchResponse(
                new RecipeSearchDetails(0, List.of())
        );
        when(recipeClient.searchRecipe(eq("Apple"), eq(true), eq(20)))
                .thenReturn(new Request<>(emptySearchResponse, 200));
        RecipeService recipeService = new RecipeService(recipeDAO, recipeClient);

        List<Recipe> recipes = recipeService.getRecipesForFood("Apple");

        assertTrue(recipes.isEmpty());

        verify(recipeClient).searchRecipe("Apple", true, 20);
        verify(recipeClient, never()).getRecipeById(91);
    }

    @Test
    public void testResponseFromApiWithAnErrorCode() {
        RecipeClientInterface recipeClient = mock(RecipeClientInterface.class);

        when(recipeClient.searchRecipe(eq("Apple"), eq(true), eq(20)))
                .thenReturn(new Request<>(null, 500));
        RecipeService recipeService = new RecipeService(recipeDAO, recipeClient);

        List<Recipe> recipes = recipeService.getRecipesForFood("Apple");

        assertTrue(recipes.isEmpty());

        verify(recipeClient).searchRecipe("Apple", true, 20);
        verify(recipeClient, never()).getRecipeById(91);
    }





}
