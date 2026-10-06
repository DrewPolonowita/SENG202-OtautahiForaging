package seng202.team4.unittests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.helpers.TestRecipeData;
import seng202.team4.models.Recipe;
import seng202.team4.models.api.ApiResponseWrapper;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdRecord;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdResponse;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchDetails;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchRecord;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchResponse;
import seng202.team4.repository.RecipeRepository;
import seng202.team4.services.RecipeFactory;
import seng202.team4.services.api.RecipeClientInterface;
import seng202.team4.services.dataCentre.recipeData.RecipeService;
import seng202.team4.unittests.TestSuite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;


/**
 * Mostly tests getRecipesForFood, heres the branches
 * getRecipesForFood decides between database and api.
 * It essentially asks:
 * Do we have the recipe ids for the food?
 * Do have recent details for each recipe ID?
 *
 *
 */
class RecipeServiceTest extends TestSuite {

    RecipeRepository testRecipeRepository;
    RecipeClientInterface testRecipeClient;
    RecipeService testRecipeService;



    @BeforeEach
    void setUpTest() {
        testRecipeRepository = mock(RecipeRepository.class);
        testRecipeClient = mock(RecipeClientInterface.class);
        testRecipeService = new RecipeService(testRecipeRepository, testRecipeClient);
    }


    @Test
    void testIdsExistsAndDetailsUpToDate() {
        Recipe recipe = TestRecipeData.createRecipeModels().getFirst();

        when(testRecipeRepository.getRecipeIdsByFoodName("Apple"))
                .thenReturn(List.of(recipe.getId()));
        when(testRecipeRepository.recipeDetailsUpToDateAndExist(recipe.getId()))
                .thenReturn(true);
        when(testRecipeRepository.getOne(recipe.getId()))
                .thenReturn(recipe);

        List<Recipe> recipes = testRecipeService.getRecipeSummariesByFood("Apple");

        assertEquals(List.of(recipe), recipes);

        // database used
        verify(testRecipeRepository).getRecipeIdsByFoodName("Apple");
        verify(testRecipeRepository).recipeDetailsUpToDateAndExist(recipe.getId());
        verify(testRecipeRepository).getOne(recipe.getId());

        // details up to date
        verify(testRecipeRepository, never()).saveRecipe(any(Recipe.class));
    }

    @Test
    void testIdsExistsButDetailsNotUpToDate() {
        RecipeByIdRecord recipeRecord = TestRecipeData.createFakeRecipeByIdRecord();

        when(testRecipeRepository.getRecipeIdsByFoodName("Apple"))
                .thenReturn(List.of(recipeRecord.recipeId()));
        when(testRecipeRepository.recipeDetailsUpToDateAndExist(recipeRecord.recipeId()))
                .thenReturn(false);
        when(testRecipeClient.getRecipeById(recipeRecord.recipeId()))
                .thenReturn(new ApiResponseWrapper<>(recipeByIdResponse(recipeRecord), 200));

        List<Recipe> recipes = testRecipeService.getRecipeSummariesByFood("Apple");


        assertEquals(recipeRecord.recipeId(), recipes.getFirst().getId());

        // database used
        verify(testRecipeRepository).getRecipeIdsByFoodName("Apple");

        // api used just for details
        verify(testRecipeClient, never()).searchRecipe(anyString(), anyBoolean(), anyInt());
        verify(testRecipeClient).getRecipeById(recipeRecord.recipeId());

        // details are not up to date
        verify(testRecipeRepository).saveRecipe(any(Recipe.class));
    }

    @Test
    void testIdsDontExistAndApiReturnsIds() {
        RecipeByIdRecord recipeRecord = TestRecipeData.createFakeRecipeByIdRecord();
        Recipe expectedRecipe = RecipeFactory.createRecipeObjectById(recipeRecord);

        when(testRecipeRepository.getRecipeIdsByFoodName("Apple"))
                .thenReturn(List.of());
        when(testRecipeRepository.recipeDetailsUpToDateAndExist(recipeRecord.recipeId()))
                .thenReturn(false, false, true);
        when(testRecipeClient.searchRecipe("Apple", true, 20))
                .thenReturn(new ApiResponseWrapper<>(searchResponseWithRecipeId(recipeRecord.recipeId()), 200));
        when(testRecipeClient.getRecipeById(recipeRecord.recipeId()))
                .thenReturn(new ApiResponseWrapper<>(recipeByIdResponse(recipeRecord), 200));
        when(testRecipeRepository.getOne(recipeRecord.recipeId()))
                .thenReturn(expectedRecipe);

        List<Recipe> recipes = testRecipeService.getRecipeSummariesByFood("Apple");

        assertEquals(recipeRecord.recipeId(), recipes.getFirst().getId());

        //api used
        verify(testRecipeClient).searchRecipe("Apple", true, 20);
        verify(testRecipeClient).getRecipeById(recipeRecord.recipeId());

        //database used to store
        verify(testRecipeRepository).addRecipeUseFood("Apple", recipeRecord.recipeId());
        verify(testRecipeRepository).saveRecipe(any(Recipe.class));
    }

