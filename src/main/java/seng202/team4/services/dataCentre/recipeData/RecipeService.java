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


    /**
     * Constructor
     */
    public RecipeService(RecipeRepository recipeRepository, RecipeClientInterface recipeClientInterface) {
        this.recipeDAO = recipeRepository;
        recipeApiHandler = new RecipeApiHandler(recipeClientInterface);
    }

    /**
     * Method to get recipes for a particular food.
     * First we check for if we are storing id's that are mapped to that food, if not
     * Call the api using the recipe search function.
     * Then check if the details for those ids are up to date
     * if not call the api using get recipe by id function
     * @param food A food specifically from the food groups we map
     * @return list of recipes
     */
    @Override
    public List<Recipe> getRecipesForFood(String food) {
        List<Integer> recipeIds = recipeDAO.getRecipeIdsByFoodName(food);
        List<Recipe> recipes = new ArrayList<>();

        boolean noIdsExist = recipeIds.isEmpty();
        if (noIdsExist) {
            recipeIds = recipeApiHandler.getRecipeIdsByFoodFromApi(food);
        }
        for (int id : recipeIds) {
            Recipe recipe = getRecipeById(id);

            if (noIdsExist) {
                recipeDAO.addRecipeUseFood(food, id);
            }
            recipes.add(recipe);

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
        if (recipeDAO.recipeDetailsUpToDateAndExist(id)) {
            return recipeDAO.getOne(id);

        } else {
            RecipeByIdRecord recipeRecord = recipeApiHandler.getRecipeById(id);
            if (recipeRecord == null) {
                return null;
            }
            Recipe recipe = createRecipeObjectById(recipeRecord);
            recipeDAO.replaceRecipe(recipe);
            populateIngredientTable(recipeRecord.ingredients() == null ? List.of() : recipeRecord.ingredients().ingredient());
            populateIngredientInRecipeTable(recipeRecord.ingredients() == null ? List.of() : recipeRecord.ingredients().ingredient(), recipe);
            return recipe;
        }
    }


    /**
     * Creates a recipe using a RecipeByIdRecord
     * @param recipeRecord a RecipeByIdRecord
     * @return a Recipe model
     */
    public Recipe createRecipeObjectById(RecipeByIdRecord recipeRecord) {
        int recipeId = recipeRecord.recipeId();
        String recipeName = recipeRecord.recipeName();
        String method = buildMethodString(recipeRecord.directions() == null ? List.of() : recipeRecord.directions().direction());
        List<String> ingredients = getIngredientString(recipeRecord.ingredients() == null ? List.of() : recipeRecord.ingredients().ingredient());




        double carbohydrates = recipeRecord.servingSizes().serving().carbohydrate();
        double calories = recipeRecord.servingSizes().serving().calories();
        double protein = recipeRecord.servingSizes().serving().protein();
        double fat = recipeRecord.servingSizes().serving().fat();
        String description = recipeRecord.recipeDescription();
        String imageUrl = null;
        if (recipeRecord.images() != null && recipeRecord.images().imageUrl() != null && !recipeRecord.images().imageUrl().isEmpty()) {
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
        List<RecipeDirection> sortedDirections = directions.stream().sorted(Comparator.comparing(RecipeDirection::directionNumber)).toList();

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < sortedDirections.size(); i++) {
            stringBuilder.append(sortedDirections.get(i).directionNumber());
            stringBuilder.append(": ").append(sortedDirections.get(i).directionDescription()).append("\n");
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
            recipeDAO.addIngredients(ingredient);
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
     * Method for loading recipes from database by food they contain
     * @param food food that should be in the ingredients list of a recipe
     * @return a list of recipes that have the specified food as an ingredient, or null if none in database
     */
    public List<Recipe> getRecipeByFood(String food) {
        List<Integer> databaseRecipesId = recipeDAO.getRecipeIdsByFoodName(food);
        List<Recipe> databaseRecipes = new ArrayList<>();
        for (int id : databaseRecipesId) {
            databaseRecipes.add(recipeDAO.getOne(id));
        }
        if (!databaseRecipes.isEmpty()) {
            log.info("Loaded {} {} recipes from database", databaseRecipes.size(), food);
            return databaseRecipes;
        }
        return null;
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
            } catch (DuplicateEntryException e) {
                log.warn("(All Recipes) Skip duplicate id: {}", recipe.getId());
            }
        }
        databaseRecipes = recipeDAO.getAll();
        return databaseRecipes;
    }
}

