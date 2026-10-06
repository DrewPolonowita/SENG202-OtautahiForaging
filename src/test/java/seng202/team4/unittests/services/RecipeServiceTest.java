package seng202.team4.unittests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.helpers.TestRecipeData;
import seng202.team4.models.Recipe;
import seng202.team4.models.RecipeSummary;
import seng202.team4.models.api.ApiResponseWrapper;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdRecord;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdResponse;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchDetails;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchRecord;
import seng202.team4.models.api.Recipe.SearchRecipe.RecipeSearchResponse;
import seng202.team4.repository.RecipeRepository;
import seng202.team4.repository.RecipeSummaryRepository;
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

    RecipeRepository mockRecipeRepository;
    RecipeSummaryRepository mockRecipeSummaryRepository;
    RecipeClientInterface mockRecipeClient;
    RecipeService testRecipeService;

    @BeforeEach
    void setUpTest() {
        mockRecipeRepository = mock(RecipeRepository.class);
        mockRecipeSummaryRepository = mock(RecipeSummaryRepository.class);
        mockRecipeClient = mock(RecipeClientInterface.class);

        testRecipeService = new RecipeService(mockRecipeRepository, mockRecipeSummaryRepository, mockRecipeClient);
    }


    @Test
    void getRecipeSummariesUseDatabaseWhenTheyExist() {
        List<RecipeSummary> testSummaries = TestRecipeData.createRecipeSummaries();

        when(mockRecipeSummaryRepository.getRecipeSummariesByFoodName("Apple"))
                .thenReturn(testSummaries);

        List<RecipeSummary> result = testRecipeService.getRecipeSummariesByFood("Apple");

        assertEquals(testSummaries, result);

        // database used
        verify(mockRecipeSummaryRepository).getRecipeSummariesByFoodName("Apple");

        // dont search api
        verify(mockRecipeClient, never()).searchRecipe(anyString(), anyBoolean(), anyInt());
    }

    @Test
    void getRecipeSummariesByFoodUsesApiWhenDatabaseIsEmpty() throws DuplicateEntryException {

        RecipeSearchRecord searchRecord = TestRecipeData.createRecipeSearchRecords().getFirst();

        when(mockRecipeSummaryRepository.getRecipeSummariesByFoodName("Apple"))
                .thenReturn(List.of());
        when(mockRecipeClient.searchRecipe("Apple", true, 20))
                .thenReturn(new ApiResponseWrapper<>(new RecipeSearchResponse(
                        new RecipeSearchDetails(1, List.of(searchRecord)), null), 200));

        List<RecipeSummary> result = testRecipeService.getRecipeSummariesByFood("Apple");

        assertEquals(1, result.size());
        assertEquals(searchRecord.id(), result.getFirst().getRecipeId());

        verify(mockRecipeClient).searchRecipe("Apple", true, 20);
        verify(mockRecipeSummaryRepository).add(any(RecipeSummary.class));
        verify(mockRecipeSummaryRepository).addRecipeUseFood("Apple", searchRecord.id());

        // test change to architecture, not getting recipe details
        verify(mockRecipeClient, never()).getRecipeById(anyInt());
    }


    @Test
    void getRecipeSummariesReturnsEmptyWhenApiSearchFails() {
        when(mockRecipeSummaryRepository.getRecipeSummariesByFoodName("Apple"))
                .thenReturn(List.of());
        when(mockRecipeClient.searchRecipe("Apple", true, 20))

                .thenReturn(new ApiResponseWrapper<>(null, 500));
        List<RecipeSummary> result =
                testRecipeService.getRecipeSummariesByFood("Apple");

        assertTrue(result.isEmpty());

        verify(mockRecipeClient).searchRecipe("Apple", true, 20);
        verify(mockRecipeClient, never()).getRecipeById(anyInt());

        verify(mockRecipeSummaryRepository, never()).addRecipeUseFood(anyString(), anyInt());
    }

    @Test
    void getRecipeByIdUsesDatabaseWhenDetailsUpToDate() {
        Recipe recipe = TestRecipeData.createRecipeModels().getFirst();

        when(mockRecipeRepository.recipeDetailsUpToDateAndExist(recipe.getId()))
                .thenReturn(true);
        when(mockRecipeRepository.getOne(recipe.getId()))
                .thenReturn(recipe);

        Recipe result = testRecipeService.getRecipeById(recipe.getId());

        assertEquals(recipe, result);
        //doesnt call api
        verify(mockRecipeClient, never()).getRecipeById(anyInt());

        //uses the database
        verify(mockRecipeRepository).recipeDetailsUpToDateAndExist(recipe.getId());
        verify(mockRecipeRepository).getOne(recipe.getId());
    }

    @Test
    void testRecipeByIdUsesApiDetailsNotUpToDate() {
        RecipeByIdRecord recipeRecord = TestRecipeData.createFakeRecipeByIdRecord();

        when(mockRecipeRepository.recipeDetailsUpToDateAndExist(recipeRecord.recipeId()))
                .thenReturn(false);
        when(mockRecipeClient.getRecipeById(recipeRecord.recipeId()))
                .thenReturn(new ApiResponseWrapper<>(new RecipeByIdResponse(recipeRecord), 200));

        Recipe result = testRecipeService.getRecipeById(recipeRecord.recipeId());

        assertEquals(recipeRecord.recipeId(), result.getId());

        // api used just for details
        verify(mockRecipeClient, never()).searchRecipe(anyString(), anyBoolean(), anyInt());
        verify(mockRecipeClient).getRecipeById(recipeRecord.recipeId());

        // details are not up to date
        verify(mockRecipeRepository).saveRecipe(any(Recipe.class));
    }

    @Test
    void getRecipeByIdReturnsNullWhenApiFails() {
        int recipeId = 123;

        when(mockRecipeRepository.recipeDetailsUpToDateAndExist(recipeId))
                .thenReturn(false);
        when(mockRecipeClient.getRecipeById(recipeId))
                .thenReturn(new ApiResponseWrapper<>(null, 500));

        Recipe result = testRecipeService.getRecipeById(recipeId);

        assertEquals(null, result);

        verify(mockRecipeRepository, never()).saveRecipe(any(Recipe.class));
    }


    @Test
    void testCreateRecipeObjectById() {
        RecipeByIdRecord testByIdRecord = TestRecipeData.createFakeRecipeByIdRecord();
        Recipe actual = RecipeFactory.createRecipeObjectById(testByIdRecord);
        Recipe expected = RecipeFactory.create(
                123,
                "Recipe Name",
                "1: Direction step 1\n2: Direction step 2\n",
                List.of("1 Apple"),
                345,
                123,
                567,
                789,
                "Description",
                null,
                "Image URL",
                10,
                10
        );
        assertEquals(expected, actual);
    }

    @Test
    void existingRecipeSummaryIsUpdatedWhenAddThrowsDuplicateEntryException() throws DuplicateEntryException {
        RecipeSearchRecord searchRecord = TestRecipeData.createRecipeSearchRecords().getFirst();

        when(mockRecipeSummaryRepository.getRecipeSummariesByFoodName("Apple"))
                .thenReturn(List.of());
        when(mockRecipeClient.searchRecipe("Apple", true, 20))
                .thenReturn(new ApiResponseWrapper<>(new RecipeSearchResponse(
                                new RecipeSearchDetails(1, List.of(searchRecord)), null), 200));
        when(mockRecipeSummaryRepository.add(any(RecipeSummary.class)))
                .thenThrow(new DuplicateEntryException("Already exists"));

        testRecipeService.getRecipeSummariesByFood("Apple");

        verify(mockRecipeSummaryRepository).update(any(RecipeSummary.class));
        verify(mockRecipeSummaryRepository).addRecipeUseFood("Apple", searchRecord.id());
    }

}
