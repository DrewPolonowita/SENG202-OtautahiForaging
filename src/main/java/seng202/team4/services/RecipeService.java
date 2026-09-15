package seng202.team4.services;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.models.Recipe;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeByIdRecord;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeDirection;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeIngredient;
import seng202.team4.repository.RecipeRepository;
import seng202.team4.services.api.ApiClient;
import seng202.team4.services.api.FoodApiClient;
import seng202.team4.services.api.FoodClientInterface;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * Service class to handle business logic managing recipes
 * @author Team 4
 */
public class RecipeService {
    private static final Logger log = LogManager.getLogger(RecipeService.class);
    private final FoodClientInterface foodApiClient;
    private final RecipeRepository recipeDAO;


    /**
     * Constructor
     */
    public RecipeService(RecipeRepository recipeRepository) {
        this.foodApiClient = new FoodApiClient(new ApiClient());
        this.recipeDAO = recipeRepository;
    }


    /**
     * Creates a recipe using a RecipeByIdRecord
     * @param recipeRecord a RecipeByIdRecord
     * @return a Recipe model
     */
    private Recipe createRecipeObjectById(RecipeByIdRecord recipeRecord) {
        int recipeId = recipeRecord.recipeId();
        String recipeName = recipeRecord.recipeName();
        String method = buildMethodString(recipeRecord.directions());
        List<String> ingredients = getIngredientString(recipeRecord.ingredients());




        double carbohydrates = recipeRecord.servingSizes().serving().carbohydrate();
        double calories = recipeRecord.servingSizes().serving().calories();
        double protein = recipeRecord.servingSizes().serving().protein();
        double fat = recipeRecord.servingSizes().serving().fat();
        String description = recipeRecord.recipeDescription();
        String imageUrl = recipeRecord.imageUrl();
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
                null,
                imageUrl,
                cookingTime,
                preparationTime
        );
    }


    private List<String> getIngredientString(List<RecipeIngredient> ingredients) {
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
        directions.sort(Comparator.comparing(RecipeDirection::directionNumber));
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < directions.size(); i++) {
            stringBuilder.append(directions.get(i).directionNumber());
            stringBuilder.append(": ").append(directions.get(i).directionDescription()).append("\n");
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
        return null; //todo: Implement
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
        List<Integer> databaseRecipesId = recipeDAO.getRecipeIdByFoodName(food);
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

