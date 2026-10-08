package seng202.team4.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import seng202.team4.helpers.TestRecipeData;
import seng202.team4.models.Recipe;
import seng202.team4.models.api.ApiResponseWrapper;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdRecord;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdResponse;
import seng202.team4.repository.RecipeRepository;
import seng202.team4.repository.RecipeSummaryRepository;
import seng202.team4.services.api.RecipeClientInterface;
import seng202.team4.services.dataCentre.recipeData.RecipeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AT_23 TODO: Update AT number
 */
public class RecipeExpiryStepDefs {
    private RecipeRepository mockRecipeRepository;
    private RecipeSummaryRepository mockRecipeSummaryRepository;
    private RecipeClientInterface mockRecipeClient;
    private RecipeService testRecipeService;

    private Recipe result;
    private Recipe expectedRecipe;
    private int recipeId;

    @Before
    public void setUp() {
        mockRecipeRepository = mock(RecipeRepository.class);
        mockRecipeSummaryRepository = mock(RecipeSummaryRepository.class);
        mockRecipeClient = mock(RecipeClientInterface.class);

        testRecipeService = new RecipeService(
                mockRecipeRepository,
                mockRecipeSummaryRepository,
                mockRecipeClient
        );
    }

    @Given("recipe {int} has database data less than 24 hours old")
    public void recipeHasUpToDateData(int id) {
        recipeId = id;
        expectedRecipe = TestRecipeData.createRecipeModels().getFirst();

        assertEquals(recipeId, expectedRecipe.getId());

        when(mockRecipeRepository.recipeDetailsUpToDateAndExist(id))
                .thenReturn(true);

        when(mockRecipeRepository.getOne(id))
                .thenReturn(expectedRecipe);
    }

    @Given("recipe {int} has database data older than 24 hours")
    public void recipeHasOldData(int id) {
        recipeId = id;

        when(mockRecipeRepository.recipeDetailsUpToDateAndExist(id))
                .thenReturn(false);
    }

    @Given("the recipe API provides new data")
    public void apiProvidesNewData() {
        RecipeByIdRecord recipeRecord = TestRecipeData.createFakeRecipeByIdRecord();

        assertEquals(recipeId, recipeRecord.recipeId());

        expectedRecipe = seng202.team4.services.RecipeFactory.createRecipeObjectById(recipeRecord);

        when(mockRecipeClient.getRecipeById(recipeId))
                .thenReturn(new ApiResponseWrapper<>(new RecipeByIdResponse(recipeRecord), 200));
    }

    @When("the recipe is requested")
    public void recipeIsRequested() {
        result = testRecipeService.getRecipeById(recipeId);
    }

    @Then("the recipe is returned from database")
    public void recipeReturnedFromDatabase() {
        assertEquals(expectedRecipe, result);

        verify(mockRecipeRepository).getOne(recipeId);
    }

    @Then("the api is not called")
    public void apiIsNotCalled() {
        verify(mockRecipeClient, never()).getRecipeById(anyInt());
    }

    @Then("the updated recipe is returned")
    public void updatedRecipeReturned() {
        assertEquals(expectedRecipe, result);
        verify(mockRecipeClient).getRecipeById(recipeId);
    }

    @Then("the updated recipe is saved in database")
    public void updatedRecipeSaved() {
        verify(mockRecipeRepository).saveRecipe(any(Recipe.class));
    }
}
