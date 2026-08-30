package seng202.team4.repository;


import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.Recipe;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public RecipeDAO(DatabaseManager databaseManager) { this.databaseManager = databaseManager; }

    /**
     * Method to return all recipes stored in the database
     * @return a list of Recipes, or an empty list if failed
     */
    @Override
    public List<Recipe> getAll() {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "EXECUTE get_recipe_info";
        try (Connection conn = databaseManager.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                String recipeIngredients = rs.getString("ingredients");
                List<String> ingredients = Arrays.asList(recipeIngredients.split(","));

                Timestamp timestamp = rs.getTimestamp("retrieval_time");
                LocalDateTime retrievalTime = retrieveTimestamp(timestamp);

                recipes.add(new Recipe(
                        rs.getInt("recipe_id"),
                        rs.getString("recipe_name"),
                        rs.getString("method"),
                        ingredients,
                        rs.getDouble("carbohydrates"),
                        rs.getDouble("calories"),
                        rs.getDouble("protein"),
                        rs.getDouble("fat"),
                        rs.getString("description"),
                        retrievalTime
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
     * @return recipe model that has the given id, null if failed
     */
    @Override
    public Recipe getOne(Integer id) {
        Recipe recipe = null;
        String sql = "EXECUTE get_recipe_info @id=?";
        try (Connection conn = databaseManager.connect();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String recipeIngredients = rs.getString("ingredients");
                    List<String> ingredients = Arrays.asList(recipeIngredients.split(","));
                    Timestamp timestamp = rs.getTimestamp("last_modified");
                    LocalDateTime retrievalTime = retrieveTimestamp(timestamp);

                    recipe = new Recipe(
                            rs.getInt("recipe_id"),
                            rs.getString("recipe_name"),
                            rs.getString("method"),
                            ingredients,
                            rs.getDouble("carbohydrate"),
                            rs.getDouble("calories"),
                            rs.getDouble("protein"),
                            rs.getDouble("fat"),
                            rs.getString("description"),
                            retrievalTime
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
    public Integer add(Recipe toAdd) {
        String sql = """
                INSERT INTO recipes (
                    recipe_id,
                    recipe_name,
                    recipe_method,
                    carbohydrates,
                    calories,
                    protein,
                    fat,
                    recipe_description
                    
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?);
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

            ps.executeUpdate();
            return toAdd.getId();
        } catch (SQLException sqlException) {
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
        String sql = "DELETE FROM recipe WHERE id=?";
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
    public List<Integer> getRecipeIdByFoodName(String food) {
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
}
