package seng202.team4.services.dataCentre.recipeData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.models.Recipe;
import seng202.team4.models.RecipeSummary;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdRecord;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeIngredient;
import seng202.team4.repository.RecipeRepository;
import seng202.team4.repository.RecipeSummaryRepository;
import seng202.team4.services.api.RecipeClientInterface;
import seng202.team4.services.dataCentre.LoadingCache;


import java.util.List;

import static seng202.team4.services.RecipeFactory.createRecipeObjectById;


/**
 * Service class to handle business logic managing recipes
 * @author Team 4
 */
public class RecipeService implements RecipeDataManager {
    private static final Logger log = LogManager.getLogger(RecipeService.class);
    private final RecipeRepository recipeDAO;
    private final RecipeSummaryRepository recipeSummaryDAO;
    private final RecipeApiHandler recipeApiHandler;
    private final LoadingCache<Integer, Recipe> recipeCache;
    private final LoadingCache<String, List<RecipeSummary>> recipeSummariesByFoodCache;

    /**
     * Constructor
     */
    public RecipeService(RecipeRepository recipeRepository, RecipeSummaryRepository recipeSummaryDAO, RecipeClientInterface recipeClientInterface) {
        this.recipeDAO = recipeRepository;
        this.recipeSummaryDAO = recipeSummaryDAO;
        this.recipeApiHandler = new RecipeApiHandler(recipeClientInterface);
        this.recipeCache = new LoadingCache<>(
                this::getRecipeFromDatabase,
                this::getRecipeFromApi);
        this.recipeSummariesByFoodCache = new LoadingCache<>(
                this::getRecipeSummariesByFoodFromDatabase,
                this::getRecipeSummariesForFoodByApi
        );
    }

    /**
     * method to get recipe from database for use with LoadingCache
     * @param id recipe being looked for
     * @return Recipe returned from database or null if there is none
     */
    private Recipe getRecipeFromDatabase(int id) {
        if (recipeDAO.recipeDetailsUpToDateAndExist(id)) {
            log.info("loaded from database");
            return recipeDAO.getOne(id);
        }
        return null;
    }

    /**
     * method to get recipe from api, and store it, for use with loading cache
     * @param id recipe being looked for
     * @return recipe returned from api or null if can't be found
     */
    private Recipe getRecipeFromApi(int id) {
        RecipeByIdRecord recipeRecord = recipeApiHandler.getRecipeById(id);
        log.info("Loaded recipe {} from api", id);

        if (recipeRecord == null) {
            return null;
        }

        Recipe recipe = createRecipeObjectById(recipeRecord);
        recipeDAO.saveRecipe(recipe);

        List<RecipeIngredient> ingredients = getRecipeIngredients(recipeRecord);
        populateIngredientTable(ingredients);
        populateIngredientInRecipeTable(ingredients, recipe);

        return recipe;
    }

    /**
     * method to get recipe summaries from the database for use with loading cache
     * @param food the food type to query
     * @return summaries from database, or null if we don't have any
     */
    @SuppressWarnings("squid:S1168") // null is used to trigger the api
    private List<RecipeSummary> getRecipeSummariesByFoodFromDatabase(String food) {
        List<RecipeSummary> summaries =
                recipeSummaryDAO.getRecipeSummariesByFoodName(food);

        if (summaries.isEmpty()) {
            return null;
        }

        return summaries;
    }

    /**
     * method to get list of {@link RecipeSummary} from the api for use with loading cache
     * @param food the food type to query
     * @return summaries from api
     */
    private List<RecipeSummary> getRecipeSummariesForFoodByApi(String food) {
        List<RecipeSummary> summaries = recipeApiHandler.getRecipeSummariesByFoodFromApi(food);

        for (RecipeSummary summary : summaries) {
            saveRecipeSummary(summary);
            recipeSummaryDAO.addRecipeUseFood(food, summary.getRecipeId());
        }

        return summaries;
    }

    /**
     * Saves the recipe summary in the database with up to date or new details
     * @param summary summary
     */
    private void saveRecipeSummary(RecipeSummary summary) {
        try {
            recipeSummaryDAO.add(summary);
        } catch (DuplicateEntryException _) {
            recipeSummaryDAO.update(summary);
        }
    }

    /**
     * Get ingredients from api record
     *
     * @param recipeRecord api recipe record
     * @return ingredient list, if api returned none then an empty list
     */
    private List<RecipeIngredient> getRecipeIngredients(RecipeByIdRecord recipeRecord) {
        if (recipeRecord.ingredients() == null) {
            return List.of();
        }
        return recipeRecord.ingredients().ingredient();
    }

    /**
     * Method to get list of {@link RecipeSummary} for a particular food.
     * A recipe summary has just recipe name, id, and image
     * We try and use the stored summaries, if they aren't available or out of date we use api
     * @param food A food specifically from the food groups we map
     * @return list of recipes summaries
     */
    @Override
    public List<RecipeSummary> getRecipeSummariesByFood(String food) {
        return recipeSummariesByFoodCache.get(food);
    }

    /**
     * Get a specific recipe by id, either from the database if we have up to date cached data within 24hrs
     * or from the api if not.
     * @param id id of the recipe we are looking for
     * @return Recipe we get from the database or api
     */
    @Override
    public Recipe getRecipeById(int id) {
        return recipeCache.get(id);
    }

    /**
     * Populates the ingredients table in the database
     * @param ingredients a list of ingredients as RecipeIngredient objects
     */
    public void populateIngredientTable(List<RecipeIngredient> ingredients) {
        for (RecipeIngredient ingredient : ingredients) {
            recipeDAO.addIngredient(ingredient);
        }
    }

    /**
     * Populates the ingredients in recipe table in the database
     * @param ingredients a list of ingredients as RecipeIngredient objects
     * @param recipe the recipe with the ingredients
     */
    private void populateIngredientInRecipeTable(List<RecipeIngredient> ingredients, Recipe recipe) {
        for (RecipeIngredient ingredient : ingredients) {
            recipeDAO.addIngredientsByRecipe(ingredient, recipe);
        }
    }
}

