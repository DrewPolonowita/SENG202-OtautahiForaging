package seng202.team4.services.dataCentre.recipeData;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.models.Recipe;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdRecord;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeDirection;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeIngredient;
import seng202.team4.repository.RecipeRepository;
import seng202.team4.services.api.RecipeClientInterface;
import seng202.team4.services.dataCentre.LoadingCache;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;



/**
 * Service class to handle business logic managing recipes
 * @author Team 4
 */
public class RecipeService implements RecipeDataManager {
    private static final Logger log = LogManager.getLogger(RecipeService.class);
    private final RecipeRepository recipeDAO;
    private final RecipeApiHandler recipeApiHandler;
    private final LoadingCache<Integer, Recipe> recipeCache;
    private final LoadingCache<String, List<Integer>> recipeIdsByFoodCache;

    /**
     * Constructor
     */
    public RecipeService(RecipeRepository recipeRepository, RecipeClientInterface recipeClientInterface) {
        this.recipeDAO = recipeRepository;
        this.recipeApiHandler = new RecipeApiHandler(recipeClientInterface);
        this.recipeCache = new LoadingCache<>(
                this::getRecipeFromDatabase,
                this::getRecipeFromApi);
        this.recipeIdsByFoodCache = new LoadingCache<>(
                this::getRecipeIdsForFoodFromDatabase,
                this::getRecipeIdsForFoodFromApi
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
     * @return recipe returned from database
     */
    private Recipe getRecipeFromApi(int id) {
        RecipeByIdRecord recipeRecord = recipeApiHandler.getRecipeById(id);
        log.info("Loaded recipe {} from api", id);

        if (recipeRecord == null) {
            return null;
        }

        Recipe recipe = createRecipeObjectById(recipeRecord);
        recipeDAO.replaceRecipe(recipe);

        List<RecipeIngredient> ingredients = getRecipeIngredients(recipeRecord);
        populateIngredientTable(ingredients);
        populateIngredientInRecipeTable(ingredients, recipe);

        return recipe;
    }

    /**
     * method to get recipe ids from the database for use with loading cache
     * @param food the food type to query
     * @return ids from database, or null if we don't have any
     */
    @SuppressWarnings("squid:S1168") // null is used to trigger the api
    private List<Integer> getRecipeIdsForFoodFromDatabase(String food) {
        List<Integer> recipeIds = recipeDAO.getRecipeIdsByFoodName(food);
        if (recipeIds.isEmpty()) {
            return null;
        }
        return recipeIds;
    }

    /**
     * method to get ids from the api for use with loading cache
     * @param food the food type to query
     * @return ids from api
     */
    private List<Integer> getRecipeIdsForFoodFromApi(String food) {
        List<Integer> recipeIds = recipeApiHandler.getRecipeIdsByFoodFromApi(food);
        log.info("Loaded {} recipes from api", recipeIds.size());

        for (int id : recipeIds) {
            Recipe recipe = getRecipeById(id);
            if (recipe != null) {
                recipeDAO.addRecipeUseFood(food, id);
            }
        }
        return recipeIds;
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
     * Method to get recipes for a particular food.
     * First we check for if we are storing id's that are mapped to that food, if not
     * Call the api using the recipe search function.
     * Then check if the details for those ids are up-to-date
     * if not call the api using get recipe by id function.
     * This functionality is effectively handled by loading cache now
     * @param food A food specifically from the food groups we map
     * @return list of recipes
     */
    @Override
    public List<Recipe> getRecipesForFood(String food) {
        List<Integer> recipeIds = recipeIdsByFoodCache.get(food);
        List<Recipe> recipes = new ArrayList<>();

        if (recipeIds == null) {
            return recipes;
        }

        for (int id : recipeIds) {
            Recipe recipe = getRecipeById(id);
            if (recipe != null) {
                recipes.add(recipe);
            }
        }
        return recipes;
    }

    /**
     * Get a specific recipe by id, either from the database if we have up to date cached data within 24hrs
     * or from the api if not.
     * @param id id of the recipe we are looking for
     * @return Recipe we get from the database or api
     */
    private Recipe getRecipeById(int id) {
        return recipeCache.get(id);
    }


    /**
     * Creates a recipe using a RecipeByIdRecord
     * @param recipeRecord a RecipeByIdRecord
     * @return a Recipe model
     */
    public Recipe createRecipeObjectById(RecipeByIdRecord recipeRecord) {
        int recipeId = recipeRecord.recipeId();
        String recipeName = recipeRecord.recipeName();
        String method = buildMethodString(recipeRecord.directions()
                == null ? List.of() : recipeRecord.directions().direction());
        List<String> ingredients = getIngredientString(recipeRecord.ingredients()
                == null ? List.of() : recipeRecord.ingredients().ingredient());




        double carbohydrates = recipeRecord.servingSizes().serving().carbohydrate();
        double calories = recipeRecord.servingSizes().serving().calories();
        double protein = recipeRecord.servingSizes().serving().protein();
        double fat = recipeRecord.servingSizes().serving().fat();
        String description = recipeRecord.recipeDescription();
        String imageUrl = null;
        if (recipeRecord.images()
                != null && recipeRecord.images().imageUrl()
                != null && !recipeRecord.images().imageUrl().isEmpty()
        ) {
            imageUrl = recipeRecord.images().imageUrl().getFirst();
        }
        int cookingTime = recipeRecord.cookingTime();
        int preparationTime = recipeRecord.preparationTime();


        return new Recipe(
                recipeId,
                recipeName,
                method,
                ingredients,
                carbohydrates,
                calories,
                protein,
                fat,
                description,
                LocalDateTime.now(),
                imageUrl,
                cookingTime,
                preparationTime
        );
    }

    /**
     * Take RecipeIngredient objects and get all their ingredient descriptions
     * @param ingredients list of ingredients
     * @return a list of all the ingredient descriptions
     */
    public List<String> getIngredientString(List<RecipeIngredient> ingredients) {
        List<String> ingredientStrings = new ArrayList<>();
        for (RecipeIngredient ingredient : ingredients) {
            ingredientStrings.add(ingredient.ingredientDescription());
        }
        return ingredientStrings;
    }


    /**
     * Method to build string of recipe method with steps each on a new line
     * @param directions a list of RecipeDirection models
     * @return a string containing the recipe's method
     */
    private String buildMethodString(List<RecipeDirection> directions) {
        List<RecipeDirection> sortedDirections = directions
                .stream()
                .sorted(Comparator.comparing(RecipeDirection::directionNumber))
                .toList();

        StringBuilder stringBuilder = new StringBuilder();
        for (RecipeDirection sortedDirection : sortedDirections) {
            stringBuilder.append(sortedDirection.directionNumber());
            stringBuilder.append(": ").append(sortedDirection.directionDescription()).append("\n");
        }
        return stringBuilder.toString();
    }


    /**
     * Method to return all recipes from API
     * @return List of all recipes from API
     */
    public List<Recipe> getAllRecipesFromApi() {
        List<Recipe> allRecipes = new ArrayList<>();
        allRecipes.addAll(getRecipesFromApiByFood("Apple"));
        allRecipes.addAll(getRecipesFromApiByFood("Pear"));
        allRecipes.addAll(getRecipesFromApiByFood("Cherry"));
        allRecipes.addAll(getRecipesFromApiByFood("Peach"));
        allRecipes.addAll(getRecipesFromApiByFood("Apricot"));


        return allRecipes;
    }


    /**
     * Method for requesting recipes with a certain food ingredient from API
     * @param food ingredient specified in requested recipes
     * @return List of recipe models containing specified ingredient
     */
    private List<Recipe> getRecipesFromApiByFood(String food) {
        return getRecipesForFood(food);
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
     */
    public void populateIngredientInRecipeTable(List<RecipeIngredient> ingredients, Recipe recipe) {
        for (RecipeIngredient ingredient : ingredients) {
            recipeDAO.addIngredientsByRecipe(ingredient, recipe);
        }
    }


    /**
     * Return all the recipes in the database if the database is populated
     * otherwise populate it
     * @return return all recipes in database
     */
    public List<Recipe> getAllRecipes() {
        List<Recipe> databaseRecipes = recipeDAO.getAll();


        if (!databaseRecipes.isEmpty()) {
            log.info("Loaded {} recipes from database", databaseRecipes.size());
            return databaseRecipes;
        }


        List<Recipe> apiRecipes = getAllRecipesFromApi();
        log.info("Loaded {} recipes from api", apiRecipes.size());
        for (Recipe recipe: apiRecipes) {
            try {
                recipeDAO.add(recipe);
            } catch (DuplicateEntryException _) {
                log.warn("(All Recipes) Skip duplicate id: {}", recipe.getId());
            }
        }
        databaseRecipes = recipeDAO.getAll();
        return databaseRecipes;
    }
}