    @Test
    void testIdsDontExistButApiThrowsErrorsOnSearch() {
        RecipeByIdRecord recipeRecord = TestRecipeData.createFakeRecipeByIdRecord();

        when(testRecipeRepository.getRecipeIdsByFoodName("Apple"))
                .thenReturn(List.of());
        when(testRecipeRepository.recipeDetailsUpToDateAndExist(recipeRecord.recipeId()))
                .thenReturn(false);
        when(testRecipeClient.searchRecipe("Apple", true, 20))
                .thenReturn(new ApiResponseWrapper<>(null, 500));

        List<Recipe> recipes = testRecipeService.getRecipeSummariesByFood("Apple");

        assertTrue(recipes.isEmpty());

        //Api is searched, but can't call recipe by id
        verify(testRecipeClient).searchRecipe("Apple", true, 20);
        verify(testRecipeClient, never()).getRecipeById(anyInt());


        //Database isnt used to store anything
        verify(testRecipeRepository, never()).saveRecipe(any(Recipe.class));
        verify(testRecipeRepository, never()).addRecipeUseFood(anyString(), anyInt());


    }

    @Test
    void testIdsAreFoundAtApiButApiThrowsErrorWhenLookingForMoreDetail() {
        RecipeByIdRecord recipeRecord = TestRecipeData.createFakeRecipeByIdRecord();

        when(testRecipeRepository.getRecipeIdsByFoodName("Apple"))
                .thenReturn(List.of());
        when(testRecipeRepository.recipeDetailsUpToDateAndExist(recipeRecord.recipeId()))
                .thenReturn(false);
        when(testRecipeClient.searchRecipe("Apple", true, 20))
                .thenReturn(new ApiResponseWrapper<>(searchResponseWithRecipeId(recipeRecord.recipeId()), 200));
        when(testRecipeClient.getRecipeById(recipeRecord.recipeId()))
                .thenReturn(new ApiResponseWrapper<>(null, 500));

        List<Recipe> recipes = testRecipeService.getRecipeSummariesByFood("Apple");

        assertTrue(recipes.isEmpty());

        // Api is searched and ids are found
        verify(testRecipeClient).searchRecipe("Apple", true, 20);
        // Details about ids are searched but error
        verify(testRecipeClient, atLeastOnce()).getRecipeById(recipeRecord.recipeId());

        // Database is not used to store incomplete data
        verify(testRecipeRepository, never()).saveRecipe(any(Recipe.class));
        verify(testRecipeRepository, never()).addRecipeUseFood(anyString(), anyInt());


    }




    private RecipeSearchResponse searchResponseWithRecipeId(int recipeId) {
        RecipeSearchRecord recipeSearchRecord = new RecipeSearchRecord(
                recipeId,
                "Recipe Name",
                "Description",
                null,
                null
        );

        return new RecipeSearchResponse(
                new RecipeSearchDetails(1, List.of(recipeSearchRecord)),
                null
        );
    }

    private RecipeByIdResponse recipeByIdResponse(RecipeByIdRecord recipeRecord) {
        return new RecipeByIdResponse(recipeRecord);
    }

    @Test
    void testCreateRecipeObjectById() {
        RecipeByIdRecord testByIdRecord = TestRecipeData.createFakeRecipeByIdRecord();
        Recipe testRecipeRecord = RecipeFactory.createRecipeObjectById(testByIdRecord);
        List<String> testIngredients = List.of("1 Apple");
        Recipe expectedRecord  = new Recipe(123, "Recipe Name", "1: Direction step 1\n2: Direction step 2", testIngredients, 345, 123,567, 789, "Description", null, "Image URL", 10, 10);
        assertEquals(expectedRecord, testRecipeRecord);
    }

}
