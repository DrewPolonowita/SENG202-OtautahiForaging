package seng202.team4.repository;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.models.Recipe;
import seng202.team4.models.api.Recipe.RecipeByID.RecipeIngredient;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * DAO Interface implementation for recipe, handles the recipe related actions to the database
 */
public class RecipeDAO implements RecipeRepository {
    private static final Logger LOG = LogManager.getLogger(RecipeDAO.class);


    private final DatabaseManager databaseManager;


    /**
     * Constructor
     * @param databaseManager means of connecting to the database
     */
    public RecipeDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }


    /**
     * Method to return all recipes stored in the database
     * @return a list of Recipes, or an empty list if failed
     */
    @Override
    public List<Recipe> getAll() {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT "
                + "recipes.recipe_id,"
                + "recipes.recipe_name,"
                + "recipes.recipe_method, "
                + "GROUP_CONCAT(ingredients_in_recipe.ingredient_description) AS recipe_ingredients,"
                + "recipes.carbohydrates,"
                + "recipes.calories,"
                + "recipes.protein,"
                + "recipes.fat,"
                + "recipes.recipe_description,"
                + "recipes.retrieval_time,"
                + "recipes.recipe_image,"
                + "recipes.cooking_time_min,"
                + "recipes.preparation_time_min "
                + "FROM recipes JOIN ingredients_in_recipe "
                + "ON recipes.recipe_id = ingredients_in_recipe.recipe_id "
                + "GROUP BY recipes.recipe_id";
        try (Connection conn = databaseManager.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                String recipeIngredients = rs.getString("recipe_ingredients");
                List<String> ingredients = Arrays.asList(recipeIngredients.split(","));


                Timestamp timestamp = rs.getTimestamp("retrieval_time");
                LocalDateTime retrievalTime = retrieveTimestamp(timestamp);


                recipes.add(new Recipe(
                        rs.getInt("recipe_id"),
                        rs.getString("recipe_name"),
                        rs.getString("recipe_method"),
                        ingredients,
                        rs.getDouble("carbohydrates"),
                        rs.getDouble("calories"),
                        rs.getDouble("protein"),
                        rs.getDouble("fat"),
                        rs.getString("recipe_description"),
                        retrievalTime,
                        rs.getString("recipe_image"),
                        rs.getInt("cooking_time_min"),
                        rs.getInt("preparation_time_min")
                ));
            }
            return recipes;
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
            return new ArrayList<>();
        }
    }


    /**
     * Gets a single recipe from the database by its id
     * @param id the recipe id
     * @return recipe model that has the given id, null recipe doesn't exist
     */
    @Override
    public Recipe getOne(Integer id) {
        Recipe recipe = null;
        String sql = "SELECT "
                + "recipes.recipe_id,"
                + "recipes.recipe_name,"
                + "recipes.recipe_method, "
                + "GROUP_CONCAT(ingredients_in_recipe.ingredient_description) AS recipe_ingredients,"
                + "recipes.carbohydrates,"
                + "recipes.calories,"
                + "recipes.protein,"
                + "recipes.fat,"
                + "recipes.recipe_description,"
                + "recipes.retrieval_time,"
                + "recipes.recipe_image,"
                + "recipes.cooking_time_min,"
                + "recipes.preparation_time_min "
                + "FROM recipes JOIN ingredients_in_recipe "
                + "ON recipes.recipe_id = ingredients_in_recipe.recipe_id "
                + "WHERE recipes.recipe_id = ? "
                + "GROUP BY recipes.recipe_id";
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String recipeIngredients = rs.getString("recipe_ingredients");
                    List<String> ingredients = Arrays.asList(recipeIngredients.split(","));
                    Timestamp timestamp = rs.getTimestamp("retrieval_time");
                    LocalDateTime retrievalTime = retrieveTimestamp(timestamp);


                    recipe = new Recipe(
                            rs.getInt("recipe_id"),
                            rs.getString("recipe_name"),
                            rs.getString("recipe_method"),
                            ingredients,
                            rs.getDouble("carbohydrates"),
                            rs.getDouble("calories"),
                            rs.getDouble("protein"),
                            rs.getDouble("fat"),
                            rs.getString("recipe_description"),
                            retrievalTime,
                            rs.getString("recipe_image"),
                            rs.getInt("cooking_time_min"),
                            rs.getInt("preparation_time_min")
                    );
                }
            }
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
            return null;
        }
        return recipe;
    }

    /**
     * Add a recipe object to the database
     * @param toAdd recipe object to add
     * @return id of recipe if success, -1 if failed
     */
    @Override
    public int add(Recipe toAdd) throws DuplicateEntryException {
        String sql = """
                   INSERT INTO recipes (
                   recipe_id,
                   recipe_name,
                   recipe_method,
                   carbohydrates,
                   calories,
                   protein,
                   fat,
                   recipe_description,
                   retrieval_time,
                   recipe_image,
                   cooking_time_min,
                   preparation_time_min
                   ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?)
                   ON CONFLICT(recipe_id) DO UPDATE SET
                   recipe_name = excluded.recipe_name,
                   recipe_method = excluded.recipe_method,
                   carbohydrates = excluded.carbohydrates,
                   calories = excluded.calories,
                   protein = excluded.protein,
                   fat = excluded.fat,
                   recipe_description = excluded.recipe_description,
                   retrieval_time = CURRENT_TIMESTAMP,
                   recipe_image = excluded.recipe_image,
                   cooking_time_min = excluded.cooking_time_min,
                   preparation_time_min = excluded.preparation_time_min
                   ;
                   """;
        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setInt(1, toAdd.getId());
            ps.setString(2, toAdd.getName());
            ps.setString(3, toAdd.getMethod());
            ps.setDouble(4, toAdd.getCarbohydrates());
            ps.setDouble(5, toAdd.getCalories());
            ps.setDouble(6, toAdd.getProtein());
            ps.setDouble(7, toAdd.getFat());
            ps.setString(8, toAdd.getDescription());
            ps.setString(9, toAdd.getImage());
            ps.setInt(10, toAdd.getCookingTime());
            ps.setInt(11, toAdd.getPreparationTime());


            ps.executeUpdate();
            return toAdd.getId();
        } catch (SQLException sqlException) {
            if (sqlException.getMessage().contains("UNIQUE constraint failed")) {
                throw new DuplicateEntryException("Recipe exists with id " + toAdd.getId()
                        + " and name " + toAdd.getName());
            }
            LOG.error(sqlException);
            return -1;
        }
    }

    /**
     * Deletes a specific recipe from the database
     * @param id id of recipe to delete
     */
    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM recipes WHERE recipe_id=?";
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
        }
    }


    /**
     * Not implemented
     * @param toUpdate recipe object that needs to be updated
     * (this object must be able to identify itself and its previous self)
     */
    @Override
    public void update(Recipe toUpdate) {
        throw new NotImplementedException();
    }


    /**
     * Retrieve all recipes with a specified ingredient
     * @param food the name of the ingredient specified
     * @return all recipes with the food specified as an ingredient
     */
    public List<Integer> getRecipeIdsByFoodName(String food) {
        List<Integer> recipeIds = new ArrayList<>();
        String sql = "SELECT recipe_id FROM recipe_uses_food WHERE common_name=?";
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, food);


            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recipeIds.add(rs.getInt("recipe_id"));
                }
            }
            return recipeIds;
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
            return new ArrayList<>();
        }
    }


    /**
     * Retrieves timestamp of when data was last retrieved from recipe API
     * and converts it to a LocalDateTime
     * @param timestamp timestamp that data was last retrieved from recipe API
     * @return time data was retrieved from recipe API as a LocalDateTime if successful, else returns null
     */
    private LocalDateTime retrieveTimestamp(Timestamp timestamp) {
        LocalDateTime retrievalTime;
        if (timestamp != null) {
            retrievalTime = timestamp.toLocalDateTime();
        } else {
            retrievalTime = null;
        }
        return retrievalTime;
    }

    /**
     * Adds ingredients of a recipe to the database
     *
     * @param toAdd recipe object to add
     */
    public void addIngredients(RecipeIngredient toAdd) {
        String sql = """
                   INSERT OR IGNORE INTO ingredient (
                   ingredient_id,
                   serving_id,
                   ingredient_name,
                   ingredient_unit_number,
                   ingredient_measurement_description,
                   ingredient_description
                   ) VALUES (?,?,?,?,?,?);
                   """;
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, toAdd.ingredientId());
            ps.setInt(2, toAdd.servingId());
            ps.setString(3, toAdd.ingredientName());
            ps.setDouble(4, toAdd.ingredientUnitNumber());
            ps.setString(5, toAdd.ingredientMeasurementDescription());
            ps.setString(6, toAdd.ingredientDescription());


            ps.executeUpdate();
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
        }
    }

    /**
     * Adds ingredients alongside recipe id to the ingredients in recipe table in database
     * @param recipe a recipe model of the recipe the ingredients are from
     * @param toAdd a recipe ingredient to add information about to the database
     * @return a map mapping recipe id to ingredient and serving ids if successful, null otherwise
     */
    public Map<Integer, List<Integer>> addIngredientsByRecipe(RecipeIngredient toAdd, Recipe recipe) {
        String sql = """
                   INSERT OR IGNORE INTO ingredients_in_recipe (
                   recipe_id,
                   ingredient_id,
                   serving_id,
                   ingredient_description
                   
                   ) VALUES (?,?,?,?);
                   """;
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recipe.getId());
            ps.setInt(2, toAdd.ingredientId());
            ps.setInt(3, toAdd.servingId());
            ps.setString(4, toAdd.ingredientDescription());


            ps.executeUpdate();
            Map<Integer, List<Integer>> recipeFoodIdMap = new HashMap<>();
            List<Integer> ingredientServingId = new ArrayList<>();
            ingredientServingId.add(toAdd.ingredientId());
            ingredientServingId.add(toAdd.servingId());
            recipeFoodIdMap.put(recipe.getId(), ingredientServingId);
            return recipeFoodIdMap;
        } catch (SQLException sqlException) {
            LOG.error(sqlException);
            return null;
        }
    }

    /**
     * Function that checks if the data in the recipes tables has been updated in the last 24hrs, or exists
     * @param recipeId Recipe to check
     * @return True if the data is up to date and exists, false otherwise and something should be done
     */
    @Override
    public boolean recipeDetailsUpToDateAndExist(int recipeId) {
        String sql = """
                SELECT 1
                FROM recipes
                WHERE recipe_id = ?
                AND retrieval_time >= DATETIME('now', '-24 hours')
                """;
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recipeId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            LOG.error(e);
            return false;
        }
    }


    /**
     * Adds data about recipes using a specific food to the database table recipe uses food
     * @param food food to add
     * @param recipeId recipe id with the food in it
     */
    @Override
    public void addRecipeUseFood(String food, int recipeId) {
        String sql = """
                INSERT OR IGNORE INTO recipe_uses_food (
                   common_name,
                   recipe_id
                   ) VALUES (?,?);
                """;
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, food);
            ps.setInt(2, recipeId);


            ps.executeUpdate();

        } catch (SQLException sqlException) {
            LOG.error(sqlException);
        }
    }

    /**
     * Replaces recipes data that exists in the database but have to be updated to comply with FatSecret API data storage time requirements
     * @param recipe recipe needing to be replaced
     */
    @Override
    public void replaceRecipe(Recipe recipe) {
        String sql = """
                UPDATE recipes
                SET recipe_name = ?,
                    recipe_method = ?,
                    carbohydrates = ?,
                    calories = ?,
                    protein = ?,
                    fat = ?,
                    recipe_description = ?,
                    retrieval_time = CURRENT_TIMESTAMP,
                    recipe_image = ?,
                    cooking_time_min = ?,
                    preparation_time_min = ?
                WHERE recipe_id = ?;
                """;
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, recipe.getName());
            ps.setString(2, recipe.getMethod());
            ps.setDouble(3, recipe.getCarbohydrates());
            ps.setDouble(4, recipe.getCalories());
            ps.setDouble(5, recipe.getProtein());
            ps.setDouble(6, recipe.getFat());
            ps.setString(7, recipe.getDescription());
            ps.setString(8, recipe.getImage());
            ps.setInt(9, recipe.getCookingTime());
            ps.setInt(10, recipe.getPreparationTime());
            ps.setInt(11, recipe.getId());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                try {
                    add(recipe);
                } catch (DuplicateEntryException e) {
                    LOG.error(e);
                }
            }
        } catch (SQLException e) {
            LOG.error(e);
        }
    }



}



