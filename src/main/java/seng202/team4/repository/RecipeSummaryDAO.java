package seng202.team4.repository;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.models.RecipeSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class RecipeSummaryDAO implements RecipeSummaryRepository {
    private static final Logger LOG = LogManager.getLogger(RecipeSummaryDAO.class);
    private static final String RECIPE_ID_REFERENCE = "recipe_id";
    private static final String RECIPE_IMAGE_REFERENCE = "recipe_image";
    private static final String RECIPE_NAME_REFERENCE = "recipe_name";
    private final DatabaseManager databaseManager;


    /**
     * Constructor
     * @param databaseManager means of connecting to the database
     */
    public RecipeSummaryDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public List<RecipeSummary> getAll() {
        throw new NotImplementedException();
    }

    @Override
    public RecipeSummary getOne(Integer id) {
        throw new NotImplementedException();
    }

    @Override
    public int add(RecipeSummary summary) throws DuplicateEntryException {
        String sql = """
                INSERT INTO recipe_summary (
                    recipe_id,
                    recipe_name,
                    recipe_image
                )
                VALUES (?, ?, ?);
                """;

        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, summary.getRecipeId());
            ps.setString(2, summary.getRecipeName());
            ps.setString(3, summary.getRecipeImageUrl());
            ps.executeUpdate();
            return summary.getRecipeId();
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                throw new DuplicateEntryException("Recipe summary exists with id " + summary.getRecipeId());
            }
            LOG.error(e);
            return -1;
        }
    }

    @Override
    public void delete(Integer id) {
        throw new NotImplementedException();
    }

    @Override
    public void update(RecipeSummary summary) {
        String sql = """
                UPDATE recipe_summary
                SET recipe_name = ?,
                    recipe_image = ?
                WHERE recipe_id = ?;
                """;
        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, summary.getRecipeName());
            ps.setString(2, summary.getRecipeImageUrl());
            ps.setInt(3, summary.getRecipeId());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.error(e);
        }
    }

    @Override
    public List<RecipeSummary> getRecipeSummariesByFoodName(String food) {
        List<RecipeSummary> summaries = new ArrayList<>();
        String sql = """
            SELECT rs.recipe_id,
                   rs.recipe_name,
                   rs.recipe_image
            FROM recipe_summary rs
            JOIN recipe_uses_food rf ON rs.recipe_id = rf.recipe_id
            WHERE rf.common_name = ?
              AND rf.retrieval_time >= DATETIME('now', '-24 hours')
            """;
        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, food);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    summaries.add(new RecipeSummary(
                            rs.getInt(RECIPE_ID_REFERENCE),
                            rs.getString(RECIPE_NAME_REFERENCE),
                            rs.getString(RECIPE_IMAGE_REFERENCE)
                    ));
                }
            }
        } catch (SQLException e) {
            LOG.error(e);
        }
        return summaries;
    }

    /**
     * Adds data about recipes using a specific food to the database table recipe uses food
     * @param food food to add
     * @param recipeId recipe id with the food in it
     */
    @Override
    public void addRecipeUseFood(String food, int recipeId) {
        String sql = """
            INSERT INTO recipe_uses_food (
                common_name,
                recipe_id,
                retrieval_time
            )
            VALUES (?, ?, CURRENT_TIMESTAMP)
            ON CONFLICT(common_name, recipe_id) DO UPDATE SET
                retrieval_time = CURRENT_TIMESTAMP;
            """;

        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, food);
            ps.setInt(2, recipeId);
            ps.executeUpdate();

        } catch (SQLException e) {
            LOG.error(e);
        }
    }
}
